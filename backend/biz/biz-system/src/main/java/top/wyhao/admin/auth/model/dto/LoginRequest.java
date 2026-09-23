package top.wyhao.admin.auth.model.dto;

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
        oneOf = {
                AccountLoginRequest.class,
                PhoneLoginRequest.class,
                EmailLoginRequest.class,
                SocialLoginRequest.class
        },
        discriminatorProperty = "grantType",
        discriminatorMapping = {
                @DiscriminatorMapping(value = "ACCOUNT", schema = AccountLoginRequest.class),
                @DiscriminatorMapping(value = "PHONE", schema = PhoneLoginRequest.class),
                @DiscriminatorMapping(value = "EMAIL", schema = EmailLoginRequest.class),
                @DiscriminatorMapping(value = "SOCIAL", schema = SocialLoginRequest.class)
        }
)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "grantType",
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
            allowableValues = {"ACCOUNT", "PHONE", "EMAIL", "SOCIAL"},
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "认证类型无效")
    String getGrantType();
}
