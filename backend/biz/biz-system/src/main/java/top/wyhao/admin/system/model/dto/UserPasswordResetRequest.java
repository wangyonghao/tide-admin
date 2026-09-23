
package top.wyhao.admin.system.model.bo.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 用户密码重置请求参数
 */
@Data
@Schema(description = "用户密码重置请求参数")
public class UserPasswordResetRequest {
    /**
     * 新密码
     */
    @Schema(description = "新密码", example = "RSA 公钥加密的新密码")
    @NotBlank(message = "新密码不能为空")
    private String newPassword;
}
