
package top.wyhao.security.app.handler;

import cn.hutool.core.text.CharSequenceUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import top.wyhao.security.client.AuthenticationException;
import top.wyhao.security.adapter.web.dto.EmailAuthenticationRequest;
import top.wyhao.security.adapter.web.dto.AuthenticationRequest;
import top.wyhao.security.domain.model.GrantType;
import top.wyhao.security.adapter.web.vo.AuthenticationResult;
import top.wyhao.security.app.AuthenticatedUsers;
import top.wyhao.identity.client.CredentialUser;
import top.wyhao.identity.client.UserApi;
import top.wyhao.starter.cache.redisson.util.RedisUtils;
import top.wyhao.identity.client.UserContextHolder;
import top.wyhao.cmn.core.constant.CacheConstants;
import top.wyhao.starter.web.http.ServletUtils;

/**
 * 邮箱登录处理器
 */

@RequiredArgsConstructor
@Component
public class EmailAuthenticationHandler implements AuthenticationHandler {
    private final UserApi userApi;

    @Override
    public GrantType grantType() {
        return GrantType.EMAIL;
    }

    public AuthenticationResult authenticate(AuthenticationRequest request) {
        EmailAuthenticationRequest req = (EmailAuthenticationRequest)request;
        String email = req.getEmail();
        String captchaKey = CacheConstants.CAPTCHA_KEY_PREFIX + email;
        String captcha = RedisUtils.get(captchaKey);
        if (CharSequenceUtil.isBlank(captcha)) {
            throw AuthenticationException.captchaOutdated();
        }
        if (!CharSequenceUtil.equalsIgnoreCase(req.getCaptcha(), captcha)) {
            throw AuthenticationException.captchaIncorrect();
        }
        RedisUtils.delete(captchaKey);
        // 验证邮箱
        CredentialUser user = userApi.findByEmail(req.getEmail());
        if (user == null) {
            throw AuthenticationException.emailNotBound();
        }
        // 检查用户状态
        AuthenticationHandlerHelper.checkUserStatus(user);

        // 7. 登录（创建会话、签发Token）
        AuthenticationHandlerHelper.issueToken(user.id());

        // 8. 保存用户信息到会话
        AuthenticationHandlerHelper.setSession(AuthenticatedUsers.from(user), "PC");

        // 9. 记录登录成功日志
        String ip = ServletUtils.getRequestIp();
        HttpServletRequest httpServletRequest = ServletUtils.getRequest();
        String userAgent = httpServletRequest != null ? httpServletRequest.getHeader("User-Agent") : null;
        AuthenticationHandlerHelper.recordSuccess(user.username(), ip, userAgent);

        return new AuthenticationResult("200", UserContextHolder.getToken(), null);
    }
}