
package top.wyhao.security.app.handler;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.json.JSONUtil;
import com.xkcoding.justauth.autoconfigure.JustAuthProperties;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import me.zhyd.oauth.AuthRequestBuilder;
import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.model.AuthUser;
import me.zhyd.oauth.request.AuthRequest;
import org.springframework.stereotype.Component;
import top.wyhao.security.adapter.web.dto.AuthenticationRequest;
import top.wyhao.security.adapter.web.dto.SocialAuthenticationRequest;
import top.wyhao.security.domain.model.GrantType;
import top.wyhao.security.adapter.web.vo.AuthenticationResult;
import top.wyhao.security.app.assembler.LoginUserAssembler;
import top.wyhao.identity.client.CredentialUser;
import top.wyhao.identity.client.SocialLink;
import top.wyhao.identity.client.UserApi;
import top.wyhao.cmn.core.constant.SystemConstants;
import top.wyhao.notification.client.MessageNotifyApi;
import top.wyhao.security.client.RoleApi;
import top.wyhao.common.satoken.util.LoginUtil;
import top.wyhao.application.autoconfigure.ApplicationProperties;
import top.wyhao.cmn.core.constant.RegexConstants;
import top.wyhao.cmn.core.enums.GenderEnum;
import top.wyhao.cmn.core.enums.RoleCodeEnum;
import top.wyhao.security.client.AuthenticationException;
import top.wyhao.web.http.ServletUtils;

import java.util.Collections;

/**
 * 第三方账号登录处理器
 */
@Component
@RequiredArgsConstructor
public class SocialAuthenticator implements Authenticator {

    private final JustAuthProperties authProperties;
    private final ApplicationProperties applicationProperties;

    private final UserApi userApi;
    private final RoleApi roleApi;
    private final MessageNotifyApi messageNotifyApi;
    private final LoginUserAssembler loginUserAssembler;

    @Override
    public GrantType grantType() {
        return GrantType.SOCIAL;
    }

    @Override
    public AuthenticationResult authenticate(AuthenticationRequest request) {
        SocialAuthenticationRequest req = (SocialAuthenticationRequest) request;
        if (StpUtil.isLogin()) {
            StpUtil.logout();
        }
        // 获取第三方登录信息
        AuthRequest authRequest = this.getAuthRequest(req.getSource());
        AuthCallback callback = new AuthCallback();
        callback.setCode(req.getCode());
        callback.setState(req.getState());
        AuthResponse<AuthUser> response = authRequest.login(callback);
        if (!response.ok()) {
            throw AuthenticationException.socialAuthFailed(response.getMsg());
        }
        AuthUser authUser = response.getData();
        // 如未绑定则自动注册新用户，保存或更新关联信息
        String source = authUser.getSource();
        String openId = authUser.getUuid();
        SocialLink userSocial = userApi.findSocial(source, openId);
        CredentialUser user;
        if (userSocial == null) {
            String username = authUser.getUsername();
            String nickname = authUser.getNickname();
            CredentialUser existsUser = userApi.findByUsername(username);
            String randomStr = RandomUtil.randomString(RandomUtil.BASE_CHAR, 5);
            if (existsUser != null || !ReUtil.isMatch(RegexConstants.USERNAME, username)) {
                username = randomStr + IdUtil.fastSimpleUUID();
            }
            if (!ReUtil.isMatch(RegexConstants.GENERAL_NAME, nickname)) {
                nickname = source.toLowerCase() + randomStr;
            }
            Integer gender = null;
            if (authUser.getGender() != null) {
                gender = GenderEnum.getByValue(Integer.parseInt(authUser.getGender().getCode())).getValue();
            }
            user = userApi.registerSocialUser(username, nickname, gender, SystemConstants.SUPER_DEPT_ID);
            roleApi.assignRolesToUser(Collections.singletonList(roleApi.getIdByCode(RoleCodeEnum.GENERAL_USER.getCode())), user.id());
            this.sendSecurityMsg(user);
        } else {
            user = userApi.findById(userSocial.userId());
        }
        // 检查用户状态
        AuthenticatorHelper.checkUserStatus(user);
        userApi.saveSocialLogin(user.id(), source, openId, JSONUtil.toJsonStr(authUser));
        // 7. 登录（创建会话、签发Token）
        AuthenticatorHelper.issueToken(user.id());

        // 8. 保存用户信息到会话
        AuthenticatorHelper.createSession(loginUserAssembler.assemble(user, "PC"), "PC");

        // 9. 记录登录成功日志
        String ip = ServletUtils.getRequestIp();
        HttpServletRequest httpRequest = ServletUtils.getRequest();
        String userAgent = httpRequest != null ? httpRequest.getHeader("User-Agent") : null;
        AuthenticatorHelper.recordSuccess(user.username(), ip, userAgent);

        return new AuthenticationResult("200", LoginUtil.getTokenValue(), null);
    }

    /**
     * 获取 AuthRequest
     *
     * @param source 平台名称
     * @return AuthRequest
     */
    private AuthRequest getAuthRequest(String source) {
        try {
            AuthConfig authConfig = authProperties.getType().get(source.toUpperCase());
            return AuthRequestBuilder.builder().source(source).authConfig(authConfig).build();
        } catch (Exception e) {
            throw AuthenticationException.platformNotSupport(source);
        }
    }

    /**
     * 发送安全消息
     *
     * @param user 用户信息
     */
    private void sendSecurityMsg(CredentialUser user) {
        String title = "欢迎加入 %s".formatted(applicationProperties.getName());
        String content = "您好，%s！您已通过第三方账号完成注册。".formatted(user.nickname());
        messageNotifyApi.notifyUsers(title, content, "SECURITY", CollUtil.toList(user.id().toString()));
    }
}
