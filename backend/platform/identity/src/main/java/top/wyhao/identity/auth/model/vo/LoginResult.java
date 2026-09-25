package top.wyhao.identity.auth.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 登录响应参数
 *
 * @since 2022/12/21 20:42
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "登录响应参数")
public class LoginResult {



    /**
     * 响应代码
     */
    @Schema(description = "响应代码", example = "PASSWORD_EXPIRED")
    private String code;

    /**
     * 令牌
     */
    @Schema(description = "令牌", example = "ey****J9.ey****fQ.KU****Z8")
    private String token;

    /**
     * 租户 ID
     */
    @Schema(description = "租户 ID", example = "0")
    private Long tenantId;
}
