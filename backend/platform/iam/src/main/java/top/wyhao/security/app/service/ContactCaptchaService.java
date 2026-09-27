package top.wyhao.security.app.service;

import cn.hutool.core.text.CharSequenceUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import top.wyhao.cmn.core.constant.CacheConstants;
import top.wyhao.security.client.AuthenticationException;
import top.wyhao.security.client.ContactCaptchaApi;
import top.wyhao.starter.cache.redisson.util.RedisUtils;

/**
 * 校验发到手机或邮箱的验证码。
 */
@Service
@RequiredArgsConstructor
public class ContactCaptchaService implements ContactCaptchaApi {

    @Override
    public void verifyPhone(String phone, String captcha) {
        String captchaKey = CacheConstants.CAPTCHA_KEY_PREFIX + phone;
        String cached = RedisUtils.get(captchaKey);
        if (CharSequenceUtil.isBlank(cached)) {
            throw AuthenticationException.captchaOutdated();
        }
        if (!CharSequenceUtil.equalsIgnoreCase(captcha, cached)) {
            throw AuthenticationException.captchaIncorrect();
        }
        RedisUtils.delete(captchaKey);
    }

    @Override
    public void verifyEmail(String email, String captcha) {
        String captchaKey = CacheConstants.CAPTCHA_KEY_PREFIX + email;
        String cached = RedisUtils.getAndDelete(captchaKey);
        if (CharSequenceUtil.isBlank(cached)) {
            throw AuthenticationException.captchaOutdated();
        }
        if (!CharSequenceUtil.equalsIgnoreCase(captcha, cached)) {
            throw AuthenticationException.captchaIncorrect();
        }
    }
}
