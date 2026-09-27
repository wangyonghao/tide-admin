package top.wyhao.security.adapter.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import top.wyhao.security.client.SecurityClient;
import top.wyhao.security.domain.gateway.MenuRepository;

import java.util.List;

/**
 * 授权域客户端实现
 */
@Service
@RequiredArgsConstructor
public class SecurityClientImpl implements SecurityClient {

    private final MenuRepository menuRepository;

    @Override
    public List<String> listPermissionsByUserId(Long userId) {
        return menuRepository.listPermissionCodesByUserId(userId);
    }
}
