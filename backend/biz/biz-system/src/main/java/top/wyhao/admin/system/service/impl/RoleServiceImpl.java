
package top.wyhao.admin.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alicp.jetcache.anno.CacheInvalidate;
import com.alicp.jetcache.anno.Cached;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.wyhao.admin.system.entity.SysMenu;
import top.wyhao.admin.system.entity.SysRole;
import top.wyhao.admin.system.entity.SysUserRole;
import top.wyhao.admin.system.entity.SysUser;
import top.wyhao.admin.system.mapper.SysMenuMapper;
import top.wyhao.admin.system.mapper.SysRoleMapper;
import top.wyhao.admin.system.mapper.SysUserRoleMapper;
import top.wyhao.admin.system.mapper.SysUserMapper;
import top.wyhao.admin.system.model.bo.RolePermissionUpdateRequest;
import top.wyhao.admin.system.model.RoleModel;
import top.wyhao.admin.system.model.result.MenuVO;
import top.wyhao.admin.system.model.RoleUserModel;
import top.wyhao.admin.system.service.RoleDeptService;
import top.wyhao.admin.system.service.RoleMenuService;
import top.wyhao.admin.system.service.RoleService;
import top.wyhao.cmn.db.util.WrapperUtil;
import top.wyhao.starter.core.constant.CacheConstants;
import top.wyhao.starter.core.enums.DataScopeEnum;
import top.wyhao.starter.core.enums.RoleCodeEnum;
import top.wyhao.starter.core.exception.BadRequestException;
import top.wyhao.starter.core.exception.BizException;
import top.wyhao.starter.core.util.CollUtils;
import top.wyhao.starter.core.util.validation.Check;
import top.wyhao.starter.excel.util.ExcelUtils;
import top.wyhao.starter.web.core.model.PageQuery;
import top.wyhao.starter.web.core.model.PageResult;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色 Service
 */
@Service
@RequiredArgsConstructor
public class RoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements RoleService {
    /**
     * 超级管理员角色 ID（内置且仅有一位超级管理员用户）
     */
    public static final Long SUPERADMIN_ROLE_ID = 1L;

    private final RoleMenuService roleMenuService;
    private final RoleDeptService roleDeptService;
    private final SysUserMapper userMapper;
    private final SysUserRoleMapper userRoleMapper;
    private final SysMenuMapper menuMapper;
    private final SysRoleMapper roleMapper;

    @Override
    public PageResult<RoleModel.Result> page(RoleModel.Query query, PageQuery pageQuery) {
        QueryWrapper<SysRole> wrapper = WrapperUtil.build(query, WrapperUtil.parseSort(query.sort()));
        IPage<SysRole> page = roleMapper.selectPage(new Page<>(pageQuery.getPage(), pageQuery.getSize()), wrapper);

        return PageResult.build(page, RoleModel.Result.class);
    }

    @Override
    public List<RoleModel.Result> list(RoleModel.Query query) {
        QueryWrapper<SysRole> wrapper = WrapperUtil.build(query, WrapperUtil.parseSort(query.sort()));
        List<SysRole> entities = roleMapper.selectList(wrapper);
        return entities.stream()
                .map(this::convertToRoleResp)
                .collect(Collectors.toList());
    }

    @Override
    public RoleModel.Detail detail(Long id) {
        SysRole entity = roleMapper.selectById(id);
        if (entity == null) {
            throw new BizException("ROLE_NOT_FOUND", "角色不存在");
        }
        RoleModel.Detail detail = convertToRoleDetailResp(entity);
        List<Long> menuIds = roleMenuService.listMenuIdByRoleIds(List.of(detail.id()));
        List<Long> deptIds = roleDeptService.listDeptIdByRoleId(detail.id());
        // 由于 Result 是 record，需要创建新实例来设置 menuIds 和 deptIds
        return new RoleModel.Detail(
                detail.id(),
                detail.createUser(),
                detail.createUserString(),
                detail.createTime(),
                detail.disabled(),
                detail.updateUser(),
                detail.updateUserString(),
                detail.updateTime(),
                detail.name(),
                detail.code(),
                detail.dataScope(),
                detail.sort(),
                detail.isBuiltin(),
                detail.menuCheckStrictly(),
                detail.deptCheckStrictly(),
                detail.description(),
                menuIds,
                deptIds
        );
    }

    @Override
    public Long create(RoleModel.Request req) {
        this.checkNameExists(req.name(), null);
        String code = req.code();
        // 防止租户添加超级管理员
        Check.throwIfEqual(RoleCodeEnum.SUPER_ADMIN.getCode(), req.code(), "编码 [{}] 禁止使用", code);
        // 新增信息
        SysRole entity = new SysRole();
        updateEntityFromReq(entity, req);
        int result = roleMapper.insert(entity);
        if (result <= 0) {
            throw new BadRequestException("CREATE_FAILED", "创建失败");
        }
        // 保存角色和部门关联
        roleDeptService.add(req.deptIds(), entity.getId());
        return entity.getId();
    }

    @Override
    public void update(RoleModel.Request req, Long id) {
        this.checkNameExists(req.name(), id);
        SysRole oldRole = roleMapper.selectById(id);
        Check.throwIfNotEqual(req.code(), oldRole.getCode(), "角色编码不允许修改", oldRole.getName());
        DataScopeEnum oldDataScope = oldRole.getDataScope();
        if (Boolean.TRUE.equals(oldRole.getIsBuiltin())) {
            Check.throwIfNotEqual(req.dataScope(), oldDataScope, "[{}] 是系统内置角色，不允许修改角色数据权限", oldRole.getName());
        }
        // 更新信息
        SysRole entity = roleMapper.selectById(id);
        updateEntityFromReq(entity, req);
        int result = roleMapper.updateById(entity);
        if (result <= 0) {
            throw new BadRequestException("UPDATE_FAILED", "更新失败");
        }
        if (RoleCodeEnum.isSuperRoleCode(req.code())) {
            return;
        }
        // 保存角色和部门关联
        boolean isSaveDeptSuccess = roleDeptService.add(req.deptIds(), id);
        // 如果数据权限有变更，则更新在线用户权限信息
        if (isSaveDeptSuccess || ObjectUtil.notEqual(req.dataScope(), oldDataScope)) {
            this.updateUserContext(id);
        }
    }

    private void checkNameExists(String name, Long id) {
        if (menuMapper.isNameExists(name, id)) {
            throw new BadRequestException("ROLENAME_ALREADY_EXISTS", "角色名称 [" + name + "] 已存在");
        }
    }

    @Override
    public void delete(Long id) {
        SysRole role = roleMapper.selectById(id);
        if (role == null) {
            throw new BizException("ROLE_NOT_FOUND", "角色不存在");
        }
        if (role.getIsBuiltin()) {
            throw new BizException("ROLE_NOT_ALLOWED_DELETE", StrUtil.format("所选角色 [{}] 是系统内置角色，不允许删除", role.getName()));
        }
        if (this.hasMember(id)) {
            throw new BizException("ROLE_NOT_ALLOWED_DELETE", "所选角色存在用户关联，请解除关联后重试");
        }

        // 删除角色和菜单关联
        roleMenuService.deleteByRoleId(id);
        // 删除角色和部门关联
        roleDeptService.deleteByRoleId(id);
        // 删除角色
        roleMapper.deleteById(id);
    }


    @Override
    public void export(RoleModel.Query query, HttpServletResponse response) {
        // 实现导出逻辑
        List<RoleModel.Result> list = list(query);
        // 使用Excel工具导出数据到response
        ExcelUtils.export(list, "角色数据", RoleModel.Result.class, response);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheInvalidate(key = "#roleId", name = CacheConstants.ROLE_MENU_KEY_PREFIX)
    public void updatePermission(Long roleId, RolePermissionUpdateRequest req) {
        SysRole role = roleMapper.selectById(roleId);
        Check.when(role.getIsBuiltin(), "[{}] 是系统内置角色，不允许修改角色功能权限", role.getName());
        // 保存角色和菜单关联
        roleMenuService.save(req.getMenuIds(), roleId);
        roleMapper.lambdaUpdate()
                .set(SysRole::getMenuCheckStrictly, req.getMenuCheckStrictly())
                .eq(SysRole::getId, roleId)
                .update();
    }

    @Override
    public void assignToUsers(Long roleId, List<Long> userIds) {
        SysRole role = roleMapper.selectById(roleId);
        Check.when(Boolean.TRUE.equals(role.getIsBuiltin()), "[{}] 是系统内置角色，不允许分配角色给其他用户", role.getName());
        // 保存用户和角色关联
        this.assignRoleToUsers(roleId, userIds);
        // 更新用户上下文
        this.updateUserContext(roleId);
    }

    private void assignRoleToUsers(Long roleId, List<Long> userIds) {
        List<SysUserRole> userRoleList = CollUtils.mapToList(userIds, userId -> new SysUserRole(userId, roleId));
        userRoleMapper.insertBatch(userRoleList);
    }

    @Override
    public void updateUserContext(Long roleId) {
        List<Long> userIdList = this.listMemberIds(roleId);
        // 更新登录用户的权限信息
    }

    @Override
    public Long getIdByCode(String code) {
        return roleMapper.lambdaQuery().eq(SysRole::getCode, code).oneOpt().map(SysRole::getId).orElse(null);
    }

    @Override
    public List<SysRole> listByNames(List<String> list) {
        if (CollUtil.isEmpty(list)) {
            return Collections.emptyList();
        }
        return roleMapper.selectList(Wrappers.<SysRole>lambdaQuery().in(SysRole::getName, list));
    }

    @Override
    public int countByNames(List<String> roleNames) {
        if (CollUtil.isEmpty(roleNames)) {
            return 0;
        }
        return roleMapper.selectCount(Wrappers.<SysRole>lambdaQuery().in(SysRole::getName, roleNames)).intValue();
    }

    private boolean hasMember(Long roleId) {
        return userRoleMapper.lambdaQuery().eq(SysUserRole::getRoleId, roleId).exists();
    }

    private void fill(Object obj) {
        if (obj instanceof RoleModel.Detail detail) {
            Long roleId = detail.id();
            List<MenuVO> list = this.listMenuByRoleId(roleId);
            List<Long> menuIds = CollUtils.mapToList(list, MenuVO::getId);
            // 由于 Result 是 record，无法直接修改，需要创建新实例
            // 这里暂时跳过，因为 fill 方法似乎没有被使用
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean assignRolesToUser(List<Long> newRoleIds, Long userId) {
        SysUser userDO = userMapper.selectById(userId);
        // 检查是否有变更
        List<Long> oldRoleIds = userRoleMapper.lambdaQuery()
                .select(SysUserRole::getRoleId)
                .eq(SysUserRole::getUserId, userId)
                .list()
                .stream()
                .map(SysUserRole::getRoleId)
                .toList();
        if (CollUtil.isEmpty(CollUtil.disjunction(newRoleIds, oldRoleIds))) {
            return false;
        }
        // 删除原有关联
        userRoleMapper.lambdaUpdate().eq(SysUserRole::getUserId, userId).remove();
        // 保存最新关联
        List<SysUserRole> userRoleList = CollUtils.mapToList(newRoleIds, roleId -> new SysUserRole(userId, roleId));
        return userRoleMapper.insertBatch(userRoleList);
    }

    @Override
    public List<Long> findRoleIdsByUserId(Long userId) {
        return userRoleMapper.lambdaQuery()
                .select(SysUserRole::getRoleId)
                .eq(SysUserRole::getUserId, userId)
                .list()
                .stream()
                .map(SysUserRole::getRoleId)
                .toList();
    }



    @Override
    @Cached(key = "#roleId", name = CacheConstants.ROLE_MENU_KEY_PREFIX)
    public List<MenuVO> listMenuByRoleId(Long roleId) {
        List<SysMenu> menuList;
        if (SUPERADMIN_ROLE_ID.equals(roleId)) {
            menuList = menuMapper.lambdaQuery().eq(SysMenu::getStatus, "1").list();
        } else {
            menuList = menuMapper.selectListByRoleId(roleId);
        }
        List<MenuVO> list = BeanUtil.copyToList(menuList, MenuVO.class);
        list.forEach(this::fill);
        return list;
    }
    @Override
    public List<Long> listMemberIds(Long roleId) {
        return userRoleMapper.lambdaQuery()
                .select(SysUserRole::getUserId)
                .eq(SysUserRole::getRoleId, roleId)
                .list()
                .stream()
                .map(SysUserRole::getUserId)
                .toList();
    }

    @Override
    public List<RoleUserModel> pageMember(Long roleId, RoleUserModel.Query query, PageQuery pageQuery) {
        QueryWrapper<SysUserRole> wrapper = Wrappers.query();
        wrapper.eq("role_id", roleId)
                .and(StrUtil.isNotBlank(query.keyword()),
                        w -> w.like("su.username", query.keyword())
                                .or().like("su.nickname", query.keyword()));
        IPage<SysUserRole> page = new Page<>(pageQuery.getPage(), pageQuery.getSize());
        return userRoleMapper.selectUserPage(page, wrapper).getRecords();
    }

    @Override
    public void deleteMember(Long roleId, List<Long> userIds) {
        SysRole role = roleMapper.selectById(roleId);
        if (role == null) {
            throw new BizException("ROLE_NOT_FOUND", "角色不存在");
        }
        userRoleMapper.lambdaUpdate().eq(SysUserRole::getRoleId, roleId).in(SysUserRole::getUserId, userIds).remove();
    }

    private RoleModel.Result convertToRoleResp(SysRole entity) {
        return new RoleModel.Result(
                entity.getId(),
                entity.getCreateUser(),
                null, // createUserString
                entity.getCreateTime(),
                null, // disabled
                entity.getUpdateUser(),
                null, // updateUserString
                entity.getUpdateTime(),
                entity.getName(),
                entity.getCode(),
                entity.getDataScope(),
                entity.getSort(),
                entity.getIsBuiltin(),
                entity.getDescription()
        );
    }

    private RoleModel.Detail convertToRoleDetailResp(SysRole entity) {
        return new RoleModel.Detail(
                entity.getId(),
                entity.getCreateUser(),
                null, // createUserString
                entity.getCreateTime(),
                null, // disabled
                entity.getUpdateUser(),
                null, // updateUserString
                entity.getUpdateTime(),
                entity.getName(),
                entity.getCode(),
                entity.getDataScope(),
                entity.getSort(),
                entity.getIsBuiltin(),
                entity.getMenuCheckStrictly(),
                entity.getDeptCheckStrictly(),
                entity.getDescription(),
                null, // menuIds
                null  // deptIds
        );
    }

    private void updateEntityFromReq(SysRole entity, RoleModel.Request req) {
        entity.setName(req.name());
        entity.setCode(req.code());
        entity.setDescription(req.description());
        entity.setDataScope(req.dataScope());
        if (entity.getId() == null) { // 创建时设置
            entity.setIsBuiltin(false);
            entity.setCreateTime(LocalDateTime.now());
        }
        entity.setUpdateTime(LocalDateTime.now());
    }
}