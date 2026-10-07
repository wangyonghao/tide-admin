package top.wyhao.security.client;

import cn.hutool.core.util.StrUtil;
import top.wyhao.cmn.core.exception.BizException;

/**
 * 认证相关业务异常
 */
public class AuthenticationException extends BizException {

    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String code, String message) {
        super(code, message);
    }

    public static AuthenticationException of(String message) {
        return new AuthenticationException(message);
    }

    public static AuthenticationException of(String code, String message) {
        return new AuthenticationException(code, message);
    }

    public static AuthenticationException passwordError() {
        return of("PASSWORD_ERROR", "用户名或密码不正确");
    }

    public static AuthenticationException passwordDecryptFailed() {
        return of("USER_PASSWORD_DECRYPT_FAILED", "密码解密失败");
    }

    public static AuthenticationException passwordFormatInvalid() {
        return of("USER_PASSWORD_FORMAT_INVALID", "密码长度为 8-32 个字符，支持大小写字母、数字、特殊字符，至少包含字母和数字");
    }

    public static AuthenticationException needCaptcha() {
        return of("NEED_CAPTCHA", "需要验证码");
    }

    public static AuthenticationException passwordErrorExceeded() {
        return of("PASSWORD_ERROR_EXCEEDED", "密码错误次数达到上限, 账号被锁定");
    }

    public static AuthenticationException captchaRequired() {
        return of("CAPTCHA_IS_REQUIRED", "验证码不能为空");
    }

    public static AuthenticationException captchaInvalid() {
        return of("CAPTCHA_INVALID", "验证码无效");
    }

    public static AuthenticationException captchaExpired() {
        return of("CAPTCHA_EXPIRED", "验证码无效");
    }

    public static AuthenticationException captchaOutdated() {
        return of("AUTH_CAPTCHA_OUTDATED", "验证码已失效");
    }

    public static AuthenticationException captchaIncorrect() {
        return of("AUTH_CAPTCHA_INCORRECT", "验证码不正确");
    }

    public static AuthenticationException phoneNotBound() {
        return of("AUTH_PHONE_NOT_BOUND", "此手机号未绑定本系统账号");
    }

    public static AuthenticationException emailNotBound() {
        return of("AUTH_EMAIL_NOT_BOUND", "此邮箱未绑定本系统账号");
    }

    public static AuthenticationException socialAuthFailed(String detail) {
        return of("AUTH_SOCIAL_AUTH_FAILED", detail);
    }

    public static AuthenticationException captchaSendFailed() {
        return of("CAPTCHA_SEND_FAILED", "验证码发送失败");
    }

    public static AuthenticationException accountLocked(long minutes) {
        return of("ACCOUNT_LOCKED", StrUtil.format("账号已锁定, {} 分钟后可重试", minutes));
    }

    public static AuthenticationException tempTokenExpired() {
        return of("TEMPTOKEN_EXPIRED", "临时令牌无效或已过期");
    }

    public static AuthenticationException platformNotSupport(String source) {
        return of("PLATFORM_NOT_SUPPORT", StrUtil.format("暂不支持 [{}] 平台账号登录", source));
    }

    public static AuthenticationException kickoutSelfNotAllowed() {
        return of("AUTH_KICKOUT_SELF_NOT_ALLOWED", "不能强退自己");
    }

    public static AuthenticationException accountDisabled() {
        return of("AUTH_ACCOUNT_DISABLED", "此账号已被禁用，如有疑问，请联系管理员");
    }

    public static AuthenticationException accountDepartmentDisabled() {
        return of("AUTH_ACCOUNT_DEPARTMENT_DISABLED", "此账号所属部门已被禁用，如有疑问，请联系管理员");
    }

    public static AuthenticationException registrationDisabled() {
        return of("REGISTRATION_DISABLED", "系统未开放注册");
    }

    public static AuthenticationException passwordMismatch() {
        return of("PASSWORD_MISMATCH", "两次输入的密码不一致");
    }

    public static AuthenticationException registerRoleMissing() {
        return of("REGISTER_ROLE_REQUIRED", "未配置注册默认角色，请联系管理员");
    }
}
