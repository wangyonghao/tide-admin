package top.wyhao.admin.auth.handler;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.extra.servlet.JakartaServletUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import top.wyhao.admin.system.entity.SysDept;
import top.wyhao.admin.system.entity.SysUser;
import top.wyhao.admin.system.service.DeptService;
import top.wyhao.admin.system.service.LoginLogService;
import top.wyhao.common.security.util.LoginUtil;
import top.wyhao.starter.core.enums.StatusEnum;
import top.wyhao.starter.core.model.LoginUser;
import top.wyhao.starter.core.util.ExceptionUtils;
import top.wyhao.starter.core.util.IpUtils;
import top.wyhao.starter.core.util.validation.Check;

/**
 * 登录帮助类
 */
public class LoginHandlerHelper {

    /**
     * 创建登录会话并签发 Token
     *
     * @param userId 用户ID
     */
    public static void doLogin(Long userId) {
        StpUtil.login(userId);
    }

    public static void setSession(LoginUser loginUser, String deviceType) {
        RequestMeta info = getRequestMeta();
        // 写入Session
        SaSession session = StpUtil.getTokenSession();
        session.set("loginName", loginUser.getUsername());
        session.set("ipaddr", info.ip());
        session.set("loginLocation", info.address());
        session.set("browser", info.browser());
        session.set("os", info.os());
        session.set("loginTime", System.currentTimeMillis());
        session.set("deviceType", deviceType);
        session.set(LoginUtil.LOGIN_USER_KEY, loginUser);
    }


    // 记录登录日志
    public static void loginFail(String username, String ip, String userAgent, String reason) {
        LoginLogService loginLogService = SpringUtil.getBean(LoginLogService.class);
        loginLogService.asyncLog(username, ip, userAgent, "FAILED", reason);
    }

    public static void loginSuccess(String username, String ip, String userAgent) {
        LoginLogService loginLogService = SpringUtil.getBean(LoginLogService.class);
        loginLogService.asyncLog(username, ip, userAgent, "SUCCESS", null);
    }

    /**
     * 检查用户状态
     *
     * @param user 用户信息
     */
    public static void checkUserStatus(SysUser user) {
        Check.throwIfEqual(StatusEnum.DISABLE, user.getStatus(), "此账号已被禁用，如有疑问，请联系管理员");
        DeptService deptService = SpringUtil.getBean(DeptService.class);
        SysDept dept = deptService.getById(user.getDeptId());
        Check.throwIfEqual(StatusEnum.DISABLE, dept.getStatus(), "此账号所属部门已被禁用，如有疑问，请联系管理员");
    }

    /**
     * 从请求中读取 ip、地址、浏览器、操作系统信息
     */
    public static RequestMeta getRequestMeta() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attrs != null ? attrs.getRequest() : null;
        String ip = request != null ? JakartaServletUtil.getClientIP(request) : null;
        String address = ExceptionUtils.exToNull(() -> IpUtils.getRegion(ip));
        UserAgent ua = request != null ? UserAgentUtil.parse(request.getHeader("User-Agent")) : null;
        String browser = ua != null ? ua.getBrowser().getName() : "Unknown";
        String os = ua != null ? ua.getOs().getName() : "Unknown";
        String uaStr = ua != null ? ua.toString() : "Unknown";
        return new RequestMeta(ip, address, uaStr, browser, os);
    }


    /**
     * 请求信息
     */
    public record RequestMeta(String ip, String address, String userAgent, String browser, String os) {
    }
}
