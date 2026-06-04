
package top.wyhao.admin.auth.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serial;
import java.io.Serializable;

/**
 * 登录响应参数
 *
 * @since 2022/12/21 20:42
 */
@Schema(description = "登录响应参数")
public record LoginResult(
        /**
         * 响应代码
         */
        @Schema(description = "响应代码", example = "PASSWORD_EXPIRED")
        String code,

        /**
         * 令牌
         */
        @Schema(description = "令牌", example = "ey****J9.ey****fQ.KU****Z8")
        String token,

        /**
         * 租户 ID
         */
        @Schema(description = "租户 ID", example = "0")
        Long tenantId
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
