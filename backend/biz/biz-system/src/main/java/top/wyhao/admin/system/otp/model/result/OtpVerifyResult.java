package top.wyhao.admin.system.otp.model.result;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serial;
import java.io.Serializable;

/**
 * 验证 OTP 验证码响应 Record
 */
public class OtpVerifyResult {
    @Schema(description = "验证 OTP 验证码响应")
    public record Result(
            /**
             * 验证结果
             */
            @Schema(description = "验证结果", example = "true")
            Boolean verified,

            /**
             * 提示信息
             */
            @Schema(description = "提示信息", example = "验证成功")
            String message
    ) implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;
    }
}
