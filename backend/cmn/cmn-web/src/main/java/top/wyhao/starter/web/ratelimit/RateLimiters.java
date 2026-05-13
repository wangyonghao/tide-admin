
package top.wyhao.starter.web.ratelimit;

import java.lang.annotation.*;

/**
 * 限流组注解
 *
 * @author KAI
 * @since 2.2.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RateLimiters {

    /**
     * 限流组
     */
    RateLimiter[] value();
}
