package top.wyhao.admin.auth.exception;

import cn.hutool.core.util.StrUtil;
import top.wyhao.starter.core.exception.BizException;

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
    public static AuthException passwordError(){
        return new AuthException("PASSWORD_ERROR","用户名或密码不正确");
    }

    public static AuthException needCaptcha(){
        return new AuthException("NEED_CAPTCHA","需要验证码");
    }

    public static AuthException passwordErrorExceeded() {
        return new AuthException("PASSWORD_ERROR_EXCEEDED","密码错误次数达到上限, 账号被锁定");
    }
}
