package top.wyhao.identity.domain.gateway;

/**
 * 历史密码重复校验。
 */
public interface PasswordReuseChecker {

    /**
     * 明文密码是否与最近若干次历史密码重复。
     */
    boolean isPasswordReused(Long userId, String password, int count);
}
