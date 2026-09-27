package top.wyhao.security.domain.gateway;

import top.wyhao.security.domain.model.SysUserRole;

import java.util.Collection;
import java.util.List;

/**
 * 用户与角色关联仓储。
 */
public interface UserRoleRepository {

    boolean insertBatch(List<SysUserRole> userRoles);

    boolean existsByRoleId(Long roleId);

    List<Long> listRoleIdsByUserId(Long userId);

    List<Long> listUserIdsByRoleId(Long roleId);

    void deleteByUserId(Long userId);

    void deleteByUserIds(List<Long> userIds);

    void deleteByRoleIdAndUserIds(Long roleId, List<Long> userIds);

    List<String> listRoleCodesByUserId(Long userId);

    void deleteAll();

    boolean deleteByRoleIdNotIn(Collection<Long> keepRoleIds);

    boolean deleteByUserIdNotIn(Collection<Long> keepUserIds);
}
