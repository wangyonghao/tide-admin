
package top.wyhao.admin.auth.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.temp.SaTempUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
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
import top.wyhao.admin.auth.handler.AccountLoginHandler;
import top.wyhao.admin.auth.handler.EmailLoginHandler;
import top.wyhao.admin.auth.handler.PhoneLoginHandler;
import top.wyhao.admin.auth.handler.SocialLoginHandler;
import top.wyhao.admin.auth.model.*;
import top.wyhao.admin.auth.model.enums.AuthType;
import top.wyhao.admin.system.model.LoginLogModel;
import top.wyhao.admin.system.model.bo.user.UserPasswordResetRequest;
import top.wyhao.admin.system.service.LoginLogService;
import top.wyhao.admin.system.service.MenuService;
import top.wyhao.admin.system.service.UserService;
import top.wyhao.common.security.util.LoginUtil;
import top.wyhao.starter.core.exception.BadRequestException;
import top.wyhao.starter.core.exception.BizException;
import top.wyhao.starter.core.util.RsaUtils;
import top.wyhao.starter.core.util.validation.Validator;
import top.wyhao.starter.web.core.model.PageQuery;
import top.wyhao.starter.web.core.model.PageResult;

import java.util.Map;

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

    @SaIgnore
    @Operation(summary = "登录", description = "用户登录")
    @PostMapping("/auth/login")
    public LoginResult login(@RequestBody @Valid Object reqObj) {
        // 需要根据 JSON 中的 authType 字段来判断具体的请求类型
        if (!(reqObj instanceof Map)) {
            throw new BadRequestException("AUTH_REQUEST_INVALID", "请求参数格式错误");
        }
        
        Map<String, Object> reqMap = (Map<String, Object>) reqObj;
        String authTypeStr = (String) reqMap.get("authType");
        AuthType authType = authTypeStr != null ? AuthType.valueOf(authTypeStr) : AuthType.ACCOUNT;
        
        LoginResult loginResult;
        switch (authType) {
            case ACCOUNT: // 账号密码登录
                AccountLoginRequest.Request accountReq = JSONUtil.toBean(JSONUtil.toJsonStr(reqMap), AccountLoginRequest.Request.class);
                Validator.validate(accountReq);
                AccountLoginHandler accountLoginHandler = SpringUtil.getBean(AccountLoginHandler.class);
                loginResult = accountLoginHandler.login(accountReq);
                break;
            case SOCIAL: // 社交账号登录
                SocialLoginRequest.Request socialReq = JSONUtil.toBean(JSONUtil.toJsonStr(reqMap), SocialLoginRequest.Request.class);
                Validator.validate(socialReq);
                SocialLoginHandler socialLoginHandler = SpringUtil.getBean(SocialLoginHandler.class);
                loginResult = socialLoginHandler.login(socialReq);
                break;
            case EMAIL: // 邮箱登录
                EmailLoginRequest.Request emailReq = JSONUtil.toBean(JSONUtil.toJsonStr(reqMap), EmailLoginRequest.Request.class);
                Validator.validate(emailReq);
                EmailLoginHandler emailLoginHandler = SpringUtil.getBean(EmailLoginHandler.class);
                loginResult = emailLoginHandler.login(emailReq);
                break;
            case PHONE:  // 手机登录
                PhoneLoginRequest.Request phoneReq = JSONUtil.toBean(JSONUtil.toJsonStr(reqMap), PhoneLoginRequest.Request.class);
                Validator.validate(phoneReq);
                PhoneLoginHandler phoneLoginHandler = SpringUtil.getBean(PhoneLoginHandler.class);
                loginResult = phoneLoginHandler.login(phoneReq);
                break;
            default:
                throw new BadRequestException("AUTH_TYPE_INVALID", "认证类型无效");
        }
        return loginResult;

    }

    @SaIgnore
    @Operation(summary = "(密码过期时）强制用户修改密码", description = "通过临时令牌修改密码")
    @PostMapping("/auth/force-change-password")
    public void forceChangePassword(@RequestBody Map<String, String> body) {
        String tempToken = body.getOrDefault("tempToken", body.get("temp-token"));
        String newPasswordEnc = body.get("newPassword");
        Object userIdObj = SaTempUtil.parseToken(tempToken);
        if (userIdObj == null) {
            throw new BizException("TEMPTOKEN_EXPIRED", "临时令牌无效或已过期");
        }
        String newPassword = RsaUtils.decryptPasswordByRsaPrivateKey(newPasswordEnc, "新密码解密失败");
        UserPasswordResetRequest resetReq = new UserPasswordResetRequest();
        resetReq.setNewPassword(newPassword);
        userService.resetPassword(resetReq, Convert.toLong(userIdObj));
    }

    @Operation(summary = "登出", description = "注销用户的当前登录")
    @Parameter(name = "Authorization", description = "令牌", required = true, example = "Bearer xxxx-xxxx-xxxx-xxxx", in = ParameterIn.HEADER)
    @PostMapping("/auth/logout")
    public void logout() {
        try {
            LoginUtil.logout();
        } catch (NotLoginException ignored) {
        }
    }

    @SaIgnore
    @Operation(summary = "三方账号登录授权", description = "三方账号登录授权")
    @Parameter(name = "source", description = "来源", example = "gitee", in = ParameterIn.PATH)
    @GetMapping("/auth/{source}")
    public SocialAuthorizeUrlResult socialLogin(@PathVariable String source) {
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
            throw new BizException("PLATFORM_NOT_SUPPORT", "暂不支持 [%s] 平台账号登录".formatted(source));
        }
    }



    @Operation(summary = "查询登录日志", description = "分页查询登录日志列表")
    @GetMapping("/auth/login-log")
    public PageResult<LoginLogModel> page(LoginLogModel.LoginLogQuery query, PageQuery pageQuery) {
        return loginLogService.page(query, pageQuery);
    }

    @Operation(summary = "导出", description = "导出登录日志数据")
    @SaCheckPermission("monitor:log:export")
    @GetMapping("/auth/login-log/export")
    public void export(LoginLogModel.LoginLogQuery query, HttpServletResponse response) {
        loginLogService.export(query, response);
    }
}
