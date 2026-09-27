package top.wyhao.security.app.service.impl;

import cn.hutool.core.collection.CollUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.wyhao.cmn.core.util.CollUtils;
import top.wyhao.security.app.service.RoleMenuService;
import top.wyhao.security.domain.gateway.RoleMenuRepository;
import top.wyhao.security.domain.model.SysRoleMenu;

import java.util.ArrayList;
import java.util.List;

/**
 * 角色和菜单业务实现
 */
@Service
@RequiredArgsConstructor
public class RoleMenuServiceImpl implements RoleMenuService {

    private final RoleMenuRepository roleMenuRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(List<Long> menuIds, Long roleId) {
        List<Long> oldMenuIdList = roleMenuRepository.listMenuIdsByRoleId(roleId);
        if (CollUtil.isEmpty(CollUtil.disjunction(menuIds, oldMenuIdList))) {
            return false;
        }
        roleMenuRepository.deleteByRoleId(roleId);
        List<SysRoleMenu> roleMenuList = CollUtils.mapToList(menuIds, menuId -> new SysRoleMenu(roleId, menuId));
        return roleMenuRepository.insertBatch(roleMenuList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByRoleId(Long roleId) {
        roleMenuRepository.deleteByRoleId(roleId);
    }

    @Override
    public List<Long> listMenuIdByRoleIds(List<Long> roleIds) {
        if (CollUtil.isEmpty(roleIds)) {
            return new ArrayList<>(0);
        }
        return roleMenuRepository.listMenuIdsByRoleIds(roleIds);
    }
}
