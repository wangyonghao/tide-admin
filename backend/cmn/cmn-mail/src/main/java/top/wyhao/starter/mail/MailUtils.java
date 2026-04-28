
package top.wyhao.starter.mail;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.extra.spring.SpringUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import top.wyhao.starter.core.constant.StringConstants;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

/**
 * 邮件工具类
 */

public class MailUtils {

    private MailUtils() {
    }

    /**
     * 发送文本邮件给单个人
     *
     * @param subject 主题
     * @param content 内容
     * @param to      收件人
     * @throws MessagingException /
     */
    public static void sendText(String to, String subject, String content) throws MessagingException {
        send(splitAddress(to), null, null, subject, content, false);
    }

    /**
     * 发送 HTML 邮件给单个人
     *
     * @param subject 主题
     * @param content 内容
     * @param to      收件人
     * @throws MessagingException /
     */
    public static void sendHtml(String to, String subject, String content) throws MessagingException {
        send(splitAddress(to), null, null, subject, content, true);
    }

    /**
     * 发送 HTML 邮件给单个人
     *
     * @param subject 主题
     * @param content 内容
     * @param to      收件人
     * @param files   附件列表
     * @throws MessagingException /
     */
    public static void sendHtml(String to, String subject, String content, File... files) throws MessagingException {
        send(splitAddress(to), null, null, subject, content, true, files);
    }

    /**
     * 发送 HTML 邮件给多个人
     *
     * @param subject 主题
     * @param content 内容
     * @param tos     收件人列表
     * @param files   附件列表
     * @throws MessagingException /
     */
    public static void sendHtml(Collection<String> tos,
                                String subject,
                                String content,
                                File... files) throws MessagingException {
        send(tos, null, null, subject, content, true, files);
    }

    /**
     * 发送 HTML 邮件给多个人
     *
     * @param subject 主题
     * @param content 内容
     * @param tos     收件人列表
     * @param ccs     抄送人列表
     * @param files   附件列表
     * @throws MessagingException /
     */
    public static void sendHtml(Collection<String> tos,
                                Collection<String> ccs,
                                String subject,
                                String content,
                                File... files) throws MessagingException {
        send(tos, ccs, null, subject, content, true, files);
    }

    /**
     * 发送 HTML 邮件给多个人
     *
     * @param subject 主题
     * @param content 内容
     * @param tos     收件人列表
     * @param ccs     抄送人列表
     * @param bccs    密送人列表
     * @param files   附件列表
     * @throws MessagingException /
     */
    public static void sendHtml(Collection<String> tos,
                                Collection<String> ccs,
                                Collection<String> bccs,
                                String subject,
                                String content,
                                File... files) throws MessagingException {
        send(tos, ccs, bccs, subject, content, true, files);
    }

    /**
     * 发送邮件给多个人
     *
     * @param tos     收件人列表
     * @param ccs     抄送人列表
     * @param bccs    密送人列表
     * @param subject 主题
     * @param content 内容
     * @param isHtml  是否是 HTML
     * @param files   附件列表
     * @throws MessagingException /
     */
    public static void send(Collection<String> tos,
                            Collection<String> ccs,
                            Collection<String> bccs,
                            String subject,
                            String content,
                            boolean isHtml,
                            File... files) throws MessagingException {
        Assert.isFalse(CollUtil.isEmpty(tos), "请至少指定一名收件人");
        MailAccount mailAccount = SpringUtil.getBean(MailAccount.class);
        JavaMailSender mailSender = getMailSender(mailAccount);
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        // 发信人
        messageHelper.setFrom(getFromAddress(mailAccount));
        // 收信人
        messageHelper.setTo(tos.toArray(String[]::new));
        // 抄送人
        if (CollUtil.isNotEmpty(ccs)) {
            messageHelper.setCc(ccs.toArray(String[]::new));
        }
        // 密送人
        if (CollUtil.isNotEmpty(bccs)) {
            messageHelper.setBcc(bccs.toArray(String[]::new));
        }
        // 主题
        messageHelper.setSubject(subject);
        // 内容
        messageHelper.setText(content, isHtml);
        // 附件
        if (ArrayUtil.isNotEmpty(files)) {
            for (File file : files) {
                messageHelper.addAttachment(file.getName(), file);
            }
        }
        // 发送邮件
        mailSender.send(mimeMessage);
    }

    /**
     * 将多个联系人转为列表，分隔符为逗号或者分号
     *
     * @param addresses 多个联系人，如果为空返回null
     * @return 联系人列表
     */
    private static List<String> splitAddress(String addresses) {
        if (CharSequenceUtil.isBlank(addresses)) {
            return new ArrayList<>(0);
        }
        List<String> result;
        if (CharSequenceUtil.contains(addresses, StringConstants.COMMA)) {
            result = CharSequenceUtil.splitTrim(addresses, StringConstants.COMMA);
        } else if (CharSequenceUtil.contains(addresses, StringConstants.SEMICOLON)) {
            result = CharSequenceUtil.splitTrim(addresses, StringConstants.SEMICOLON);
        } else {
            result = CollUtil.newArrayList(addresses);
        }
        return result;
    }

    /**
     * 动态创建 JavaMailSender
     * 根据数据库配置动态创建邮件发送器
     */
    private static JavaMailSender getMailSender(MailAccount mailAccount) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        // 从配置中获取邮件服务器信息

        mailSender.setHost(mailAccount.getHost());
        mailSender.setPort(mailAccount.getPort());
        mailSender.setUsername(mailAccount.getUsername());
        mailSender.setPassword(mailAccount.getPassword());
        mailSender.setDefaultEncoding("UTF-8");

        // 配置邮件属性
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.timeout", "10000");
        props.put("mail.smtp.connectiontimeout", "10000");

        if (mailAccount.isSslEnabled()) {
            props.put("mail.smtp.ssl.enable", "true");
            props.put("mail.smtp.ssl.trust", mailAccount.getHost());
            props.put("mail.smtp.ssl.checkserveridentity", "true");
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.socketFactory.port", String.valueOf(mailAccount.getPort()));
        } else {
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.starttls.required", "true");
        }
        return mailSender;
    }

    /**
     * 获取发件人地址
     */
    private static String getFromAddress(MailAccount mailAccount) {
        String fromName = mailAccount.getFrom();
        String username = mailAccount.getUsername();

        if (fromName != null && !fromName.isEmpty()) {
            return fromName + " <" + username + ">";
        }
        return username;
    }
}
