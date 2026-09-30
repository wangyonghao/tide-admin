
package top.wyhao.security.app.service.impl;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.wyhao.security.app.assembler.MenuAssembler;
import top.wyhao.security.domain.gateway.MenuRepository;
import top.wyhao.security.domain.gateway.UserRoleRepository;
import top.wyhao.security.domain.model.SysMenu;
import top.wyhao.security.domain.exception.MenuException;
import top.wyhao.cmn.core.constant.SystemConstants;
import top.wyhao.security.domain.model.MenuType;
import top.wyhao.security.adapter.web.vo.MenuTreeVO;
import top.wyhao.security.adapter.web.vo.MenuVO;
import top.wyhao.security.app.service.MenuService;
import top.wyhao.redisson.util.RedisUtils;
import top.wyhao.cmn.core.constant.CacheConstants;
import top.wyhao.cmn.core.constant.StringConstants;
import top.wyhao.cmn.core.enums.RoleCodeEnum;
import top.wyhao.cmn.core.util.TreeUtils;
import java.util.ArrayList;
import java.util.List;
import top.wyhao.security.adapter.web.dto.MenuQuery;
import top.wyhao.security.adapter.web.dto.MenuRequest;

/**
 * 菜单 Service
 */
@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

    private final UserRoleRepository userRoleRepository;
    private final MenuRepository menuRepository;
    private final MenuAssembler menuAssembler;

    @Override
    public List<MenuTreeVO> tree(MenuQuery query) {
        return buildPermissionTree(menuRepository.listEnabledTree());
    }

    @Override
    public List<MenuTreeVO> getMenuTreeByUserId(Long userId) {
        // 获取用户的角色ID列表
        List<String> roleCodes = userRoleRepository.listRoleCodesByUserId(userId);
        if (roleCodes.contains(RoleCodeEnum.SUPER_ADMIN.getCode())) {
            return this.buildPermissionTree(menuRepository.listEnabledCatalogAndMenu());
        }
        List<SysMenu> menus = menuRepository.listByUserId(userId);
        return this.buildPermissionTree(menus);
    }

    @Override
    public List<MenuVO> listByRoleIds(List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return List.of();
        }

        // 如果包含超级管理员角色，则返回所有启用的菜单
        if (roleIds.contains(SystemConstants.SUPER_ADMIN_ROLE_ID)) {
            return menuAssembler.toVOList(menuRepository.listEnabled());
        }

        List<SysMenu> allMenus = new ArrayList<>();
        for (Long roleId : roleIds) {
            List<SysMenu> menusForRole = menuRepository.listByRoleId(roleId);
            allMenus.addAll(menusForRole);
        }

        // 去重并转换为响应对象
        return menuAssembler.toVOList(allMenus.stream().distinct().toList());
    }


    @Override
    public List<MenuVO> list(MenuQuery query) {
        return List.of();
    }

    @Override
    public void export(MenuQuery query, HttpServletResponse response) {

    }


    @Override
    public MenuVO get(Long id) {
        SysMenu sysMenu = menuRepository.findById(id);
        if (sysMenu == null) {
            throw MenuException.notFound();
        }
        return menuAssembler.toVO(sysMenu);
    }

    @Override
    public Long create(MenuRequest req) {
        this.checkNameUnique(req.getName(), req.getParentId(), null);

        // 目录类型菜单，默认为 Layout
        if (MenuType.DIR.equals(req.getType())) {
            req.setComponent(CharSequenceUtil.blankToDefault(req.getComponent(), "Layout"));
        }
        RedisUtils.deleteByPattern(CacheConstants.ROLE_MENU_KEY_PREFIX + StringConstants.ASTERISK);
        SysMenu menuDO = menuAssembler.toEntity(req);
        menuRepository.insert(menuDO);
        return menuDO.getId();
    }

    @Override
    public void update(Long id, MenuRequest req) {

        if(StrUtil.isNotBlank(req.getName())){
            this.checkNameUnique(req.getName(), req.getParentId(), id);
        }


        SysMenu entity = new SysMenu();
        menuAssembler.update(req, entity);
        entity.setId(id);
        menuRepository.updateById(entity);
        RedisUtils.deleteByPattern(CacheConstants.ROLE_MENU_KEY_PREFIX + StringConstants.ASTERISK);
    }

    /**
     * 删除菜单<br>
     *
     * @param id 菜单ID
     */
    @Override
    public void delete(Long id) {
        List<Long> pendingDeleteIds = this.listDescendantIds(List.of(id));
        menuRepository.deleteByIds(pendingDeleteIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(List<Long> ids) {
        // 级联删除菜单（包含子菜单）
        List<Long> allDeleteIdList = this.listDescendantIds(ids);
        menuRepository.deleteByIds(allDeleteIdList);
        RedisUtils.deleteByPattern(CacheConstants.ROLE_MENU_KEY_PREFIX + StringConstants.ASTERISK);
    }


    /**
     * 检查标题是否重复
     *
     * @param name    标题
     * @param parentId 上级 ID
     * @param selfId   ID
     */
    private void checkNameUnique(String name, Long parentId, Long selfId) {
        if (menuRepository.nameExists(name, parentId, selfId)) {
            throw MenuException.titleExist(name);
        }
    }

    /**
     * 级联获取所有待删除菜单 ID 列表（包含自身及所有子菜单）
     *
     * @param ids ID 列表
     * @return 待删除菜单 ID 列表（包含自身及所有子菜单）
     */
    private List<Long> listDescendantIds(List<Long> ids) {
        List<Long> menuIds = new ArrayList<>(ids);
        List<Long> childIdList = menuRepository.listChildIds(menuIds);
        if (childIdList.isEmpty()) {
            return menuIds;
        }
        menuIds.addAll(this.listDescendantIds(childIdList));
        return menuIds;
    }


    private List<MenuTreeVO> buildPermissionTree(List<SysMenu> menus) {
        List<MenuTreeVO> flat = menuAssembler.toTreeVOList(menus);
        return TreeUtils.flatToTree(flat,
                MenuTreeVO::getId,
                MenuTreeVO::getParentId,
                MenuTreeVO::getChildren,
                MenuTreeVO::setChildren);
    }


}
