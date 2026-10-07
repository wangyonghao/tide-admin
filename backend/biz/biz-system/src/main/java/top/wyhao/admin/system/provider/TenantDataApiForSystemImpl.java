
package top.wyhao.admin.system.provider;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.text.CharSequenceUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.wyhao.audit.mapper.SysOperationLogMapper;
import top.wyhao.cmn.core.enums.DataScopeEnum;
import top.wyhao.cmn.core.enums.GenderEnum;
import top.wyhao.cmn.core.enums.RoleCodeEnum;
import top.wyhao.cmn.core.enums.StatusEnum;
import top.wyhao.cmn.core.util.ExceptionUtils;
import top.wyhao.cmn.core.util.RsaUtils;
import top.wyhao.identity.domain.exception.UserException;
import top.wyhao.identity.domain.gateway.UserRepository;
import top.wyhao.identity.domain.gateway.UserSocialRepository;
import top.wyhao.identity.domain.model.SysUser;
import top.wyhao.notification.mapper.SysMessageMapper;
import top.wyhao.notification.mapper.SysNoticeMapper;
import top.wyhao.department.domain.gateway.DepartmentRepository;
import top.wyhao.department.domain.model.SysDepartment;
import top.wyhao.security.app.service.RoleService;
import top.wyhao.security.domain.gateway.RoleDepartmentRepository;
import top.wyhao.security.domain.gateway.RoleMenuRepository;
import top.wyhao.security.domain.gateway.RoleRepository;
import top.wyhao.security.domain.gateway.UserRoleRepository;
import top.wyhao.security.domain.model.SysRole;
import top.wyhao.tenant.client.PackageMenuApi;
import top.wyhao.tenant.client.TenantApi;
import top.wyhao.tenant.client.TenantBO;
import top.wyhao.tenant.client.TenantDataApi;
import top.wyhao.tenant.util.TenantUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 租户数据 API 实现
 *
 * @since 2024/12/2 20:12
 */
@Service
@RequiredArgsConstructor
public class TenantDataApiForSystemImpl implements TenantDataApi {

    private final PackageMenuApi packageMenuApi;
    private final TenantApi tenantApi;
    private final DepartmentRepository departmentRepository;
    private final RoleRepository roleRepository;
    private final RoleMenuRepository roleMenuRepository;
    private final SysOperationLogMapper operationLogMapper;
    private final SysMessageMapper messageMapper;
    private final SysMessageMapper messageUserMapper;
    private final SysNoticeMapper noticeMapper;
    private final RoleDepartmentRepository roleDepartmentRepository;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final UserSocialRepository userSocialRepository;
    private final RoleService roleService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void init(TenantBO tenant) {
        Long tenantId = tenant.getId();
        TenantUtils.execute(tenantId, () -> {
            // 初始化部门
            Long departmentId = this.initDepartmentData(tenant);
            // 初始化角色
            Long roleId = this.initRoleData(tenant);
            // 角色绑定菜单
            List<Long> menuIds = packageMenuApi.listMenuIdsByPackageId(tenant.getPackageId());
            roleMenuRepository.replaceByRoleId(roleId, menuIds);
            // 初始化管理用户
            Long userId = this.initUserData(tenant, departmentId);
            // 用户绑定角色
            roleService.assignToUsers(roleId, ListUtil.of(userId));
            // 租户绑定用户
            tenantApi.bindAdminUser(tenantId, userId);
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void clear() {
        // 退出所有用户
        List<SysUser> userList = userRepository.listAll();
        for (SysUser user : userList) {
            StpUtil.logout(user.getId());
        }
        Wrapper queryWrapper = Wrappers.query().eq("1", 1);
        // 部门清除
        departmentRepository.deleteAll();
//        // 文件清除
//        List<Long> fileIds = CollUtils.mapToList(fileService.list(), FileDO::getId);
//        if (!fileIds.isEmpty()) {
//            fileService.delete(fileIds);
//        }
        // 日志清除
        operationLogMapper.delete(queryWrapper);
        // 消息清除
        messageMapper.delete(queryWrapper);
        messageUserMapper.delete(queryWrapper);
        // 通知清除
        noticeMapper.delete(queryWrapper);
        // 角色相关数据清除
        roleRepository.deleteAll();
        roleDepartmentRepository.deleteAll();
        roleMenuRepository.deleteAll();
        // 用户数据清除
        userRepository.deleteAll();
        userRoleRepository.deleteAll();
        userSocialRepository.deleteAll();
    }

    public static final Long ROOT_PARENT_ID = 0L;
    /**
     * 初始化部门数据
     *
     * @param tenant 租户信息
     * @return 部门 ID
     */
    private Long initDepartmentData(TenantBO tenant) {
        SysDepartment department = new SysDepartment();
        department.setName(tenant.getName());
        department.setCode("000000"); // todo
        department.setType(1); // todo
        department.setParentId(ROOT_PARENT_ID);
        department.setAncestors(ROOT_PARENT_ID.toString());
        department.setDescription("系统初始部门");
        department.setSort(1);
        department.setStatus(StatusEnum.ENABLE.getValue());
        department.setIsBuiltin(true);
        departmentRepository.insert(department);
        return department.getId();
    }

    /**
     * 初始化角色数据
     *
     * @param tenant 租户信息
     * @return 角色 ID
     */
    private Long initRoleData(TenantBO tenant) {
        SysRole role = new SysRole();
        RoleCodeEnum tenantAdmin = RoleCodeEnum.TENANT_ADMIN;
        role.setName(tenantAdmin.getDescription());
        role.setCode(tenantAdmin.getCode());
        role.setDataScope(DataScopeEnum.ALL);
        role.setDescription("系统初始角色");
        role.setSort(1);
        role.setIsBuiltin(true);
        role.setMenuCheckStrictly(true);
        role.setDepartmentCheckStrictly(true);
        roleRepository.insert(role);
        return role.getId();
    }

    /**
     * 初始化用户数据
     *
     * @param tenant 租户信息
     * @param departmentId 部门 ID
     * @return 用户 ID
     */
    private Long initUserData(TenantBO tenant, Long departmentId) {
        // 解密密码
        String rawPassword = ExceptionUtils.exToNull(() -> RsaUtils.decryptByRsaPrivateKey(tenant.getAdminPassword()));
        if (CharSequenceUtil.isBlank(rawPassword)) {
            throw UserException.passwordDecryptFailed();
        }
        // 初始化用户
        SysUser user = new SysUser();
        user.setUsername(tenant.getAdminUsername());
        user.setDisplayName(RoleCodeEnum.TENANT_ADMIN.getDescription());
        user.setPassword(rawPassword);
        user.setGender(GenderEnum.UNKNOWN.getValue());
        user.setDescription("系统初始用户");
        user.setStatus(StatusEnum.ENABLE.getValue());
        user.setIsBuiltin(true);
        user.setPwdUpdateTime(LocalDateTime.now());
        user.setDepartmentId(departmentId);
        userRepository.insert(user);
        return user.getId();
    }
}
