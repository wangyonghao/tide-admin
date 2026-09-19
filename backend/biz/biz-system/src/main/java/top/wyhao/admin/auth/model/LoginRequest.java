
package top.wyhao.admin.auth.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/**
 * 登录请求参数基类
 *
 * @since 2024/12/22 15:16
 */

@Schema(
        description = "登录请求",

        oneOf = { // 让 Swagger 展示多个可选请求模型
                AccountLoginRequest.class,
                PhoneLoginRequest.class,
                EmailLoginRequest.class,
                SocialLoginRequest.class
        },
        discriminatorProperty = "grantType",
        discriminatorMapping = {
                @DiscriminatorMapping(
                        value = "ACCOUNT",
                        schema = AccountLoginRequest.class
                ),
                @DiscriminatorMapping(
                        value = "PHONE",
                        schema = PhoneLoginRequest.class
                ),
                @DiscriminatorMapping(
                        value = "EMAIL",
                        schema = EmailLoginRequest.class
                ),
                @DiscriminatorMapping(
                        value = "SOCIAL",
                        schema = SocialLoginRequest.class
                )
        }
)

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "grantType", // 根据 grantType 自动反序列化为对应子类
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = AccountLoginRequest.class, name = "ACCOUNT"),
        @JsonSubTypes.Type(value = PhoneLoginRequest.class, name = "PHONE"),
        @JsonSubTypes.Type(value = EmailLoginRequest.class, name = "EMAIL"),
        @JsonSubTypes.Type(value = SocialLoginRequest.class, name = "SOCIAL"),
})
public sealed interface LoginRequest permits AccountLoginRequest, PhoneLoginRequest, EmailLoginRequest, SocialLoginRequest {

    /**
     * 认证类型
     */
    @Schema(
            description = "登录方式",
            allowableValues = {
                    "ACCOUNT",
                    "PHONE",
                    "EMAIL",
                    "SOCIAL"
            },
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "认证类型无效")
    String grantType();
}
