package top.wyhao.starter.core.spi;

import top.wyhao.starter.core.model.LoginUser;

/**
 * 登录用户上下文
 * <p>
 * 由cmn-security提供具体实现
 * </p>
 */
public interface UserContext {
    /**
     * 获取当前登录用户ID
     * @return 未登录/匿名返回 null
     */
    Long getUserId();

    /**
     * 获取当前登录用户信息
     * @return 未登录/匿名返回 null
     */
    LoginUser getUser();

    /**
     * 获取当前登录用户Token
     * @return 未登录/匿名返回 null
     */
    String getToken();

    /**
     * 判断当前用户是否为超级管理员
     * @return 未登录/匿名返回 false
     */
    boolean isSuperadmin();
}
