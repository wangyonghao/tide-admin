package top.wyhao.admin.cmn.mail;

/**
 * 读取当前发信账户。实现由系统设置模块提供。
 */
public interface MailConfigApi {

    /**
     * 获取邮件配置
     *
     * @return 邮件配置
     */
    MailConfigVO getMailConfig();
}
