
package top.wyhao.starter.captcha.behavior.autoconfigure.cache;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.NumberUtil;
import com.anji.captcha.service.CaptchaCacheService;
import top.wyhao.starter.cache.redisson.util.RedisUtils;
import top.wyhao.starter.captcha.behavior.enums.StorageType;

import java.time.Duration;

/**
 * 行为验证码 Redis 缓存实现
 *
 * @author Bull-BCLS
 * @since 1.1.0
 */
public class BehaviorCaptchaCacheServiceImpl implements CaptchaCacheService {
    @Override
    public void set(String key, String value, long expiresInSeconds) {
        if (NumberUtil.isNumber(value)) {
            RedisUtils.set(key, Convert.toInt(value), Duration.ofSeconds(expiresInSeconds));
        } else {
            RedisUtils.set(key, value, Duration.ofSeconds(expiresInSeconds));
        }
    }

    @Override
    public boolean exists(String key) {
        return RedisUtils.exists(key);
    }

    @Override
    public void delete(String key) {
        RedisUtils.delete(key);
    }

    @Override
    public String get(String key) {
        return Convert.toStr(RedisUtils.get(key));
    }

    @Override
    public String type() {
        return StorageType.REDIS.name().toLowerCase();
    }

    @Override
    public Long increment(String key, long val) {
        return RedisUtils.incr(key);
    }
}
