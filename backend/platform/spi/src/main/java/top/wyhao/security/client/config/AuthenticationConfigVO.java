package top.wyhao.security.client.config;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 登录 / 认证相关配置。
 */
@Data
@Schema(description = "登录配置")
public class AuthenticationConfigVO {

    @Schema(description = "是否开启验证码", example = "true")
    private Boolean captchaEnabled;

    @Schema(description = "验证码类型：graphic-图形验证码，behavior-行为验证码", example = "graphic")
    private String captchaType;

    @Schema(description = "登录最大重试次数", example = "5")
    private Integer maxRetry;

    @Schema(description = "登录锁定时间（分钟）", example = "30")
    private Integer lockTime;

    @Schema(description = "会话超时时间（分钟）", example = "30")
    private Integer sessionTimeout;
}
