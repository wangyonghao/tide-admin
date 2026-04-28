
package top.wyhao.starter.mail;

import org.springframework.mail.MailPreparationException;
import top.wyhao.starter.core.util.validation.ValidationUtils;

/**
 * 邮件配置
 *
 * @author Charles7c
 * @since 2.1.0
 */
public interface MailConfigurer {

    /**
     * 获取邮件配置
     *
     * @return 邮件配置
     */
    MailAccount getMailAccount();

    default void checkMailAccount(MailAccount mailAccount) throws MailPreparationException {
        ValidationUtils.throwIfBlank(mailAccount.getHost(), "邮件配置不正确：服务器地址不能为空");
        ValidationUtils.throwIfNull(mailAccount.getPort(), "邮件配置不正确：服务器端口不能为空");
        ValidationUtils.throwIfBlank(mailAccount.getUsername(), "邮件配置不正确：用户名不能为空");
        ValidationUtils.throwIfBlank(mailAccount.getPassword(), "邮件配置不正确：密码不能为空");
    }
}
