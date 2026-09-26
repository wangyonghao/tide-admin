package top.wyhao.identity.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import top.wyhao.identity.domain.exception.UserException;
import top.wyhao.identity.domain.gateway.UserRepository;

/**
 * 用户身份唯一性校验。
 */
@Component
@RequiredArgsConstructor
public class UserUniquenessChecker {

    private final UserRepository userRepository;

    public void assertUsernameAvailable(String username) {
        if (userRepository.existsUsername(username)) {
            throw UserException.usernameExists();
        }
    }

    public void assertEmailAvailable(String email, Long excludeUserId) {
        if (userRepository.existsEmail(email, excludeUserId)) {
            throw UserException.emailExists();
        }
    }

    public void assertPhoneAvailable(String phone, Long excludeUserId) {
        if (userRepository.existsPhone(phone, excludeUserId)) {
            throw UserException.phoneExists();
        }
    }
}
