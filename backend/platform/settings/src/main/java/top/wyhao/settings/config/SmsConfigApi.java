package top.wyhao.settings.config;

import top.wyhao.admin.cmn.sms.SmsConfig;

/**
 * 短信配置 API（由系统配置模块实现）
 */
public interface SmsConfigApi {

    /**
     * 获取短信通道配置
     */
    SmsConfig getSmsConfig();

    /**
     * 获取场景对应的短信模板
     */
    String getSmsTemplate(String scene);
}
