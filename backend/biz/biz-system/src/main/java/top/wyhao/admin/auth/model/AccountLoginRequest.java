
package top.wyhao.admin.auth.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.io.Serial;
import java.io.Serializable;

/**
 * 账号登录请求参数
 *
 * @since 2022/12/21 20:43
 */
public class AccountLoginRequest {
    @Schema(description = "账号登录请求参数")
    public record Request(
            /**
             * 客户端 ID
             */
            @Schema(description = "客户端 ID", example = "ef51c9a3e9046c4f2ea45142c8a8344a")
            @NotBlank(message = "客户端ID不能为空")
            String clientId,

            /**
             * 用户名
             */
            @Schema(description = "用户名", example = "zhangsan")
            @NotBlank(message = "用户名不能为空")
            String username,

            /**
             * 密码
             */
            @Schema(description = "密码", example = "RSA 公钥加密的密码")
            @NotBlank(message = "密码不能为空")
            String password,

            /**
             * 验证码
             */
            @Schema(description = "验证码", example = "ABCD")
            String captcha,

            /**
             * 验证码标识
             */
            @Schema(description = "验证码标识", example = "090b9a2c-1691-4fca-99db-e4ed0cff362f")
            String uuid
    ) implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;
    }
}
