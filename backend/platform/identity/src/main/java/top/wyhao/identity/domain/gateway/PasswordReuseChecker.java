package top.wyhao.identity.domain.gateway;

/**
 * 历史密码重复校验。
 */
public interface PasswordReuseChecker {

    /**
     * 明文密码是否与最近若干次历史密码重复。
     *
     * @param userId   用户 ID
     * @param password 明文密码
     * @param count    最近 N 次
     * @return 是否重复
     */
    boolean isPasswordReused(Long userId, String password, int count);
}
