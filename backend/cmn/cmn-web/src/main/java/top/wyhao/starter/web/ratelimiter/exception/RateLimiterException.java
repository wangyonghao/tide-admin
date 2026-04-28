
package top.wyhao.starter.web.ratelimiter.exception;

import top.wyhao.starter.core.exception.BusinessException;
import top.wyhao.starter.core.exception.SystemException;

/**
 * 限流异常
 *
 * @author KAI
 * @since 2.2.0
 */
public class RateLimiterException extends SystemException {

    public RateLimiterException(String message) {
        super(message);
    }

    public RateLimiterException(String message, Throwable cause) {
        super(message, cause);
    }
}
