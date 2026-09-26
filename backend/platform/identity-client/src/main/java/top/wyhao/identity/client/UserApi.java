package top.wyhao.identity.client;

import java.time.LocalDateTime;
import java.util.Collection;

/**
 * 用户身份 API（身份域对外提供）
 */
public interface UserApi {

    /**
     * 统计部门下的用户数
     */
    long countByDeptIds(Collection<Long> deptIds);

    CredentialUser findById(Long id);

    CredentialUser findByUsername(String username);

    CredentialUser findByPhone(String phone);

    CredentialUser findByEmail(String email);

    /**
     * 第三方登录首次进入时注册用户，状态为启用。
     */
    CredentialUser registerSocialUser(String username, String nickname, Integer gender, Long deptId);

    SocialLink findSocial(String source, String openId);

    void saveSocialLogin(Long userId, String source, String openId, String metaJson);

    UserProfile profile(Long id);
}
