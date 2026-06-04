
package top.wyhao.admin.auth.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

import java.io.Serial;
import java.io.Serializable;

/**
 * 邮箱登录请求参数
 *
 * @since 2023/10/23 20:15
 */
public class EmailLoginRequest {
    @Schema(description = "邮箱登录请求参数")
    public record Request(
            /**
             * 客户端 ID
             */
            @Schema(description = "客户端 ID", example = "ef51c9a3e9046c4f2ea45142c8a8344a")
            @NotBlank(message = "客户端ID不能为空")
            String clientId,

            /**
             * 邮箱
             */
            @Schema(description = "邮箱", example = "123456789@qq.com")
            @NotBlank(message = "邮箱不能为空")
            @Email(message = "邮箱格式不正确")
            String email,

            /**
             * 验证码
             */
            @Schema(description = "验证码", example = "888888")
            @NotBlank(message = "验证码不能为空")
            @Length(max = 6, message = "验证码无效")
            String captcha
    ) implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;
    }
}
