
package top.wyhao.starter.core.constant;

/**
 * 配置属性相关常量
 *
 * @author Charles7c
 * @since 1.1.1
 */
public interface PropertiesConstants {
    /**
     * ContiNew Starter
     */
    String WYHAO_STARTER = "wyhao-starter";

    /**
     * 启用配置
     */
    String ENABLED = "enabled";

    /**
     * Web 配置
     */
    String WEB = WYHAO_STARTER + StringConstants.DOT + "web";

    /**
     * Web-跨域配置
     */
    String WEB_CORS = WEB + StringConstants.DOT + "cors";

    /**
     * Web-响应配置
     */
    String WEB_RESPONSE = WEB + StringConstants.DOT + "response";


    /**
     * 限流配置
     */
    String RATE_LIMITER = WYHAO_STARTER + StringConstants.DOT + "rate-limiter";

    /**
     * 加密配置
     */
    String ENCRYPT = WYHAO_STARTER + StringConstants.DOT + "encrypt";

    /**
     * 加密-字段加密
     */
    String ENCRYPT_FIELD = ENCRYPT + StringConstants.DOT + "field";

    /**
     * 加密-API 加密
     */
    String ENCRYPT_API = ENCRYPT + StringConstants.DOT + "api";

    /**
     * 安全配置
     */
    String SECURITY = "security";

    /**
     * 安全-敏感词配置
     */
    String SECURITY_SENSITIVE_WORDS = SECURITY + StringConstants.DOT + "sensitive-words";


    /**
     * 链路追踪配置
     */
    String TRACE = WYHAO_STARTER + StringConstants.DOT + "trace";

    /**
     * 验证码配置
     */
    String CAPTCHA = WYHAO_STARTER + StringConstants.DOT + "captcha";

    /**
     * 图形验证码配置
     */
    String CAPTCHA_GRAPHIC = CAPTCHA + StringConstants.DOT + "graphic";

    /**
     * 行为验证码配置
     */
    String CAPTCHA_BEHAVIOR = CAPTCHA + StringConstants.DOT + "behavior";

    /**
     * 消息配置
     */
    String MESSAGING = WYHAO_STARTER + StringConstants.DOT + "messaging";

    /**
     * WebSocket 配置
     */
    String MESSAGING_WEBSOCKET = MESSAGING + StringConstants.DOT + "websocket";

    /**
     * 日志配置
     */
    String LOG = WYHAO_STARTER + StringConstants.DOT + "log";

    /**
     * 存储配置
     */
    String STORAGE = WYHAO_STARTER + StringConstants.DOT + "storage";

    /**
     * License 配置
     */
    String LICENSE = WYHAO_STARTER + StringConstants.DOT + "license";

    /**
     * License 生成器配置
     */
    String LICENSE_GENERATOR = LICENSE + StringConstants.DOT + "generator";

    /**
     * License 校验器配置
     */
    String LICENSE_VERIFIER = LICENSE + StringConstants.DOT + "verifier";

    /**
     * CRUD 配置
     */
    String CRUD = WYHAO_STARTER + StringConstants.DOT + "crud";

    /**
     * 数据权限配置
     */
    String DATA_PERMISSION = WYHAO_STARTER + StringConstants.DOT + "data-permission";

    /**
     * 租户配置
     */
    String TENANT = WYHAO_STARTER + StringConstants.DOT + "tenant";
}
