
package top.wyhao.starter.web.ratelimit;

import top.wyhao.starter.core.exception.SystemException;

/**
 * 限流异常
 */
public class RateLimiterException extends SystemException {

    public RateLimiterException(String message) {
        super(message);
    }

    public RateLimiterException(String message, Throwable cause) {
        super(message, cause);
    }
}
