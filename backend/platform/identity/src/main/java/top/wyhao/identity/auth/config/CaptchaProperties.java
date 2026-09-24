package top.wyhao.admin.auth.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 验证码配置属性
 *
 * @since 2022/12/11 13:35
 */
@Data
@Component
@ConfigurationProperties(prefix = "captcha")
public class CaptchaProperties {

    @Value("${wyhao-starter.captcha.graphic.expirationInSeconds}")
    private long expirationInSeconds;

    private CaptchaEmail email;

    private CaptchaSms sms;

    @Data
    public static class CaptchaEmail {
        private int length;
        private long expirationInMinutes;
        private String templatePath;
    }

    @Data
    public static class CaptchaSms {
        private int length;
        private long expirationInMinutes;
        private String codeKey = "code";
        private String timeKey = "expirationInMinutes";
    }
}
