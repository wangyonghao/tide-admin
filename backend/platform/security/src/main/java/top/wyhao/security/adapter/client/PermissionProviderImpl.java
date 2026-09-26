package top.wyhao.security.fascade;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import top.wyhao.identity.app.service.UserService;
import top.wyhao.security.client.PermissionProvider;

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
