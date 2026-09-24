package top.wyhao.admin.auth.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 账号登录请求参数
 *
 * @since 2022/12/21 20:43
 */
@Data
@Schema(description = "账号登录请求参数")
public final class AccountLoginRequest implements LoginRequest {

    @Schema(
            description = "登录方式",
            allowableValues = {"ACCOUNT"},
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String grantType;

    @Schema(description = "客户端 ID", example = "ef51c9a3e9046c4f2ea45142c8a8344a")
    @NotBlank(message = "客户端ID不能为空")
    private String clientId;

    @Schema(description = "用户名", example = "zhangsan")
    @NotBlank(message = "用户名不能为空")
    private String username;

    @Schema(description = "密码", example = "RSA 公钥加密的密码")
    @NotBlank(message = "密码不能为空")
    private String password;

    @Schema(description = "验证码", example = "ABCD")
    private String captcha;

    @Schema(description = "验证码标识", example = "090b9a2c-1691-4fca-99db-e4ed0cff362f")
    private String uuid;
}
