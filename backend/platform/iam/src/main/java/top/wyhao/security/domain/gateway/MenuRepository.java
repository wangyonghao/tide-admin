package top.wyhao.security.domain.gateway;

import top.wyhao.security.domain.model.SysMenu;

import java.util.List;

/**
 * 菜单仓储。
 */
public interface MenuRepository {

    List<SysMenu> listEnabledTree();

    List<SysMenu> listEnabledCatalogAndMenu();

    List<SysMenu> listEnabled();

    List<SysMenu> listByUserId(Long userId);

    List<SysMenu> listByRoleId(Long roleId);

    SysMenu findById(Long id);

    void insert(SysMenu menu);

    void updateById(SysMenu menu);

    void deleteByIds(List<Long> ids);

    boolean nameExists(String name, Long parentId, Long selfId);

    List<Long> listChildIds(List<Long> parentIds);

    List<String> listPermissionCodesByUserId(Long userId);

    long countIdGreaterThan(Long id);

    boolean deleteIdGreaterThan(Long id);
}
