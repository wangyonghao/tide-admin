
package top.wyhao.admin.auth.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.temp.SaTempUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import com.xkcoding.justauth.autoconfigure.JustAuthProperties;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.zhyd.oauth.AuthRequestBuilder;
import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.request.AuthRequest;
import me.zhyd.oauth.utils.AuthStateUtils;
import org.springframework.web.bind.annotation.*;
import top.wyhao.admin.auth.model.dto.LoginRequest;
import top.wyhao.admin.auth.model.vo.AuthInfoResult;
import top.wyhao.admin.auth.model.vo.LoginResult;
import top.wyhao.admin.auth.model.vo.OnlineUserResult;
import top.wyhao.admin.auth.model.vo.SocialAuthorizeUrlResult;
import top.wyhao.admin.auth.service.AuthService;
import top.wyhao.admin.system.model.dto.UserPasswordResetRequest;
import top.wyhao.admin.system.service.LoginLogService;
import top.wyhao.admin.system.service.MenuService;
import top.wyhao.admin.system.service.UserService;
import top.wyhao.common.security.util.LoginUtil;
import top.wyhao.admin.auth.exception.AuthException;
import top.wyhao.starter.core.util.RsaUtils;
import top.wyhao.starter.web.core.model.PageQuery;
import top.wyhao.starter.web.core.model.PageResult;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import top.wyhao.admin.system.model.dto.LoginLogQuery;
import top.wyhao.admin.system.model.vo.LoginLogResult;

/**
 * 用户认证 API
 */
@Tag(name = "用户认证 API")
@RestController
@RequiredArgsConstructor
public class AuthController {
    private final JustAuthProperties authProperties;

    private final UserService userService;
    private final MenuService menuService;
    private final LoginLogService loginLogService;
    private final AuthService authService;

    @SaIgnore
    @Operation(summary = "登录", description = "用户统一登录入口")
    @PostMapping("/auth/login")
    public LoginResult login(@RequestBody @Valid LoginRequest loginRequest) {
        return authService.login(loginRequest);
    }

    @Operation(summary = "登出", description = "注销用户的当前登录")
    @Parameter(name = "Authorization", description = "令牌", required = true, example = "Bearer xxxx-xxxx-xxxx-xxxx", in = ParameterIn.HEADER)
    @PostMapping("/auth/logout")
    public void logout() {
        authService.logout();
    }

    @SaIgnore
    @Operation(summary = "(密码过期时）强制用户修改密码", description = "通过临时令牌修改密码")
    @PostMapping("/auth/force-change-password")
    public void forceChangePassword(@RequestBody Map<String, String> body) {
        String tempToken = body.getOrDefault("tempToken", body.get("temp-token"));
        String newPasswordEnc = body.get("newPassword");
        Object userIdObj = SaTempUtil.parseToken(tempToken);
        if (userIdObj == null) {
            throw AuthException.tempTokenExpired();
        }
        String newPassword = RsaUtils.decryptPasswordByRsaPrivateKey(newPasswordEnc, "新密码解密失败");
        UserPasswordResetRequest resetReq = new UserPasswordResetRequest();
        resetReq.setNewPassword(newPassword);
        userService.resetPassword(resetReq, Convert.toLong(userIdObj));
    }


    @SaIgnore
    @Operation(summary = "三方账号登录授权", description = "三方账号登录授权")
    @Parameter(name = "source", description = "来源", example = "gitee", in = ParameterIn.PATH)
    @GetMapping("/auth/{source}")
    public SocialAuthorizeUrlResult bind(@PathVariable String source) {
        AuthRequest authRequest = this.getAuthRequest(source);
        return new SocialAuthorizeUrlResult(authRequest.authorize(AuthStateUtils.createState()));
    }

    @Operation(summary = "获取认证信息", description = "获取认证信息")
    @GetMapping("/auth/info")
    public AuthInfoResult getAuthInfo() {
        Long userId = LoginUtil.getUserId();
        return new AuthInfoResult(
                userService.detail(userId),
                userService.findUserRoles(userId),
                userService.findUserPermissions(userId),
                menuService.getMenuTreeByUserId(userId)
        );
    }

    private AuthRequest getAuthRequest(String source) {
        try {
            AuthConfig authConfig = authProperties.getType().get(source.toUpperCase());
            return AuthRequestBuilder.builder().source(source).authConfig(authConfig).build();
        } catch (Exception e) {
            throw AuthException.platformNotSupport(source);
        }
    }

    @Operation(summary = "查询登录日志", description = "分页查询登录日志列表")
    @GetMapping("/auth/login-log")
    public PageResult<LoginLogResult> page(LoginLogQuery query, PageQuery pageQuery) {
        return loginLogService.page(query, pageQuery);
    }

    @Operation(summary = "导出", description = "导出登录日志数据")
    @SaCheckPermission("monitor:log:export")
    @GetMapping("/auth/login-log/export")
    public void export(LoginLogQuery query, HttpServletResponse response) {
        loginLogService.export(query, response);
    }

    @Operation(summary = "分页查询列表", description = "分页查询列表")
    @SaCheckPermission("monitor:online:list")
    @GetMapping("/monitor/online")
    public PageResult<OnlineUserResult> page(@Valid String keyword, @Valid PageQuery pageQuery) {
        int start = (pageQuery.getPage() - 1) * pageQuery.getPageSize();

        List<String> sessionIds = StpUtil.searchTokenSessionId("", start, pageQuery.getPageSize(), false);

        List<OnlineUserResult> onlineUsers = new ArrayList<>();
        for (String sessionId : sessionIds) {
            try {
                SaSession session = StpUtil.getSessionBySessionId(sessionId);
                if (session != null) {
                    long loginTime = session.get("loginTime", session.getCreateTime());
                    long lastAccessTime =  StpUtil.getStpLogic().getTokenLastActiveTime(session.getToken());
                    OnlineUserResult online = new OnlineUserResult(
                            sessionId,
                            session.getToken(),
                            session.get("loginName", ""),
                            session.get("ipaddr", ""),
                            session.get("loginLocation", ""),
                            session.get("browser", ""),
                            session.get("os", ""),
                            formatTime(loginTime),
                            formatTime(lastAccessTime)
                    );
                    onlineUsers.add(online);
                }
            } catch (Exception e) {
                // 忽略无效的session
            }
        }
        return PageResult.build(pageQuery.getPage(), pageQuery.getPageSize(), onlineUsers);
    }


    @Operation(summary = "强退在线用户", description = "强退在线用户")
    @Parameter(name = "token", description = "令牌", example = "ey****J9.ey****fQ.7q****vE", in = ParameterIn.PATH)
    @SaCheckPermission("monitor:online:kickout")
    @DeleteMapping("/monitor/online/{token}")
    public void kickout(@PathVariable String token) {
        String currentToken = LoginUtil.getTokenValue();
        if (ObjectUtil.equal(token, currentToken)) {
            throw AuthException.kickoutSelfNotAllowed();
        }
        LoginUtil.kickout(token);
    }

    @Operation(summary = "批量强退在线用户", description = "批量强退在线用户")
    @SaCheckPermission("monitor:online:kickout")
    @DeleteMapping("/monitor/online")
    public void batchKickout(@Valid @org.springframework.web.bind.annotation.RequestBody List<String> tokens) {
        String currentToken = LoginUtil.getTokenValue();
        for (String token : tokens) {
            if (!token.equals(currentToken)) {
                LoginUtil.kickout(token);
            }
        }
    }


    private LocalDateTime formatTime(long timestamp) {
        return LocalDateTime.ofInstant(
                java.time.Instant.ofEpochMilli(timestamp),
                java.time.ZoneId.systemDefault()
        );
    }
}
