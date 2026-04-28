
package top.wyhao.admin.system.model.vo.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 邮件配置
 *
 * @author wyhao
 * @since 2024/04/26
 */
@Data
@Schema(description = "邮件配置")
public class EmailConfigVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * SMTP服务器地址
     */
    @Schema(description = "SMTP服务器地址", example = "smtp.example.com")
    private String host;

    /**
     * SMTP端口
     */
    @Schema(description = "SMTP端口", example = "465")
    private Integer port;

    /**
     * 发件人邮箱
     */
    @Schema(description = "发件人邮箱", example = "admin@example.com")
    private String username;

    /**
     * 邮箱密码/授权码（敏感字段）
     */
    @Schema(description = "邮箱密码/授权码", example = "******")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    /**
     * 发件人名称
     */
    @Schema(description = "发件人名称", example = "WYH Admin")
    private String fromName;
}
