
package top.wyhao.security.app.handler;

import cn.dev33.satoken.temp.SaTempUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import top.wyhao.security.client.AuthenticationException;
import top.wyhao.security.adapter.web.dto.AccountAuthenticationRequest;
import top.wyhao.security.adapter.web.dto.AuthenticationRequest;
import top.wyhao.security.domain.model.GrantType;
import top.wyhao.security.adapter.web.vo.AuthenticationResult;
import top.wyhao.security.app.assembler.LoginUserAssembler;
import top.wyhao.identity.client.CredentialUser;
import top.wyhao.security.client.LoginConfigApi;
import top.wyhao.security.client.config.LoginConfigVO;
import top.wyhao.identity.client.UserApi;
import top.wyhao.redisson.util.RedisUtils;
import top.wyhao.common.satoken.util.LoginUtil;
import top.wyhao.cmn.core.constant.RegexConstants;
import top.wyhao.cmn.core.exception.BizException;
import top.wyhao.cmn.core.util.ExceptionUtils;
import top.wyhao.cmn.core.util.RsaUtils;
import top.wyhao.web.http.ServletUtils;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Objects;

/**
 * 账号登录处理器
 *
 * @since 2025/5/18
 */
@Component
@RequiredArgsConstructor
public class AccountAuthenticator implements Authenticator {

    private static final String RETRY_KEY_PREFIX = "login:retry:";
    private static final String CAPTCHA_KEY = "login:captcha:";

    private final PasswordEncoder passwordEncoder;
    private final UserApi userApi;
    private final LoginConfigApi loginConfigApi;
    private final LoginUserAssembler loginUserAssembler;

    @Override
    public GrantType grantType() {
        return GrantType.ACCOUNT;
    }

    @Override
    public AuthenticationResult authenticate(AuthenticationRequest request) {
        AccountAuthenticationRequest req = (AccountAuthenticationRequest) request;
        // 解密密码
        String password = decryptPassword(req.getPassword());
        String ip = ServletUtils.getRequestIp();
        HttpServletRequest httpServletRequest = ServletUtils.getRequest();
        String userAgent = httpServletRequest != null ? httpServletRequest.getHeader("User-Agent") : null;

        String retryKey = buildRetryKey(req.getUsername(), ip);

        try {
            // 1. 校验图片验证码
            if (needCaptcha(retryKey)) {
                validateCaptcha(req.getUuid(), req.getCaptcha());
            }

            // 2. 重试次数检查
            checkRetryLimit(retryKey);

            // 3. 查询用户
            CredentialUser user = loadUser(req.getUsername(), retryKey, ip, userAgent);

            // 4. 校验密码
            validatePassword(user, password, retryKey, ip, userAgent);

            // 5. 检查用户状态
            AuthenticatorHelper.checkUserStatus(user);

            // 6. 密码过期时，强制用户修改密码
            AuthenticationResult expiredResult = handlePasswordExpired(user, ip, userAgent);
            if (expiredResult != null) {
                return expiredResult;
            }
            // 7. 登录（创建会话、签发Token）
            AuthenticatorHelper.issueToken(user.id());

            // 8. 保存用户信息到会话
            AuthenticatorHelper.createSession(loginUserAssembler.assemble(user, "PC"), "PC");

            // 9. 记录登录成功日志
            AuthenticatorHelper.recordSuccess(user.username(), ip, userAgent);

            return new AuthenticationResult("200", LoginUtil.getTokenValue(), null);
        } catch (BizException e) {
            // 如果是业务异常且还没记录日志，记录失败日志
            if (!"USERNAME_PASSWORD_ERROR".equals(e.getCode())) {
                AuthenticatorHelper.recordFailure(req.getUsername(), ip, userAgent, e.getMessage());
            }
            throw e;
        } catch (Exception e) {
            // 其他异常也记录失败日志
            AuthenticatorHelper.recordFailure(req.getUsername(), ip, userAgent, "系统异常: " + e.getMessage());
            throw e;
        }
    }


    private @Nullable AuthenticationResult handlePasswordExpired(CredentialUser user, String ip, String userAgent) {
        if (user.pwdExpireDate() != null && user.pwdExpireDate().isBefore(LocalDate.now())) {
            String tempToken = SaTempUtil.createToken(user.id(), 600); // 10分钟
            // 记录登录失败日志（密码过期）
            AuthenticatorHelper.recordFailure(user.username(), ip, userAgent, "密码已过期");
            return new AuthenticationResult("PASSWORD_EXPIRED", tempToken, null);
        }
        return null;
    }

    private void validatePassword(CredentialUser user, String password, String retryKey, String ip, String userAgent) {
        if (passwordEncoder.matches(password, user.password())) {
            clearRetryCount(retryKey);
            return;
        }
        incrementRetry(retryKey);
        // 记录登录失败日志
        AuthenticatorHelper.recordFailure(user.username(), ip, userAgent, "密码错误");
        throw handlePasswordError(retryKey);
    }

    public boolean needCaptcha(String retryKey) {
        int count = getRetryCount(retryKey);
        return count >= 2;
    }

    private AuthenticationException handlePasswordError(String retryKey) {
        if (needCaptcha(retryKey)) {
            return AuthenticationException.needCaptcha();
        } else if (exceedRetryLimit(retryKey)) {
            return AuthenticationException.passwordErrorExceeded();
        } else {
            return AuthenticationException.passwordError();
        }
    }

    private boolean exceedRetryLimit(String retryKey) {
        LoginConfigVO config = loginConfigApi.get();
        int remain = config.getMaxRetry() - getRetryCount(retryKey);
        return remain <= 0;
    }

    private String buildRetryMessage(String retryKey) {
        LoginConfigVO config = loginConfigApi.get();
        int remain = config.getMaxRetry() - getRetryCount(retryKey);
        return remain > 0
                ? "用户名或密码错误，还剩" + remain + "次机会"
                : "用户名或密码错误，账号已锁定";
    }

    private CredentialUser loadUser(String username, String retryKey, String ip, String userAgent) {
        CredentialUser user = userApi.findByUsername(username);

        if (Objects.nonNull(user)) {
            return user;
        }

        // 用户不存在，累计失败次数
        incrementRetry(retryKey);

        // 记录登录失败日志
        AuthenticatorHelper.recordFailure(username, ip, userAgent, "用户不存在");
        // 防止用户名探测，统一提示：用户名或密码错误
        throw handlePasswordError(retryKey);
    }


    private static @NonNull String buildRetryKey(String username, String ip) {
        return RETRY_KEY_PREFIX + username + ":" + ip;
    }

    private void validateCaptcha(String captchaUUID, String captchaValue) {
        // 校验验证码
        LoginConfigVO configVO = loginConfigApi.get();
        boolean loginCaptchaEnabled = configVO.getCaptchaEnabled();
        if (!loginCaptchaEnabled) {
            return;
        }
        if (StrUtil.isBlank(captchaValue)) {
            throw AuthenticationException.captchaRequired();
        }
        if (StrUtil.isBlank(captchaUUID)) {
            throw AuthenticationException.captchaInvalid();
        }
        String cachedCaptcha = RedisUtils.getAndDelete(CAPTCHA_KEY + captchaUUID);
        if (StrUtil.isBlank(cachedCaptcha)) {
            throw AuthenticationException.captchaExpired();
        }
    }

    private void incrementRetry(String retryKey) {
        RedisUtils.incr(retryKey);
        LoginConfigVO configVO = loginConfigApi.get();
        RedisUtils.expire(retryKey, Duration.ofMinutes(configVO.getLockTime()));
    }

    private int getRetryCount(String retryKey) {
        Integer count = RedisUtils.get(retryKey);
        return count != null ? count : 0;
    }

    /**
     * 清除密码错误次数
     *
     * @param retryKey 密码错误次数缓存的Key
     */
    private void clearRetryCount(String retryKey) {
        RedisUtils.delete(retryKey);
    }

    /**
     * 密码错误次数超过限制则锁定账号
     *
     * @param retryKey 密码错误次数缓存的Key
     */
    private void checkRetryLimit(String retryKey) {
        int retryCount = getRetryCount(retryKey);
        LoginConfigVO loginConfig = loginConfigApi.get();
        int maxRetry = loginConfig.getMaxRetry();
        if (retryCount >= maxRetry) {
            long ttl = RedisUtils.getTimeToLive(retryKey);
            throw AuthenticationException.accountLocked(ttl / 60);
        }
    }

    private String decryptPassword(String encryptedPassword) {
        String rawPassword = ExceptionUtils.exToNull(() -> RsaUtils.decryptByRsaPrivateKey(encryptedPassword));
        if (StrUtil.isBlank(rawPassword)) {
            throw AuthenticationException.passwordDecryptFailed();
        }
        if (!ReUtil.isMatch(RegexConstants.PASSWORD, rawPassword)) {
            throw AuthenticationException.passwordFormatInvalid();
        }
        return rawPassword;
    }

}