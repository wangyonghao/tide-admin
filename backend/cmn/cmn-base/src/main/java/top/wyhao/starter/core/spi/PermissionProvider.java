package top.wyhao.starter.core.spi;

import java.util.List;

/**
 * 权限提供器
 * <p>
 * 由biz-system提供权限数据
 * </p>
 */
public interface PermissionProvider {
    /**
     * 获取用户权限码集合
     *
     * @param userId 用户ID
     * @return 权限码集合
     */
    List<String> findUserPermissions(Long userId);

    /**
     * 获取用户角色码集合
     *
     * @param userId 用户ID
     * @return 角色码集合
     */
    List<String> findUserRoles(Long userId);
}
