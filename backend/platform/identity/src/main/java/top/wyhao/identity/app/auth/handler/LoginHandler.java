
package top.wyhao.identity.app.auth.handler;

import top.wyhao.identity.adapter.web.auth.dto.LoginRequest;
import top.wyhao.identity.domain.auth.GrantType;
import top.wyhao.identity.adapter.web.auth.vo.LoginResult;

/**
 * 登录处理器
 */
public interface LoginHandler {

    GrantType grantType();

    /**
     * 登录
     *
     * @param request     登录请求参数
     * @return 登录响应参数
     */
    LoginResult login(LoginRequest request);



}