
package top.wyhao.security.service.impl;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.wyhao.security.assembler.MenuAssembler;
import top.wyhao.security.entity.SysMenu;
import top.wyhao.security.exception.MenuException;
import top.wyhao.security.mapper.SysMenuMapper;
import top.wyhao.cmn.core.constant.SystemConstants;
import top.wyhao.security.model.enums.MenuType;
import top.wyhao.security.model.result.MenuTreeVO;
import top.wyhao.security.model.result.MenuVO;
import top.wyhao.security.service.MenuService;
import top.wyhao.identity.app.service.UserService;
import top.wyhao.starter.cache.redisson.util.RedisUtils;
import top.wyhao.cmn.core.constant.CacheConstants;
import top.wyhao.cmn.core.constant.StringConstants;
import top.wyhao.cmn.core.enums.RoleCodeEnum;
import top.wyhao.cmn.core.enums.StatusEnum;
import top.wyhao.cmn.core.util.TreeUtils;
import java.util.ArrayList;
import java.util.List;
import top.wyhao.security.model.dto.MenuQuery;
import top.wyhao.security.model.dto.MenuRequest;

/**
 * 菜单 Service
 */
@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

    private final UserService userService;
    private final SysMenuMapper menuMapper;
    private final MenuAssembler menuAssembler;

    @Override
    public List<MenuTreeVO> tree(MenuQuery query) {
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenu::getStatus, StatusEnum.ENABLE.getValue())
                .orderByAsc(SysMenu::getParentId)
                .orderByAsc(SysMenu::getSort);
        List<SysMenu> menus = menuMapper.selectList(wrapper);
        return buildPermissionTree(menus);
    }

    @Override
    public List<MenuTreeVO> getMenuTreeByUserId(Long userId) {
        // 获取用户的角色ID列表
        List<String> roleCodes = userService.findUserRoles(userId);
        // 超级管理员
        if (roleCodes.contains(RoleCodeEnum.SUPER_ADMIN.getCode())) {
            LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysMenu::getStatus, 1)
                    .in(SysMenu::getType, 1, 2)
                    .orderByAsc(SysMenu::getParentId)
                    .orderByAsc(SysMenu::getSort);
            List<SysMenu> menus = menuMapper.selectList(wrapper);
            return this.buildPermissionTree(menus);
        }
        // 普通用户
        List<SysMenu> menus = menuMapper.selectMenusByUserId(userId);
        return this.buildPermissionTree(menus);
    }

    @Override
    public List<MenuVO> listByRoleIds(List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return List.of();
        }

        // 如果包含超级管理员角色，则返回所有启用的菜单
        if (roleIds.contains(SystemConstants.SUPER_ADMIN_ROLE_ID)) {
            List<SysMenu> menuList = menuMapper.lambdaQuery()
                    .eq(SysMenu::getStatus, "1")
                    .list();
            return menuAssembler.toVOList(menuList);
        }

        // 否则根据角色ID列表获取菜单 - 遍历每个角色ID并合并菜单列表
        List<SysMenu> allMenus = new ArrayList<>();
        for (Long roleId : roleIds) {
            List<SysMenu> menusForRole = menuMapper.selectListByRoleId(roleId);
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
        SysMenu sysMenu = menuMapper.selectById(id);
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
        menuMapper.insert(menuDO);
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
        menuMapper.updateById(entity);
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
        menuMapper.deleteByIds(pendingDeleteIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(List<Long> ids) {
        // 级联删除菜单（包含子菜单）
        List<Long> allDeleteIdList = this.listDescendantIds(ids);
        menuMapper.deleteByIds(allDeleteIdList);
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
        if(menuMapper.isNameExists(name, parentId, selfId)){
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
        List<Long> childIdList = menuMapper.lambdaQuery()
                .select(SysMenu::getId)
                .in(SysMenu::getParentId, menuIds)
                .list()
                .stream()
                .map(SysMenu::getId)
                .toList();
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
