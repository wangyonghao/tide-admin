package top.wyhao.identity.auth.exception;

import cn.hutool.core.util.StrUtil;
import top.wyhao.starter.core.exception.BizException;

/**
 * 认证相关业务异常
 */
public class AuthException extends BizException {

    public AuthException(String message) {
        super(message);
    }

    public AuthException(String code, String message) {
        super(code, message);
    }

    public static AuthException of(String message) {
        return new AuthException(message);
    }

    public static AuthException of(String code, String message) {
        return new AuthException(code, message);
    }

    public static AuthException passwordError() {
        return of("PASSWORD_ERROR", "用户名或密码不正确");
    }

    public static AuthException needCaptcha() {
        return of("NEED_CAPTCHA", "需要验证码");
    }

    public static AuthException passwordErrorExceeded() {
        return of("PASSWORD_ERROR_EXCEEDED", "密码错误次数达到上限, 账号被锁定");
    }

    public static AuthException captchaRequired() {
        return of("CAPTCHA_IS_REQUIRED", "验证码不能为空");
    }

    public static AuthException captchaInvalid() {
        return of("CAPTCHA_INVALID", "验证码无效");
    }

    public static AuthException captchaExpired() {
        return of("CAPTCHA_EXPIRED", "验证码无效");
    }

    public static AuthException captchaOutdated() {
        return of("AUTH_CAPTCHA_OUTDATED", "验证码已失效");
    }

    public static AuthException captchaIncorrect() {
        return of("AUTH_CAPTCHA_INCORRECT", "验证码不正确");
    }

    public static AuthException phoneNotBound() {
        return of("AUTH_PHONE_NOT_BOUND", "此手机号未绑定本系统账号");
    }

    public static AuthException emailNotBound() {
        return of("AUTH_EMAIL_NOT_BOUND", "此邮箱未绑定本系统账号");
    }

    public static AuthException socialAuthFailed(String detail) {
        return of("AUTH_SOCIAL_AUTH_FAILED", detail);
    }

    public static AuthException captchaSendFailed() {
        return of("CAPTCHA_SEND_FAILED", "验证码发送失败");
    }

    public static AuthException accountLocked(long minutes) {
        return of("ACCOUNT_LOCKED", StrUtil.format("账号已锁定, {} 分钟后可重试", minutes));
    }

    public static AuthException tempTokenExpired() {
        return of("TEMPTOKEN_EXPIRED", "临时令牌无效或已过期");
    }

    public static AuthException platformNotSupport(String source) {
        return of("PLATFORM_NOT_SUPPORT", StrUtil.format("暂不支持 [{}] 平台账号登录", source));
    }

    public static AuthException kickoutSelfNotAllowed() {
        return of("AUTH_KICKOUT_SELF_NOT_ALLOWED", "不能强退自己");
    }

    public static AuthException accountDisabled() {
        return of("AUTH_ACCOUNT_DISABLED", "此账号已被禁用，如有疑问，请联系管理员");
    }

    public static AuthException accountDeptDisabled() {
        return of("AUTH_ACCOUNT_DEPT_DISABLED", "此账号所属部门已被禁用，如有疑问，请联系管理员");
    }
}
