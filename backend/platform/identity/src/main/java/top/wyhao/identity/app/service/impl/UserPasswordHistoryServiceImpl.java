
package top.wyhao.identity.app.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.wyhao.identity.app.service.UserPasswordHistoryService;
import top.wyhao.identity.domain.gateway.PasswordHistoryRepository;
import top.wyhao.identity.domain.gateway.PasswordReuseChecker;

import java.util.List;

/**
 * 用户历史密码业务实现
 *

 * @since 2024/5/16 21:58
 */
@Service
@RequiredArgsConstructor
public class UserPasswordHistoryServiceImpl implements UserPasswordHistoryService, PasswordReuseChecker {

    private final PasswordHistoryRepository passwordHistoryRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(Long userId, String password, int count) {
        if (StrUtil.isBlank(password)) {
            return;
        }
        passwordHistoryRepository.add(userId, password, count);
    }

    @Override
    public void deleteByUserIds(List<Long> userIds) {
        if (CollUtil.isEmpty(userIds)) {
            return;
        }
        passwordHistoryRepository.deleteByUserIds(userIds);
    }

    @Override
    public boolean isPasswordReused(Long userId, String password, int count) {
        List<String> passwordList = passwordHistoryRepository.recentPasswords(userId, count);
        if (CollUtil.isEmpty(passwordList)) {
            return false;
        }
        return passwordList.stream().anyMatch(p -> passwordEncoder.matches(password, p));
    }
}