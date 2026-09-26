package top.wyhao.security.client;

import top.wyhao.security.client.config.AuthenticationConfigVO;

/**
 * 登录 / 认证配置。由 security 基于 settings {@code ConfigStoreApi} 实现。
 */
public interface AuthenticationConfigApi {

    AuthenticationConfigVO get();

    void update(AuthenticationConfigVO config);
}
