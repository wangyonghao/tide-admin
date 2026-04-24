package top.wyhao.starter.core.auth;

import java.util.List;

public interface PermissionProvider {
    List<String> findUserPermissions(Long userId);
    List<String> findUserRoles(Long userId);
}
