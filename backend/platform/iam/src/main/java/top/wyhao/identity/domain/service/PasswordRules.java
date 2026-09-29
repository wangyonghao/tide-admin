package top.wyhao.identity.domain.service;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import top.wyhao.cmn.core.constant.RegexConstants;
import top.wyhao.identity.domain.exception.UserException;
import top.wyhao.identity.domain.gateway.PasswordReuseChecker;
import top.wyhao.identity.domain.model.PasswordPolicy;

/**
 * 设密 / 改密时的密码合规规则。
 */
@Component
@RequiredArgsConstructor
public class PasswordRules {

    private final PasswordReuseChecker passwordReuseChecker;

    /**
     * 校验明文密码是否满足策略；不通过则抛 {@link UserException}。
     */
    public void assertCompliant(String rawPassword, PasswordPolicy policy, long userId, String username) {
        assertMinLengthAndFormat(rawPassword, policy);
        assertSymbolsIfRequired(rawPassword, policy);
        assertNotContainUsername(rawPassword, policy, username);
        assertNotInHistory(rawPassword, policy, userId);
    }

    private void assertMinLengthAndFormat(String rawPassword, PasswordPolicy policy) {
        int minLength = policy.minLength();
        int maxLength = policy.maxLength();
        if (StrUtil.length(rawPassword) < minLength) {
            throw UserException.passwordPolicyViolated("密码最小长度为 %d 个字符".formatted(minLength));
        }
        if (!ReUtil.isMatch(RegexConstants.PASSWORD_TEMPLATE.formatted(minLength, maxLength), rawPassword)) {
            throw UserException.passwordFormatInvalid(minLength, maxLength);
        }
    }

    private void assertSymbolsIfRequired(String rawPassword, PasswordPolicy policy) {
        if (policy.requireSymbols() && !ReUtil.contains(RegexConstants.SPECIAL_CHARACTER, rawPassword)) {
            throw UserException.passwordPolicyViolated("密码必须包含特殊字符");
        }
    }

    private void assertNotContainUsername(String rawPassword, PasswordPolicy policy, String username) {
        if (policy.allowContainUsername() || CharSequenceUtil.isBlank(username)) {
            return;
        }
        if (CharSequenceUtil.containsAnyIgnoreCase(rawPassword, username, StrUtil.reverse(username))) {
            throw UserException.passwordPolicyViolated("密码不允许包含正反序用户名");
        }
    }

    private void assertNotInHistory(String rawPassword, PasswordPolicy policy, long userId) {
        int times = policy.historyRepetitionTimes();
        if (passwordReuseChecker.isPasswordReused(userId, rawPassword, times)) {
            throw UserException.passwordPolicyViolated("新密码不得与历史前 %d 次密码重复".formatted(times));
        }
    }
}
