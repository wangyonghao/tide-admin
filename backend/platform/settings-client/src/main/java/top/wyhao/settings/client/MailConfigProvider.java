
package top.wyhao.settings.client;

import top.wyhao.cmn.core.model.MailConfig;

/**
 * 邮件配置器
 * <p>
 * 由biz-system提供邮件配置
 * </p>
 *

 * @since 2026/5/10
 */
public interface MailConfigProvider {

    /**
     * 获取邮件配置
     *
     * @return 邮件配置
     */
    MailConfig getMailConfig();
}
