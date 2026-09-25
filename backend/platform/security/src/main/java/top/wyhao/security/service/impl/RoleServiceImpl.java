
package top.wyhao.security.service.impl;

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
import top.wyhao.security.assembler.MenuAssembler;
import top.wyhao.security.entity.SysMenu;
import top.wyhao.security.entity.SysRole;
import top.wyhao.identity.domain.gateway.UserRepository;
import top.wyhao.security.entity.SysUserRole;
import top.wyhao.security.mapper.SysMenuMapper;
import top.wyhao.security.mapper.SysRoleMapper;
import top.wyhao.security.mapper.SysUserRoleMapper;
import top.wyhao.security.model.dto.RolePermissionUpdateRequest;
import top.wyhao.security.model.result.MenuVO;
import top.wyhao.security.service.RoleDeptService;
import top.wyhao.security.service.RoleMenuService;
import top.wyhao.security.service.RoleService;
import top.wyhao.cmn.db.query.QueryWrapperBuilder;
import top.wyhao.cmn.core.constant.CacheConstants;
import top.wyhao.cmn.core.enums.DataScopeEnum;
import top.wyhao.cmn.core.enums.RoleCodeEnum;
import top.wyhao.security.exception.RoleException;
import top.wyhao.cmn.core.util.CollUtils;
import top.wyhao.starter.excel.util.ExcelUtils;
import top.wyhao.starter.web.core.model.PageQuery;
import top.wyhao.cmn.db.query.PageResult;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import top.wyhao.security.model.vo.RoleDetailResult;
import top.wyhao.security.model.dto.RoleMemberQuery;
import top.wyhao.security.model.vo.RoleMemberResult;
import top.wyhao.security.model.dto.RoleQuery;
import top.wyhao.security.model.dto.RoleRequest;
import top.wyhao.security.model.vo.RoleResult;

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
    private final UserRepository userRepository;
    private final SysUserRoleMapper userRoleMapper;
    private final SysMenuMapper menuMapper;
    private final SysRoleMapper roleMapper;
    private final MenuAssembler menuAssembler;

    @Override
    public PageResult<RoleResult> page(RoleQuery query, PageQuery pageQuery) {
        IPage<SysRole> page = roleMapper.selectPage(new Page<>(pageQuery.getPage(), pageQuery.getPageSize()), QueryWrapperBuilder.build(query,SysRole.class));

        return PageResult.of(page).map(this::convertToRoleResp);
    }

    @Override
    public List<RoleResult> list(RoleQuery query) {
        List<SysRole> entities = roleMapper.selectList(QueryWrapperBuilder.build(query, SysRole.class));
        return entities.stream()
                .map(this::convertToRoleResp)
                .collect(Collectors.toList());
    }

    @Override
    public RoleDetailResult detail(Long id) {
        SysRole entity = roleMapper.selectById(id);
        if (entity == null) {
            throw RoleException.notFound();
        }
        RoleDetailResult detail = convertToRoleDetailResp(entity);
        detail.setMenuIds(roleMenuService.listMenuIdByRoleIds(List.of(detail.getId())));
        detail.setDeptIds(roleDeptService.listDeptIdByRoleId(detail.getId()));
        return detail;
    }

    @Override
    public Long create(RoleRequest req) {
        this.checkNameExists(req.getName(), null);
        String code = req.getCode();
        // 防止租户添加超级管理员
        if (ObjectUtil.equal(RoleCodeEnum.SUPER_ADMIN.getCode(), req.getCode())) {
            throw RoleException.codeForbidden(code);
        }
        // 新增信息
        SysRole entity = new SysRole();
        updateEntityFromReq(entity, req);
        int result = roleMapper.insert(entity);
        if (result <= 0) {
            throw RoleException.createFailed();
        }
        // 保存角色和部门关联
        roleDeptService.add(req.getDeptIds(), entity.getId());
        return entity.getId();
    }

    @Override
    public void update(RoleRequest req, Long id) {
        this.checkNameExists(req.getName(), id);
        SysRole oldRole = roleMapper.selectById(id);
        if (ObjectUtil.notEqual(req.getCode(), oldRole.getCode())) {
            throw RoleException.codeUpdateNotAllowed();
        }
        DataScopeEnum oldDataScope = oldRole.getDataScope();
        if (Boolean.TRUE.equals(oldRole.getIsBuiltin()) && ObjectUtil.notEqual(req.getDataScope(), oldDataScope)) {
            throw RoleException.builtinDataScopeUpdateNotAllowed(oldRole.getName());
        }
        // 更新信息
        SysRole entity = roleMapper.selectById(id);
        updateEntityFromReq(entity, req);
        int result = roleMapper.updateById(entity);
        if (result <= 0) {
            throw RoleException.updateFailed();
        }
        if (RoleCodeEnum.isSuperRoleCode(req.getCode())) {
            return;
        }
        // 保存角色和部门关联
        boolean isSaveDeptSuccess = roleDeptService.add(req.getDeptIds(), id);
        // 如果数据权限有变更，则更新在线用户权限信息
        if (isSaveDeptSuccess || ObjectUtil.notEqual(req.getDataScope(), oldDataScope)) {
            this.updateUserContext(id);
        }
    }

    private void checkNameExists(String name, Long id) {
        if (roleMapper.isNameExists(name, id)) {
            throw RoleException.nameAlreadyExists(name);
        }
    }

    @Override
    public void delete(Long id) {
        SysRole role = roleMapper.selectById(id);
        if (role == null) {
            throw RoleException.notFound();
        }
        if (role.getIsBuiltin()) {
            throw RoleException.builtinNotAllowedDelete(role.getName());
        }
        if (this.hasMember(id)) {
            throw RoleException.hasUserRelation();
        }

        // 删除角色和菜单关联
        roleMenuService.deleteByRoleId(id);
        // 删除角色和部门关联
        roleDeptService.deleteByRoleId(id);
        // 删除角色
        roleMapper.deleteById(id);
    }


    @Override
    public void export(RoleQuery query, HttpServletResponse response) {
        // 实现导出逻辑
        List<RoleResult> list = list(query);
        // 使用Excel工具导出数据到response
        ExcelUtils.export(list, "角色数据", RoleResult.class, response);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheInvalidate(key = "#roleId", name = CacheConstants.ROLE_MENU_KEY_PREFIX)
    public void updatePermission(Long roleId, RolePermissionUpdateRequest req) {
        SysRole role = roleMapper.selectById(roleId);
        if (Boolean.TRUE.equals(role.getIsBuiltin())) {
            throw RoleException.builtinPermissionUpdateNotAllowed(role.getName());
        }
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
        if (Boolean.TRUE.equals(role.getIsBuiltin())) {
            throw RoleException.builtinAssignNotAllowed(role.getName());
        }
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
        if (obj instanceof RoleDetailResult detail) {
            Long roleId = detail.getId();
            List<MenuVO> list = this.listMenuByRoleId(roleId);
            detail.setMenuIds(CollUtils.mapToList(list, MenuVO::getId));
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean assignRolesToUser(List<Long> newRoleIds, Long userId) {
        userRepository.findById(userId);
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
        List<MenuVO> list = menuAssembler.toVOList(menuList);
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
    public List<RoleMemberResult> pageMember(Long roleId, RoleMemberQuery query, PageQuery pageQuery) {
        QueryWrapper<SysUserRole> wrapper = Wrappers.query();
        wrapper.eq("role_id", roleId)
                .and(StrUtil.isNotBlank(query.getKeyword()),
                        w -> w.like("su.username", query.getKeyword())
                                .or().like("su.nickname", query.getKeyword()));
        IPage<SysUserRole> page = new Page<>(pageQuery.getPage(), pageQuery.getPageSize());
        return userRoleMapper.selectUserPage(page, wrapper).getRecords();
    }

    @Override
    public void deleteMember(Long roleId, List<Long> userIds) {
        SysRole role = roleMapper.selectById(roleId);
        if (role == null) {
            throw RoleException.notFound();
        }
        userRoleMapper.lambdaUpdate().eq(SysUserRole::getRoleId, roleId).in(SysUserRole::getUserId, userIds).remove();
    }

    @Override
    public void deleteUserRolesByUserIds(List<Long> userIds) {
        if (CollUtil.isEmpty(userIds)) {
            return;
        }
        userRoleMapper.lambdaUpdate().in(SysUserRole::getUserId, userIds).remove();
    }

    private RoleResult convertToRoleResp(SysRole entity) {
        return new RoleResult(
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

    private RoleDetailResult convertToRoleDetailResp(SysRole entity) {
        return new RoleDetailResult(
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

    private void updateEntityFromReq(SysRole entity, RoleRequest req) {
        entity.setName(req.getName());
        entity.setCode(req.getCode());
        entity.setDescription(req.getDescription());
        entity.setDataScope(req.getDataScope());
        if (entity.getId() == null) { // 创建时设置
            entity.setIsBuiltin(false);
            entity.setCreateTime(LocalDateTime.now());
        }
        entity.setUpdateTime(LocalDateTime.now());
    }
}