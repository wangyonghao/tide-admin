package top.wyhao.identity.domain.gateway;

import top.wyhao.identity.domain.model.SysUserSocial;

import java.util.Collection;
import java.util.List;

/**
 * 用户第三方账号仓储。
 */
public interface UserSocialRepository {

    SysUserSocial findBySourceAndOpenId(String source, String openId);

    void insert(SysUserSocial social);

    void updateLoginMeta(SysUserSocial social);

    List<SysUserSocial> listByUserId(Long userId);

    void deleteBySourceAndUserId(String source, Long userId);

    void deleteByUserIds(List<Long> userIds);

    void deleteAll();

    void deleteExcludingUserIds(Collection<Long> keepUserIds);
}
