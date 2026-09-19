
package top.wyhao.admin.auth.handler;

import top.wyhao.admin.auth.model.LoginRequest;
import top.wyhao.admin.auth.model.LoginResult;
import top.wyhao.admin.auth.model.enums.GrantType;

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