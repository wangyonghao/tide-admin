package top.wyhao.security.adapter.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import top.wyhao.cmn.core.enums.RoleCodeEnum;
import top.wyhao.security.client.PermissionProvider;
import top.wyhao.security.client.SecurityClient;
import top.wyhao.security.domain.gateway.UserRoleRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PermissionProviderImpl implements PermissionProvider {

    private final UserRoleRepository userRoleRepository;
    private final SecurityClient securityClient;

    @Override
    public List<String> findUserPermissions(Long userId) {
        List<String> roleCodes = findUserRoles(userId);
        if (roleCodes.contains(RoleCodeEnum.SUPER_ADMIN.getCode())) {
            return List.of("*:*:*");
        }
        return securityClient.listPermissionsByUserId(userId);
    }

    @Override
    public List<String> findUserRoles(Long userId) {
        return userRoleRepository.listRoleCodesByUserId(userId);
    }
}
