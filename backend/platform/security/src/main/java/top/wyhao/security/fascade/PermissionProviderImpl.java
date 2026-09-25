package top.wyhao.security.provider;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import top.wyhao.identity.service.UserService;
import top.wyhao.starter.core.spi.PermissionProvider;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PermissionProviderImpl implements PermissionProvider {
    private final UserService userService;

    @Override
    public List<String> findUserPermissions(Long userId) {
        return userService.findUserPermissions(userId);
    }

    @Override
    public List<String> findUserRoles(Long userId) {
        return userService.findUserRoles(userId);
    }

}
