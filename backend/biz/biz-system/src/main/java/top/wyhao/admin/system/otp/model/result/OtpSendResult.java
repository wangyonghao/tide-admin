package top.wyhao.admin.system.otp.model.result;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serial;
import java.io.Serializable;

/**
 * 发送 OTP 验证码响应 Record
 */
public class OtpSendResult {
    @Schema(description = "发送 OTP 验证码响应")
    public record Result(
            /**
             * OTP 会话 UUID
             */
            @Schema(description = "OTP 会话 UUID", example = "550e8400-e29b-41d4-a716-446655440000")
            String otpUuid,

            /**
             * 验证码有效期（秒）
             */
            @Schema(description = "验证码有效期（秒）", example = "300")
            Integer expiresIn,

            /**
             * 提示信息
             */
            @Schema(description = "提示信息", example = "验证码已发送")
            String message
    ) implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;
    }
}
