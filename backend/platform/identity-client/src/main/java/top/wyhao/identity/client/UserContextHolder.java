package top.wyhao.identity.client;

import top.wyhao.cmn.core.model.LoginUser;

/**
 * 登录用户信息持有者
 */
public class UserContextHolder {

    private static UserContext LOGIN_USER_CONTEXT;

    public static void setLoginUserContext(UserContext context) {
        LOGIN_USER_CONTEXT = context;
    }

    public static Long getUserId() {
        return LOGIN_USER_CONTEXT == null ? null : LOGIN_USER_CONTEXT.getUserId();
    }

    public static LoginUser getCurrentUser() {
        return LOGIN_USER_CONTEXT == null ? null : LOGIN_USER_CONTEXT.getUser();
    }

    public static boolean isSuperadmin() {
        return LOGIN_USER_CONTEXT != null && LOGIN_USER_CONTEXT.isSuperadmin();
    }

    public static String getToken() {
        return LOGIN_USER_CONTEXT == null ? null : LOGIN_USER_CONTEXT.getToken();
    }
}
