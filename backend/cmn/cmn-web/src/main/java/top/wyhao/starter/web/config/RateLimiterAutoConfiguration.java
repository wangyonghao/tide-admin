
package top.wyhao.starter.web.config;

import jakarta.annotation.PostConstruct;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import top.wyhao.starter.cache.redisson.autoconfigure.RedissonAutoConfiguration;
import top.wyhao.starter.core.constant.PropertiesConstants;
import top.wyhao.starter.web.ratelimiter.aop.RateLimiterAspect;
import top.wyhao.starter.web.ratelimiter.generator.DefaultRateLimiterNameGenerator;
import top.wyhao.starter.web.ratelimiter.generator.RateLimiterNameGenerator;

/**
 * 限流器自动配置
 *
 * @author KAI
 * @author Charles7c
 * @since 2.2.0
 */
@AutoConfiguration(after = RedissonAutoConfiguration.class)
@EnableConfigurationProperties(RateLimiterAutoConfiguration.RateLimiterProperties.class)
@ConditionalOnProperty(prefix = PropertiesConstants.RATE_LIMITER, name = PropertiesConstants.ENABLED, havingValue = "true", matchIfMissing = true)
public class RateLimiterAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(RateLimiterAutoConfiguration.class);

    /**
     * 限流器切面
     */
    @Bean
    public RateLimiterAspect rateLimiterAspect(RateLimiterNameGenerator rateLimiterNameGenerator,
                                               RedissonClient redissonClient) {
        return new RateLimiterAspect(rateLimiterNameGenerator, redissonClient);
    }

    /**
     * 限流器名称生成器
     */
    @Bean
    @ConditionalOnMissingBean
    public RateLimiterNameGenerator nameGenerator() {
        return new DefaultRateLimiterNameGenerator();
    }

    @PostConstruct
    public void postConstruct() {
        log.debug("[ContiNew Starter] - Auto Configuration 'RateLimiter' completed initialization.");
    }

    @ConfigurationProperties(PropertiesConstants.RATE_LIMITER)
    class RateLimiterProperties {
        private boolean enabled = true;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }
}
