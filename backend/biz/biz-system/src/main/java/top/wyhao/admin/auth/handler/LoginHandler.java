
package top.wyhao.admin.auth.handler;

import top.wyhao.admin.auth.model.LoginResult;

/**
 * 登录处理器
 */
public interface LoginHandler<T> {

    /**
     * 登录
     *
     * @param req     登录请求参数
     * @param client  客户端信息
     * @param request 请求对象
     * @return 登录响应参数
     */
    LoginResult login(T req);
}