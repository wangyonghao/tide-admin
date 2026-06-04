
package top.wyhao.admin.system.service;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import top.wyhao.admin.system.model.MenuModel;
import top.wyhao.admin.system.model.result.MenuTreeVO;
import top.wyhao.admin.system.model.result.MenuVO;

import java.util.List;

/**
 * 菜单业务接口
 *

 * @since 2023/2/15 20:30
 */
public interface MenuService {

    List<MenuTreeVO> tree(@Valid MenuModel.MenuQuery query);

    MenuVO get(Long id);

    Long create(@Valid MenuModel.Request req);

    void update(Long id, @Valid MenuModel.Request req);

    void delete(Long id);

    void delete(List<Long> id);

    /**
     * 根据用户ID获取菜单树
     *
     * @param userId 用户ID
     * @return 菜单树
     */
    List<MenuTreeVO> getMenuTreeByUserId(Long userId);

    /**
     * 根据角色ID列表获取菜单列表
     *
     * @param roleIds 角色ID列表
     * @return 菜单列表
     */
    List<MenuVO> listByRoleIds(List<Long> roleIds);

    List<MenuVO> list(@Valid MenuModel.MenuQuery query);

    void export(@Valid MenuModel.MenuQuery query, HttpServletResponse response);


}
