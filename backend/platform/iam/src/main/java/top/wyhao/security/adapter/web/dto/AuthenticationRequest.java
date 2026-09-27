package top.wyhao.security.adapter.web.dto;

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
                AccountAuthenticationRequest.class,
                PhoneAuthenticationRequest.class,
                EmailAuthenticationRequest.class,
                SocialAuthenticationRequest.class
        },
        discriminatorProperty = "grantType",
        discriminatorMapping = {
                @DiscriminatorMapping(value = "ACCOUNT", schema = AccountAuthenticationRequest.class),
                @DiscriminatorMapping(value = "PHONE", schema = PhoneAuthenticationRequest.class),
                @DiscriminatorMapping(value = "EMAIL", schema = EmailAuthenticationRequest.class),
                @DiscriminatorMapping(value = "SOCIAL", schema = SocialAuthenticationRequest.class)
        }
)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "grantType",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = AccountAuthenticationRequest.class, name = "ACCOUNT"),
        @JsonSubTypes.Type(value = PhoneAuthenticationRequest.class, name = "PHONE"),
        @JsonSubTypes.Type(value = EmailAuthenticationRequest.class, name = "EMAIL"),
        @JsonSubTypes.Type(value = SocialAuthenticationRequest.class, name = "SOCIAL"),
})
public sealed interface AuthenticationRequest permits AccountAuthenticationRequest, PhoneAuthenticationRequest, EmailAuthenticationRequest, SocialAuthenticationRequest {

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
