
package top.wyhao.identity.app.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import me.zhyd.oauth.model.AuthUser;
import org.springframework.stereotype.Service;
import top.wyhao.identity.domain.exception.UserException;
import top.wyhao.identity.domain.gateway.UserSocialRepository;
import top.wyhao.identity.domain.model.SocialSource;
import top.wyhao.identity.domain.model.SysUserSocial;
import top.wyhao.identity.app.service.UserSocialService;
import top.wyhao.cmn.core.util.CollUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * 用户社交账号服务类
 *

 * @since 2025/12/7
 */
@Service
@RequiredArgsConstructor
public class UserSocialServiceImpl implements UserSocialService {

    private final UserSocialRepository userSocialRepository;

    @Override
    public SysUserSocial getBySourceAndOpenId(String source, String openId) {
        return userSocialRepository.findBySourceAndOpenId(source, openId);
    }

    @Override
    public void saveOrUpdate(SysUserSocial userSocial) {
        if (userSocial.getCreateTime() == null) {
            userSocialRepository.insert(userSocial);
        } else {
            userSocialRepository.updateLoginMeta(userSocial);
        }
    }

    @Override
    public List<SysUserSocial> listByUserId(Long userId) {
        return userSocialRepository.listByUserId(userId);
    }

    @Override
    public void bind(AuthUser authUser, Long userId) {
        String source = authUser.getSource();
        String openId = authUser.getUuid();
        List<SysUserSocial> userSocialList = this.listByUserId(userId);
        Set<String> boundSocialSet = CollUtils.mapToSet(userSocialList, SysUserSocial::getSource);
        String description = SocialSource.valueOf(source).getDescription();
        if (boundSocialSet.contains(source)) {
            throw UserException.socialAlreadyBound(description);
        }
        SysUserSocial userSocial = this.getBySourceAndOpenId(source, openId);
        if (userSocial != null) {
            throw UserException.socialBoundByOther(description, authUser.getUsername());
        }
        userSocial = new SysUserSocial();
        userSocial.setUserId(userId);
        userSocial.setSource(source);
        userSocial.setOpenId(openId);
        userSocial.setMetaJson(JSONUtil.toJsonStr(authUser));
        userSocial.setLastLoginTime(LocalDateTime.now());
        userSocialRepository.insert(userSocial);
    }

    @Override
    public void deleteBySourceAndUserId(String source, Long userId) {
        userSocialRepository.deleteBySourceAndUserId(source, userId);
    }

    @Override
    public void deleteByUserIds(List<Long> userIds) {
        if (CollUtil.isEmpty(userIds)) {
            return;
        }
        userSocialRepository.deleteByUserIds(userIds);
    }
}
