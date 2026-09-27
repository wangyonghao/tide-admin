package top.wyhao.security.adapter.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import top.wyhao.security.client.AuthenticationConfigApi;
import top.wyhao.security.client.config.AuthenticationConfigVO;
import top.wyhao.settings.client.ConfigKeys;
import top.wyhao.settings.client.ConfigStoreApi;

/**
 * 登录配置：委托 settings 通用存储。
 */
@Service
@RequiredArgsConstructor
public class AuthenticationConfigApiImpl implements AuthenticationConfigApi {

    private final ConfigStoreApi configStoreApi;

    @Override
    public AuthenticationConfigVO get() {
        return configStoreApi.get(ConfigKeys.LOGIN, AuthenticationConfigVO.class);
    }

    @Override
    public void update(AuthenticationConfigVO config) {
        configStoreApi.put(ConfigKeys.LOGIN, config);
    }
}
