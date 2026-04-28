
package top.wyhao.starter.web.ratelimiter.enums;

/**
 * 限流类型
 *
 * @author KAI
 * @since 2.2.0
 */
public enum LimitType {

    /**
     * 全局限流
     */
    DEFAULT,

    /**
     * 根据 IP 限流
     */
    IP,

    /**
     * 根据实例限流（支持集群多实例）
     */
    CLUSTER
}
