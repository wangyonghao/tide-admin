package top.wyhao.admin.system.otp.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import top.wyhao.admin.system.otp.enums.OtpScene;

import java.io.Serial;
import java.io.Serializable;

/**
 * 发送 OTP 验证码请求 Record
 */
public class OtpSendRequest {
    @Schema(description = "发送 OTP 验证码请求")
    public record Request(
            /**
             * 业务场景
             */
            @Schema(description = "业务场景", example = "REGISTER")
            @NotNull(message = "业务场景不能为空")
            OtpScene scene,

            /**
             * 目标地址（邮箱或手机号）
             */
            @Schema(description = "目标地址（邮箱或手机号）", example = "user@example.com")
            @NotBlank(message = "目标地址不能为空")
            String target,

            /**
             * 语言代码
             */
            @Schema(description = "语言代码", example = "zh_CN")
            String locale
    ) implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;
    }
}
