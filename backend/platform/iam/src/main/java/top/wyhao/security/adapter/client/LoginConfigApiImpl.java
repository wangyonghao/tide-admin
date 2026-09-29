package top.wyhao.security.adapter.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import top.wyhao.security.client.LoginConfigApi;
import top.wyhao.security.client.config.LoginConfigVO;
import top.wyhao.settings.client.ConfigKeys;
import top.wyhao.settings.client.ConfigStoreApi;

/**
 * 登录配置：委托 settings 通用存储。
 */
@Service
@RequiredArgsConstructor
public class LoginConfigApiImpl implements LoginConfigApi {

    private final ConfigStoreApi configStoreApi;

    @Override
    public LoginConfigVO get() {
        return configStoreApi.get(ConfigKeys.LOGIN, LoginConfigVO.class);
    }

    @Override
    public void update(LoginConfigVO config) {
        configStoreApi.put(ConfigKeys.LOGIN, config);
    }
}
