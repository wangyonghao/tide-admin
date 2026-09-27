package top.wyhao.security.infrastructure.persistence;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import top.wyhao.cmn.core.util.CollUtils;
import top.wyhao.security.domain.gateway.RoleMenuRepository;
import top.wyhao.security.domain.model.SysRoleMenu;
import top.wyhao.security.infrastructure.persistence.mapper.SysRoleMenuMapper;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * 角色与菜单关联仓储实现。
 */
@Repository
@RequiredArgsConstructor
public class RoleMenuRepositoryImpl implements RoleMenuRepository {

    private final SysRoleMenuMapper roleMenuMapper;

    @Override
    public List<Long> listMenuIdsByRoleId(Long roleId) {
        return roleMenuMapper.lambdaQuery()
                .select(SysRoleMenu::getMenuId)
                .eq(SysRoleMenu::getRoleId, roleId)
                .list()
                .stream()
                .map(SysRoleMenu::getMenuId)
                .toList();
    }

    @Override
    public void deleteByRoleId(Long roleId) {
        roleMenuMapper.lambdaUpdate().in(SysRoleMenu::getRoleId, roleId).remove();
    }

    @Override
    public boolean insertBatch(List<SysRoleMenu> roleMenus) {
        return roleMenuMapper.insertBatch(roleMenus);
    }

    @Override
    public List<Long> listMenuIdsByRoleIds(List<Long> roleIds) {
        return roleMenuMapper.selectMenuIdByRoleIds(roleIds);
    }

    @Override
    public Set<Long> listRoleIdsNotInMenuIds(List<Long> menuIds) {
        List<SysRoleMenu> roleMenuList = roleMenuMapper.lambdaQuery()
                .select(SysRoleMenu::getRoleId)
                .notIn(SysRoleMenu::getMenuId, menuIds)
                .list();
        return CollUtils.mapToSet(roleMenuList, SysRoleMenu::getRoleId);
    }

    @Override
    public void deleteMenuIdNotIn(List<Long> menuIds) {
        roleMenuMapper.lambdaUpdate().notIn(SysRoleMenu::getMenuId, menuIds).remove();
    }

    @Override
    public void deleteAll() {
        roleMenuMapper.delete(Wrappers.<SysRoleMenu>query().eq("1", 1));
    }

    @Override
    public boolean deleteByRoleIdNotIn(Collection<Long> keepRoleIds) {
        return roleMenuMapper.lambdaUpdate().notIn(SysRoleMenu::getRoleId, keepRoleIds).remove();
    }
}
