package top.wyhao.admin.system.exception;

import top.wyhao.starter.core.exception.BizException;

/**
 * 系统配置业务异常
 */
public class ConfigException extends BizException {

    public ConfigException(String message) {
        super(message);
    }

    public ConfigException(String code, String message) {
        super(code, message);
    }

    public static ConfigException of(String message) {
        return new ConfigException(message);
    }

    public static ConfigException of(String code, String message) {
        return new ConfigException(code, message);
    }

    public static ConfigException mailTestFailed(String detail) {
        return of("MAIL_TEST_FAILED", "测试邮件发送失败：" + detail);
    }
}
