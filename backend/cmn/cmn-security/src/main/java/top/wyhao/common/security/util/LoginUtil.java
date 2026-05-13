package top.wyhao.common.security.util;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.servlet.JakartaServletUtil;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import top.wyhao.starter.core.model.LoginUser;
import top.wyhao.starter.core.util.ExceptionUtils;
import top.wyhao.starter.core.util.IpUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 封装 SaToken 工具类
 */

public class LoginUtil {
    public static final String LOGIN_USER_KEY = "loginUser";

    /**
     * 获取用户基于token
     */
    public static Long getUserId() {
        return StpUtil.getLoginIdAsLong();
    }

    public static String getUsername(){
        return getLoginUser().getUsername();
    }
    /**
     * 获取当前登录用户
     */
    public static LoginUser getLoginUser() {
        SaSession session = StpUtil.getTokenSession();
        return (LoginUser) session.get(LOGIN_USER_KEY);
    }



    public static LoginUser getLoginUser(Object loginId) {
        SaSession session = StpUtil.getSessionByLoginId(loginId);
        if (ObjectUtil.isNull(session)) {
            return null;
        }
        return (LoginUser) session.get(LOGIN_USER_KEY);
    }

    /**
     * 判断当前会话是否已经登录
     */
    public static boolean isLogin() {
        return StpUtil.isLogin();
    }

    /**
     * 检验当前会话是否已经登录，如未登录，则抛出异常
     */
    public static void checkLogin(){
        StpUtil.checkLogin();
    }

    /**
     * 根据用户id 踢人下线
     * @param userId 用户ID
     */
    public static void kickout(Long userId){
        StpUtil.kickout(userId);
    }

    /**
     * 根据Token 踢人下线
     * @param token token
     */
    public static void kickout(String token){
        StpUtil.kickoutByTokenValue(token);
    }
    public static void logout(){
        StpUtil.logout();
    }

    public static List<String> getPermissions(){
        return StpUtil.getPermissionList();
    }

    public static List<String> getPermissions(String userId){
        return StpUtil.getPermissionList(userId);
    }

    public static boolean hasPermission(String permission){
        return StpUtil.hasPermission(permission);
    }

    public static boolean hasRole(String role){
        return StpUtil.hasRole(role);
    }

    /**
     * 查询已登录用户信息
     * @param page 页码
     * @param pageSize 每页数量
     * @param ascend 升序
     * @return 登录用户信息
     */
    public static List<LoginUser> pageUser(String keyword, int page, int pageSize, boolean ascend){
        // 1. 计算起始索引
        int start = (page - 1) * pageSize;
        // 2. 分页查询会话id
        List<String> sessionIdList = StpUtil.searchTokenSessionId(keyword, start, pageSize, ascend);
        // 3. 根据会话ID 获取用户信息
        List<LoginUser> users = new ArrayList<>();
        for (String sessionId : sessionIdList) {
            SaSession session = StpUtil.getSessionBySessionId(sessionId);
            if (session != null) {
                StpUtil.getStpLogic().getTokenLastActiveTime();
                users.add((LoginUser)session.get(LOGIN_USER_KEY));
            }
        }
        return users;
    }



    public static String getTokenValue() {
        return StpUtil.getTokenValue();
    }

    public static Long getTenantId() {
        SaSession session = StpUtil.getTokenSession();
        return session.getLong("tenantId");
    }

}
