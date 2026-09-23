package top.wyhao.admin.auth.exception;

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
}
