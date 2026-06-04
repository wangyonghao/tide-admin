
package top.wyhao.admin.auth.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serial;
import java.io.Serializable;

/**
 * 三方账号授权认证响应参数
 *
 * @since 2024/3/6 22:26
 */
@Schema(description = "三方账号授权认证响应参数")
public record SocialAuthorizeUrlResult(
        /**
         * 授权 URL
         */
        @Schema(description = "授权 URL", example = "https://gitee.com/oauth/authorize?response_type=code&client_id=5d271b7f638941812aaf8bfc2e2f08f06d6235ef934e0e39537e2364eb8452c4&redirect_uri=http://localhost:5173/social/callback?source=gitee&state=d4ea7129e2531050210e9c918cc007d7&scope=user_info")
        String authorizeUrl
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
