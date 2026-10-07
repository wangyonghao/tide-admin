package top.wyhao.security.app.service.impl;

import cn.dev33.satoken.exception.NotLoginException;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.wyhao.cmn.core.constant.SystemConstants;
import top.wyhao.cmn.core.util.ExceptionUtils;
import top.wyhao.cmn.core.util.RsaUtils;
import top.wyhao.common.satoken.util.LoginUtil;
import top.wyhao.identity.client.CredentialUser;
import top.wyhao.identity.client.PasswordPolicyConfig;
import top.wyhao.identity.client.PasswordPolicyConfigApi;
import top.wyhao.identity.client.UserApi;
import top.wyhao.identity.domain.model.PasswordHistory;
import top.wyhao.identity.domain.model.PasswordPolicy;
import top.wyhao.identity.domain.service.PasswordRules;
import top.wyhao.security.adapter.web.dto.AuthenticationRequest;
import top.wyhao.security.adapter.web.dto.RegisterRequest;
import top.wyhao.security.adapter.web.vo.AuthenticationResult;
import top.wyhao.security.app.assembler.LoginUserAssembler;
import top.wyhao.security.app.handler.Authenticator;
import top.wyhao.security.app.handler.AuthenticatorFactory;
import top.wyhao.security.app.handler.AuthenticatorHelper;
import top.wyhao.security.app.service.AuthenticationService;
import top.wyhao.security.client.AuthenticationException;
import top.wyhao.security.client.RegisterConfigApi;
import top.wyhao.security.client.RoleApi;
import top.wyhao.security.client.config.RegisterConfigVO;
import top.wyhao.web.http.ServletUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private static final String DEFAULT_REGISTER_ROLE_CODE = "general";

    private final AuthenticatorFactory authenticatorFactory;
    private final RegisterConfigApi registerConfigApi;
    private final UserApi userApi;
    private final RoleApi roleApi;
    private final PasswordPolicyConfigApi passwordPolicyConfigApi;
    private final PasswordRules passwordRules;
    private final LoginUserAssembler loginUserAssembler;

    @Override
    public AuthenticationResult authenticate(@Valid AuthenticationRequest authenticationRequest) {
        Authenticator authenticator = authenticatorFactory.getHandler(authenticationRequest.getGrantType());
        return authenticator.authenticate(authenticationRequest);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public AuthenticationResult register(@Valid RegisterRequest request) {
        RegisterConfigVO config = registerConfigApi.get();
        if (!config.isRegistrationEnabled()) {
            throw AuthenticationException.registrationDisabled();
        }

        String password = ExceptionUtils.exToNull(() -> RsaUtils.decryptByRsaPrivateKey(request.getPassword()));
        String confirmPassword = ExceptionUtils.exToNull(() -> RsaUtils.decryptByRsaPrivateKey(request.getConfirmPassword()));
        if (CharSequenceUtil.isBlank(password) || CharSequenceUtil.isBlank(confirmPassword)) {
            throw AuthenticationException.passwordDecryptFailed();
        }
        if (!StrUtil.equals(password, confirmPassword)) {
            throw AuthenticationException.passwordMismatch();
        }

        passwordRules.assertCompliant(password, toPolicy(passwordPolicyConfigApi.get()), request.getUsername(),
                PasswordHistory.empty());

        Long roleId = resolveDefaultRoleId(config.getDefaultRoleId());
        if (roleId == null) {
            throw AuthenticationException.registerRoleMissing();
        }

        CredentialUser user = userApi.registerLocalUser(
                request.getUsername(),
                password,
                SystemConstants.SUPER_DEPARTMENT_ID,
                List.of(roleId)
        );

        AuthenticatorHelper.checkUserStatus(user);
        AuthenticatorHelper.issueToken(user.id());
        AuthenticatorHelper.createSession(loginUserAssembler.assemble(user, "PC"), "PC");

        String ip = ServletUtils.getRequestIp();
        String userAgent = ServletUtils.getRequest() != null
                ? ServletUtils.getRequest().getHeader("User-Agent")
                : null;
        AuthenticatorHelper.recordSuccess(user.username(), ip, userAgent);

        return new AuthenticationResult("200", LoginUtil.getTokenValue(), null);
    }

    @Override
    public void logout() {
        try {
            LoginUtil.logout();
        } catch (NotLoginException ignored) {
        }
    }

    private Long resolveDefaultRoleId(String configuredRoleId) {
        if (StrUtil.isNotBlank(configuredRoleId)) {
            return Convert.toLong(configuredRoleId, null);
        }
        return roleApi.getIdByCode(DEFAULT_REGISTER_ROLE_CODE);
    }

    private PasswordPolicy toPolicy(PasswordPolicyConfig config) {
        if (config == null) {
            return new PasswordPolicy(
                    PasswordPolicy.MIN_LENGTH_LOWER,
                    PasswordPolicy.DEFAULT_MAX_LENGTH,
                    false,
                    false,
                    PasswordPolicy.REPETITION_TIMES_LOWER,
                    90
            );
        }
        return new PasswordPolicy(
                ObjectUtil.defaultIfNull(config.getPasswordMinLength(), PasswordPolicy.MIN_LENGTH_LOWER),
                PasswordPolicy.DEFAULT_MAX_LENGTH,
                Boolean.TRUE.equals(config.getPasswordRequireSpecial()),
                Boolean.TRUE.equals(config.getPasswordAllowContainUsername()),
                ObjectUtil.defaultIfNull(config.getPasswordRepetitionTimes(), PasswordPolicy.REPETITION_TIMES_LOWER),
                ObjectUtil.defaultIfNull(config.getPasswordExpireDays(), 90)
        );
    }
}
