package top.wyhao.security.app.handler;

import cn.hutool.core.text.CharSequenceUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import top.wyhao.cmn.core.constant.CacheConstants;
import top.wyhao.identity.client.CredentialUser;
import top.wyhao.identity.client.UserApi;
import top.wyhao.identity.client.UserContextHolder;
import top.wyhao.security.adapter.web.dto.AuthenticationRequest;
import top.wyhao.security.adapter.web.dto.PhoneAuthenticationRequest;
import top.wyhao.security.adapter.web.vo.AuthenticationResult;
import top.wyhao.security.app.assembler.LoginUserAssembler;
import top.wyhao.security.client.AuthenticationException;
import top.wyhao.security.domain.model.GrantType;
import top.wyhao.starter.cache.redisson.util.RedisUtils;
import top.wyhao.starter.web.http.ServletUtils;

/**
 * 手机号登录处理器
 */
@RequiredArgsConstructor
@Component
public class PhoneAuthenticator implements Authenticator {
    private final UserApi userApi;
    private final LoginUserAssembler loginUserAssembler;

    @Override
    public GrantType grantType() {
        return GrantType.PHONE;
    }

    @Override
    public AuthenticationResult authenticate(AuthenticationRequest request) {
        PhoneAuthenticationRequest req = (PhoneAuthenticationRequest) request;
        this.preLogin(req);
        // 验证手机号
        CredentialUser user = userApi.findByPhone(req.getPhone());
        if (user == null) {
            throw AuthenticationException.phoneNotBound();
        }
        // 检查用户状态
        AuthenticatorHelper.checkUserStatus(user);

        // 7. 登录（创建会话、签发Token）
        AuthenticatorHelper.issueToken(user.id());

        // 8. 保存用户信息到会话
        AuthenticatorHelper.createSession(loginUserAssembler.assemble(user, "PC"), "PC");

        // 9. 记录登录成功日志
        String ip = ServletUtils.getRequestIp();
        HttpServletRequest httpServletRequest = ServletUtils.getRequest();
        String userAgent = httpServletRequest != null ? httpServletRequest.getHeader("User-Agent") : null;
        AuthenticatorHelper.recordSuccess(user.username(), ip, userAgent);

        return new AuthenticationResult("200", UserContextHolder.getToken(), null);
    }

    public void preLogin(PhoneAuthenticationRequest req) {
        String phone = req.getPhone();
        String captchaKey = CacheConstants.CAPTCHA_KEY_PREFIX + phone;
        String captcha = RedisUtils.get(captchaKey);
        if (CharSequenceUtil.isBlank(captcha)) {
            throw AuthenticationException.captchaOutdated();
        }
        if (!CharSequenceUtil.equalsIgnoreCase(req.getCaptcha(), captcha)) {
            throw AuthenticationException.captchaOutdated();
        }
        RedisUtils.delete(captchaKey);
    }
}
