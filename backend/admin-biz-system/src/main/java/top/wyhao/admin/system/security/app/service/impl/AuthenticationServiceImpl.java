package top.wyhao.admin.system.security.app.service.impl;

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
import top.wyhao.satoken.util.LoginUtil;
import top.wyhao.admin.system.identity.client.CredentialUser;
import top.wyhao.admin.system.identity.client.PasswordPolicyConfig;
import top.wyhao.admin.system.identity.client.PasswordPolicyConfigApi;
import top.wyhao.admin.system.identity.client.UserApi;
import top.wyhao.admin.system.identity.domain.model.PasswordHistory;
import top.wyhao.admin.system.identity.domain.model.PasswordPolicy;
import top.wyhao.admin.system.identity.domain.service.PasswordRules;
import top.wyhao.admin.system.security.adapter.web.dto.AuthenticationRequest;
import top.wyhao.admin.system.security.adapter.web.dto.RegisterRequest;
import top.wyhao.admin.system.security.adapter.web.vo.AuthenticationResult;
import top.wyhao.admin.system.identity.app.service.OrgService;
import top.wyhao.admin.system.security.app.assembler.LoginUserAssembler;
import top.wyhao.admin.system.security.app.handler.Authenticator;
import top.wyhao.admin.system.security.app.handler.AuthenticatorFactory;
import top.wyhao.admin.system.security.app.handler.AuthenticatorHelper;
import top.wyhao.admin.system.security.app.service.AuthenticationService;
import top.wyhao.admin.system.security.client.AuthenticationException;
import top.wyhao.admin.system.security.client.RegisterConfigApi;
import top.wyhao.security.client.RoleApi;
import top.wyhao.admin.system.security.client.config.RegisterConfigVO;
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
    private final OrgService orgService;

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
        Long orgId = orgService.resolveLoginTenantId(user.id());
        AuthenticatorHelper.createSession(loginUserAssembler.assemble(user, "PC"), "PC", orgId);

        String ip = ServletUtils.getRequestIp();
        String userAgent = ServletUtils.getRequest() != null
                ? ServletUtils.getRequest().getHeader("User-Agent")
                : null;
        AuthenticatorHelper.recordSuccess(user.username(), ip, userAgent);

        return AuthenticatorHelper.successResult();
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
