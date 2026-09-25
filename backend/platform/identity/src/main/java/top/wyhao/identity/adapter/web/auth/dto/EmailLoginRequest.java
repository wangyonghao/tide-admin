package top.wyhao.identity.adapter.web.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * 邮箱登录请求参数
 *
 * @since 2023/10/23 20:15
 */
@Data
@Schema(description = "邮箱登录请求参数")
public final class EmailLoginRequest implements LoginRequest {

    @Schema(
            description = "登录方式",
            allowableValues = {"EMAIL"},
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String grantType;

    /**
     * 客户端 ID
     */
    @Schema(description = "客户端 ID", example = "ef51c9a3e9046c4f2ea45142c8a8344a")
    @NotBlank(message = "客户端ID不能为空")
    private String clientId;

    /**
     * 邮箱
     */
    @Schema(description = "邮箱", example = "123456789@qq.com")
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    /**
     * 验证码
     */
    @Schema(description = "验证码", example = "888888")
    @NotBlank(message = "验证码不能为空")
    @Length(max = 6, message = "验证码无效")
    private String captcha;
}
