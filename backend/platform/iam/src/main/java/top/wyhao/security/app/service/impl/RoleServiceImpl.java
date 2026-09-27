
package top.wyhao.security.app.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alicp.jetcache.anno.CacheInvalidate;
import com.alicp.jetcache.anno.Cached;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.wyhao.cmn.core.constant.CacheConstants;
import top.wyhao.cmn.core.enums.DataScopeEnum;
import top.wyhao.cmn.core.enums.RoleCodeEnum;
import top.wyhao.cmn.core.util.CollUtils;
import top.wyhao.cmn.db.query.PageResult;
import top.wyhao.security.adapter.web.dto.RoleMemberQuery;
import top.wyhao.security.adapter.web.dto.RolePermissionUpdateRequest;
import top.wyhao.security.adapter.web.dto.RoleQuery;
import top.wyhao.security.adapter.web.dto.RoleRequest;
import top.wyhao.security.adapter.web.vo.MenuVO;
import top.wyhao.security.adapter.web.vo.RoleDetailResult;
import top.wyhao.security.adapter.web.vo.RoleMemberResult;
import top.wyhao.security.adapter.web.vo.RoleResult;
import top.wyhao.security.app.assembler.MenuAssembler;
import top.wyhao.security.app.query.RoleViewQuery;
import top.wyhao.security.app.service.RoleService;
import top.wyhao.security.domain.exception.RoleException;
import top.wyhao.security.domain.gateway.*;
import top.wyhao.security.domain.model.SysMenu;
import top.wyhao.security.domain.model.SysRole;
import top.wyhao.security.domain.model.SysUserRole;
import top.wyhao.starter.excel.util.ExcelUtils;
import top.wyhao.starter.web.core.model.PageQuery;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色 Service
 */
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    /**
     * 超级管理员角色 ID（内置且仅有一位超级管理员用户）
     */
    public static final Long SUPERADMIN_ROLE_ID = 1L;

    private final RoleMenuRepository roleMenuRepository;
    private final RoleDeptRepository roleDeptRepository;
    private final UserRoleRepository userRoleRepository;
    private final MenuRepository menuRepository;
    private final RoleRepository roleRepository;
    private final RoleViewQuery roleViewQuery;
    private final MenuAssembler menuAssembler;

    @Override
    public PageResult<RoleResult> page(RoleQuery query, PageQuery pageQuery) {
        return roleViewQuery.page(query, pageQuery.getPage(), pageQuery.getPageSize()).map(this::convertToRoleResp);
    }

    @Override
    public List<RoleResult> list(RoleQuery query) {
        List<SysRole> entities = roleViewQuery.list(query);
        return entities.stream()
                .map(this::convertToRoleResp)
                .collect(Collectors.toList());
    }

    @Override
    public RoleDetailResult detail(Long id) {
        SysRole entity = roleRepository.findById(id);
        if (entity == null) {
            throw RoleException.notFound();
        }
        RoleDetailResult detail = convertToRoleDetailResp(entity);
        detail.setMenuIds(roleMenuRepository.listMenuIdsByRoleIds(List.of(detail.getId())));
        detail.setDeptIds(roleDeptRepository.listDeptIdsByRoleId(detail.getId()));
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
        int result = roleRepository.insert(entity);
        if (result <= 0) {
            throw RoleException.createFailed();
        }
        // 保存角色和部门关联
        roleDeptRepository.replaceByRoleId(entity.getId(), req.getDeptIds());
        return entity.getId();
    }

    @Override
    public void update(RoleRequest req, Long id) {
        this.checkNameExists(req.getName(), id);
        SysRole oldRole = roleRepository.findById(id);
        if (ObjectUtil.notEqual(req.getCode(), oldRole.getCode())) {
            throw RoleException.codeUpdateNotAllowed();
        }
        DataScopeEnum oldDataScope = oldRole.getDataScope();
        if (Boolean.TRUE.equals(oldRole.getIsBuiltin()) && ObjectUtil.notEqual(req.getDataScope(), oldDataScope)) {
            throw RoleException.builtinDataScopeUpdateNotAllowed(oldRole.getName());
        }
        // 更新信息
        SysRole entity = roleRepository.findById(id);
        updateEntityFromReq(entity, req);
        int result = roleRepository.updateById(entity);
        if (result <= 0) {
            throw RoleException.updateFailed();
        }
        if (RoleCodeEnum.isSuperRoleCode(req.getCode())) {
            return;
        }
        // 保存角色和部门关联
        boolean isSaveDeptSuccess = roleDeptRepository.replaceByRoleId(id, req.getDeptIds());
        // 如果数据权限有变更，则更新在线用户权限信息
        if (isSaveDeptSuccess || ObjectUtil.notEqual(req.getDataScope(), oldDataScope)) {
            this.updateUserContext(id);
        }
    }

    private void checkNameExists(String name, Long id) {
        if (roleRepository.nameExists(name, id)) {
            throw RoleException.nameAlreadyExists(name);
        }
    }

    public SysRole requireByid(Long id) {
        SysRole role = roleRepository.findById(id);
        if (role == null) {
            throw RoleException.notFound();
        }
        return role;
    }

    @Override
    public void delete(Long id) {
        SysRole role = requireByid(id);
        if (role.getIsBuiltin()) {
            throw RoleException.builtinNotAllowedDelete(role.getName());
        }
        if (this.hasMember(id)) {
            throw RoleException.hasUserRelation();
        }

        // 删除角色和菜单关联
        roleMenuRepository.deleteByRoleId(id);
        // 删除角色和部门关联
        roleDeptRepository.deleteByRoleId(id);
        // 删除角色
        roleRepository.deleteById(id);
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
        SysRole role = roleRepository.findById(roleId);
        if (Boolean.TRUE.equals(role.getIsBuiltin())) {
            throw RoleException.builtinPermissionUpdateNotAllowed(role.getName());
        }
        // 保存角色和菜单关联
        roleMenuRepository.replaceByRoleId(roleId, req.getMenuIds());
        roleRepository.updateMenuCheckStrictly(roleId, req.getMenuCheckStrictly());
    }

    @Override
    public void assignToUsers(Long roleId, List<Long> userIds) {
        SysRole role = roleRepository.findById(roleId);
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
        userRoleRepository.insertBatch(userRoleList);
    }

    @Override
    public void updateUserContext(Long roleId) {
        List<Long> userIdList = this.listMemberIds(roleId);
        // 更新登录用户的权限信息
    }

    @Override
    public Long getIdByCode(String code) {
        return roleRepository.findIdByCode(code);
    }

    @Override
    public List<SysRole> listByNames(List<String> list) {
        if (CollUtil.isEmpty(list)) {
            return Collections.emptyList();
        }
        return roleRepository.listByNames(list);
    }

    @Override
    public int countByNames(List<String> roleNames) {
        if (CollUtil.isEmpty(roleNames)) {
            return 0;
        }
        return roleRepository.countByNames(roleNames);
    }

    private boolean hasMember(Long roleId) {
        return userRoleRepository.existsByRoleId(roleId);
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
        List<Long> roleIds = CollUtil.emptyIfNull(newRoleIds);
        List<Long> oldRoleIds = this.findRoleIdsByUserId(userId);
        if (CollUtil.isEmpty(CollUtil.disjunction(roleIds, oldRoleIds))) {
            return false;
        }
        userRoleRepository.deleteByUserId(userId);
        if (CollUtil.isEmpty(roleIds)) {
            return true;
        }
        List<SysUserRole> userRoleList = CollUtils.mapToList(roleIds, roleId -> new SysUserRole(userId, roleId));
        return userRoleRepository.insertBatch(userRoleList);
    }

    @Override
    public List<Long> findRoleIdsByUserId(Long userId) {
        return CollUtils.mapToList(userRoleRepository.listByUserId(userId), SysUserRole::getRoleId);
    }

    @Override
    @Cached(key = "#roleId", name = CacheConstants.ROLE_MENU_KEY_PREFIX)
    public List<MenuVO> listMenuByRoleId(Long roleId) {
        List<SysMenu> menuList;
        if (SUPERADMIN_ROLE_ID.equals(roleId)) {
            menuList = menuRepository.listEnabled();
        } else {
            menuList = menuRepository.listByRoleId(roleId);
        }
        List<MenuVO> list = menuAssembler.toVOList(menuList);
        list.forEach(this::fill);
        return list;
    }

    @Override
    public List<Long> listMemberIds(Long roleId) {
        return userRoleRepository.listUserIdsByRoleId(roleId);
    }

    @Override
    public List<RoleMemberResult> pageMember(Long roleId, RoleMemberQuery query, PageQuery pageQuery) {
        return roleViewQuery.pageMembers(roleId, query, pageQuery.getPage(), pageQuery.getPageSize());
    }

    @Override
    public void deleteMember(Long roleId, List<Long> userIds) {
        SysRole role = roleRepository.findById(roleId);
        if (role == null) {
            throw RoleException.notFound();
        }
        userRoleRepository.deleteByRoleIdAndUserIds(roleId, userIds);
    }

    @Override
    public void deleteUserRolesByUserIds(List<Long> userIds) {
        if (CollUtil.isEmpty(userIds)) {
            return;
        }
        userRoleRepository.deleteByUserIds(userIds);
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