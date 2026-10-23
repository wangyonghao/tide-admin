package top.wyhao.admin.cmn.mail;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * SMTP 发信账户，同时作为邮件设置接口的读写载荷。
 */
@Data
public class MailConfigVO {

    private String host;

    private Integer port;

    private String username;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private String from;

    private boolean sslEnabled;
}
