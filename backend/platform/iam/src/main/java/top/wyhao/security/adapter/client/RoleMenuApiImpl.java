package top.wyhao.security.adapter.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import top.wyhao.security.client.RoleMenuApi;
import top.wyhao.security.domain.gateway.RoleMenuRepository;

import java.util.List;
import java.util.Set;

/**
 * 角色和菜单关联业务 API 实现
 *
 * @since 2025/7/26 9:39
 */
@Service
@RequiredArgsConstructor
public class RoleMenuApiImpl implements RoleMenuApi {

    private final RoleMenuRepository roleMenuRepository;

    @Override
    public Set<Long> listRoleIdByNotInMenuIds(List<Long> menuIds) {
        return roleMenuRepository.listRoleIdsNotInMenuIds(menuIds);
    }

    @Override
    public void deleteByNotInMenuIds(List<Long> menuIds) {
        roleMenuRepository.deleteMenuIdNotIn(menuIds);
    }

    @Override
    public boolean add(List<Long> menuIds, Long roleId) {
        return roleMenuRepository.replaceByRoleId(roleId, menuIds);
    }
}
