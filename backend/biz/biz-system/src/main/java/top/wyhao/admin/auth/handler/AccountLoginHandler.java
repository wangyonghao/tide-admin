
package top.wyhao.admin.auth.handler;

import cn.dev33.satoken.temp.SaTempUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import top.wyhao.admin.auth.exception.AuthException;
import top.wyhao.admin.auth.model.dto.AccountLoginRequest;
import top.wyhao.admin.auth.model.dto.LoginRequest;
import top.wyhao.admin.auth.model.enums.GrantType;
import top.wyhao.admin.auth.model.vo.LoginResult;
import top.wyhao.admin.system.assembler.UserAssembler;
import top.wyhao.admin.system.entity.SysUser;
import top.wyhao.admin.system.model.result.config.LoginConfigVO;
import top.wyhao.admin.system.service.ConfigService;
import top.wyhao.admin.system.service.UserService;
import top.wyhao.starter.cache.redisson.util.RedisUtils;
import top.wyhao.starter.core.UserContextHolder;
import top.wyhao.starter.core.constant.RegexConstants;
import top.wyhao.admin.auth.exception.AuthException;
import top.wyhao.starter.core.exception.BizException;
import top.wyhao.starter.core.util.ExceptionUtils;
import top.wyhao.starter.core.util.RsaUtils;
import top.wyhao.starter.core.util.validation.ValidationUtils;
import top.wyhao.starter.web.http.ServletUtils;

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
public class AccountLoginHandler implements LoginHandler {

    private static final String RETRY_KEY_PREFIX = "login:retry:";
    private static final String CAPTCHA_KEY = "login:captcha:";

    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final ConfigService configService;
    private final UserAssembler userAssembler;

    @Override
    public GrantType grantType() {
        return GrantType.ACCOUNT;
    }

    @Override
    public LoginResult login(LoginRequest request) {
        AccountLoginRequest req = (AccountLoginRequest) request;
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
            SysUser user = loadUser(req.getUsername(), retryKey, ip, userAgent);

            // 4. 校验密码
            validatePassword(user, password, retryKey, ip, userAgent);

            // 5. 检查用户状态
            LoginHandlerHelper.checkUserStatus(user);

            // 6. 密码过期时，强制用户修改密码
            LoginResult expiredResult = handlePasswordExpired(user, ip, userAgent);
            if (expiredResult != null) {
                return expiredResult;
            }
            // 7. 登录（创建会话、签发Token）
            LoginHandlerHelper.doLogin(user.getId());

            // 8. 保存用户信息到会话
            LoginHandlerHelper.setSession(userAssembler.toLoginUser(user), "PC");

            // 9. 记录登录成功日志
            LoginHandlerHelper.loginSuccess(user.getUsername(), ip, userAgent);

            return new LoginResult("200", UserContextHolder.getToken(), null);
        } catch (BizException e) {
            // 如果是业务异常且还没记录日志，记录失败日志
            if (!"USERNAME_PASSWORD_ERROR".equals(e.getCode())) {
                LoginHandlerHelper.loginFail(req.getUsername(), ip, userAgent, e.getMessage());
            }
            throw e;
        } catch (Exception e) {
            // 其他异常也记录失败日志
            LoginHandlerHelper.loginFail(req.getUsername(), ip, userAgent, "系统异常: " + e.getMessage());
            throw e;
        }
    }


    private @Nullable LoginResult handlePasswordExpired(SysUser user, String ip, String userAgent) {
        if (user.getPwdExpireDate() != null && user.getPwdExpireDate().isBefore(LocalDate.now())) {
            String tempToken = SaTempUtil.createToken(user.getId(), 600); // 10分钟
            // 记录登录失败日志（密码过期）
            LoginHandlerHelper.loginFail(user.getUsername(), ip, userAgent, "密码已过期");
            return new LoginResult("PASSWORD_EXPIRED", tempToken, null);
        }
        return null;
    }

    private void validatePassword(SysUser user, String password, String retryKey, String ip, String userAgent) {
        if (passwordEncoder.matches(password, user.getPassword())) {
            clearRetryCount(retryKey);
            return;
        }
        incrementRetry(retryKey);
        // 记录登录失败日志
        LoginHandlerHelper.loginFail(user.getUsername(), ip, userAgent, "密码错误");
        throw handlePasswordError(retryKey);
    }

    public boolean needCaptcha(String retryKey) {
        int count = getRetryCount(retryKey);
        return count >= 2;
    }

    private AuthException handlePasswordError(String retryKey) {
        if (needCaptcha(retryKey)) {
            return AuthException.needCaptcha();
        } else if (exceedRetryLimit(retryKey)) {
            return AuthException.passwordErrorExceeded();
        } else {
            return AuthException.passwordError();
        }
    }

    private boolean exceedRetryLimit(String retryKey) {
        LoginConfigVO config = configService.getLoginConfig();
        int remain = config.getMaxRetry() - getRetryCount(retryKey);
        return remain <= 0;
    }

    private String buildRetryMessage(String retryKey) {
        LoginConfigVO config = configService.getLoginConfig();
        int remain = config.getMaxRetry() - getRetryCount(retryKey);
        return remain > 0
                ? "用户名或密码错误，还剩" + remain + "次机会"
                : "用户名或密码错误，账号已锁定";
    }

    private SysUser loadUser(String username, String retryKey, String ip, String userAgent) {
        SysUser user = userService.getByUsername(username);

        if (Objects.nonNull(user)) {
            return user;
        }

        // 用户不存在，累计失败次数
        incrementRetry(retryKey);

        // 记录登录失败日志
        LoginHandlerHelper.loginFail(username, ip, userAgent, "用户不存在");
        // 防止用户名探测，统一提示：用户名或密码错误
        throw handlePasswordError(retryKey);
    }


    private static @NonNull String buildRetryKey(String username, String ip) {
        return RETRY_KEY_PREFIX + username + ":" + ip;
    }

    private void validateCaptcha(String captchaUUID, String captchaValue) {
        // 校验验证码
        LoginConfigVO configVO = configService.getLoginConfig();
        boolean loginCaptchaEnabled = configVO.getCaptchaEnabled();
        if (!loginCaptchaEnabled) {
            return;
        }
        if (StrUtil.isBlank(captchaValue)) {
            throw AuthException.captchaRequired();
        }
        if (StrUtil.isBlank(captchaUUID)) {
            throw AuthException.captchaInvalid();
        }
        String cachedCaptcha = RedisUtils.getAndDelete(CAPTCHA_KEY + captchaUUID);
        if (StrUtil.isBlank(cachedCaptcha)) {
            throw AuthException.captchaExpired();
        }
    }

    private void incrementRetry(String retryKey) {
        RedisUtils.incr(retryKey);
        LoginConfigVO configVO = configService.getLoginConfig();
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
        LoginConfigVO loginConfig = configService.getLoginConfig();
        int maxRetry = loginConfig.getMaxRetry();
        if (retryCount >= maxRetry) {
            long ttl = RedisUtils.getTimeToLive(retryKey);
            throw AuthException.accountLocked(ttl / 60);
        }
    }

    private String decryptPassword(String encryptedPassword) {
        String rawPassword = ExceptionUtils.exToNull(() -> RsaUtils.decryptByRsaPrivateKey(encryptedPassword));
        ValidationUtils.throwIfBlank(rawPassword, "密码解密失败");
        ValidationUtils.throwIf(!ReUtil.isMatch(RegexConstants.PASSWORD, rawPassword), "密码长度为 8-32 个字符，支持大小写字母、数字、特殊字符，至少包含字母和数字");
        return rawPassword;
    }

}