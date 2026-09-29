package top.wyhao.security.client;

import top.wyhao.security.client.config.LoginConfigVO;

/**
 * 登录 / 认证配置。由 security 基于 settings {@code ConfigStoreApi} 实现。
 */
public interface LoginConfigApi {

    LoginConfigVO get();

    void update(LoginConfigVO config);
}
