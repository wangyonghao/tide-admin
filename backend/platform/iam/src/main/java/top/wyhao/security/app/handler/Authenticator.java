
package top.wyhao.security.app.handler;

import top.wyhao.security.adapter.web.dto.AuthenticationRequest;
import top.wyhao.security.domain.model.GrantType;
import top.wyhao.security.adapter.web.vo.AuthenticationResult;

/**
 * 认证处理器。按 grantType 校验凭证并建立会话。
 */
public interface Authenticator {

    GrantType grantType();

    /**
     * 认证
     *
     * @param request 认证请求
     * @return 认证结果
     */
    AuthenticationResult authenticate(AuthenticationRequest request);



}