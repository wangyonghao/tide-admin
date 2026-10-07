package top.wyhao.security.client;

import top.wyhao.security.client.config.RegisterConfigVO;

/**
 * 注册配置。由 security 基于 settings {@code ConfigStoreApi} 实现。
 */
public interface RegisterConfigApi {

    RegisterConfigVO get();

    void update(RegisterConfigVO config);
}
