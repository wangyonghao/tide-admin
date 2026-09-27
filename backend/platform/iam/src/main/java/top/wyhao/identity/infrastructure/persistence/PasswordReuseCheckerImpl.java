package top.wyhao.identity.infrastructure.persistence;

import cn.hutool.core.collection.CollUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import top.wyhao.identity.domain.gateway.PasswordHistoryRepository;
import top.wyhao.identity.domain.gateway.PasswordReuseChecker;

import java.util.List;

/**
 * 基于历史密码哈希的重复校验。
 */
@Component
@RequiredArgsConstructor
public class PasswordReuseCheckerImpl implements PasswordReuseChecker {

    private final PasswordHistoryRepository passwordHistoryRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public boolean isPasswordReused(Long userId, String password, int count) {
        List<String> passwordList = passwordHistoryRepository.recentPasswords(userId, count);
        if (CollUtil.isEmpty(passwordList)) {
            return false;
        }
        return passwordList.stream().anyMatch(p -> passwordEncoder.matches(password, p));
    }
}
