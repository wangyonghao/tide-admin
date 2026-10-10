package top.wyhao.security.infrastructure.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import top.wyhao.cmn.core.enums.StatusEnum;
import top.wyhao.security.domain.gateway.MenuRepository;
import top.wyhao.security.domain.model.SysMenu;
import top.wyhao.security.infrastructure.persistence.mapper.SysMenuMapper;

import java.util.List;

/**
 * 菜单仓储实现。
 */
@Repository
@RequiredArgsConstructor
public class MenuRepositoryImpl implements MenuRepository {

    private final SysMenuMapper menuMapper;

    @Override
    public List<SysMenu> listAll() {
        return menuMapper.selectList(new LambdaQueryWrapper<SysMenu>()
                .orderByAsc(SysMenu::getParentId)
                .orderByAsc(SysMenu::getSort));
    }

    @Override
    public List<SysMenu> listEnabledTree() {
        return menuMapper.selectList(new LambdaQueryWrapper<SysMenu>()
                .eq(SysMenu::getStatus, StatusEnum.ENABLE.getValue())
                .orderByAsc(SysMenu::getParentId)
                .orderByAsc(SysMenu::getSort));
    }

    @Override
    public List<SysMenu> listEnabledCatalogAndMenu() {
        return menuMapper.selectList(new LambdaQueryWrapper<SysMenu>()
                .eq(SysMenu::getStatus, 1)
                .in(SysMenu::getType, 1, 2)
                .orderByAsc(SysMenu::getParentId)
                .orderByAsc(SysMenu::getSort));
    }

    @Override
    public List<SysMenu> listEnabled() {
        return menuMapper.lambdaQuery().eq(SysMenu::getStatus, "1").list();
    }

    @Override
    public List<SysMenu> listByUserId(Long userId) {
        return menuMapper.selectMenusByUserId(userId);
    }

    @Override
    public List<SysMenu> listByRoleId(Long roleId) {
        return menuMapper.selectListByRoleId(roleId);
    }

    @Override
    public SysMenu findById(Long id) {
        return menuMapper.selectById(id);
    }

    @Override
    public void insert(SysMenu menu) {
        menuMapper.insert(menu);
    }

    @Override
    public void updateById(SysMenu menu) {
        menuMapper.updateById(menu);
    }

    @Override
    public void updatePlacement(Long id, Long parentId, Integer sort) {
        menuMapper.lambdaUpdate()
                .eq(SysMenu::getId, id)
                .set(SysMenu::getParentId, parentId)
                .set(SysMenu::getSort, sort)
                .update();
    }

    @Override
    public void deleteByIds(List<Long> ids) {
        menuMapper.deleteByIds(ids);
    }

    @Override
    public boolean nameExists(String name, Long parentId, Long selfId) {
        return menuMapper.isNameExists(name, parentId, selfId);
    }

    @Override
    public List<Long> listChildIds(List<Long> parentIds) {
        return menuMapper.lambdaQuery()
                .select(SysMenu::getId)
                .in(SysMenu::getParentId, parentIds)
                .list()
                .stream()
                .map(SysMenu::getId)
                .toList();
    }

    @Override
    public List<String> listPermissionCodesByUserId(Long userId) {
        return menuMapper.selectPermissionByUserId(userId);
    }

    @Override
    public long countIdGreaterThan(Long id) {
        return menuMapper.lambdaQuery().gt(SysMenu::getId, id).count();
    }

    @Override
    public boolean deleteIdGreaterThan(Long id) {
        return menuMapper.lambdaUpdate().gt(SysMenu::getId, id).remove();
    }
}
