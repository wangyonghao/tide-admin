package top.wyhao.security.domain.gateway;

import top.wyhao.security.domain.model.SysRoleMenu;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * 角色与菜单关联仓储。
 */
public interface RoleMenuRepository {

    List<Long> listMenuIdsByRoleId(Long roleId);

    void deleteByRoleId(Long roleId);

    boolean insertBatch(List<SysRoleMenu> roleMenus);

    /**
     * 覆盖式保存角色菜单关联；无变更返回 false。
     */
    boolean replaceByRoleId(Long roleId, List<Long> menuIds);

    List<Long> listMenuIdsByRoleIds(List<Long> roleIds);

    Set<Long> listRoleIdsNotInMenuIds(List<Long> menuIds);

    void deleteMenuIdNotIn(List<Long> menuIds);

    void deleteAll();

    boolean deleteByRoleIdNotIn(Collection<Long> keepRoleIds);
}
