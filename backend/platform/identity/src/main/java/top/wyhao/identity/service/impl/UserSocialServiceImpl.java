
package top.wyhao.identity.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import me.zhyd.oauth.model.AuthUser;
import org.springframework.stereotype.Service;
import top.wyhao.identity.entity.SysUserSocial;
import top.wyhao.identity.exception.UserException;
import top.wyhao.identity.mapper.SysUserSocialMapper;
import top.wyhao.identity.model.enums.SocialSource;
import top.wyhao.identity.service.UserSocialService;
import top.wyhao.starter.core.util.CollUtils;

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

    private final SysUserSocialMapper baseMapper;

    @Override
    public SysUserSocial getBySourceAndOpenId(String source, String openId) {
        return baseMapper.selectBySourceAndOpenId(source, openId);
    }

    @Override
    public void saveOrUpdate(SysUserSocial userSocial) {
        if (userSocial.getCreateTime() == null) {
            baseMapper.insert(userSocial);
        } else {
            baseMapper.lambdaUpdate()
                .set(SysUserSocial::getMetaJson, userSocial.getMetaJson())
                .set(SysUserSocial::getLastLoginTime, userSocial.getLastLoginTime())
                .eq(SysUserSocial::getSource, userSocial.getSource())
                .eq(SysUserSocial::getOpenId, userSocial.getOpenId())
                .update();
        }
    }

    @Override
    public List<SysUserSocial> listByUserId(Long userId) {
        return baseMapper.lambdaQuery().eq(SysUserSocial::getUserId, userId).list();
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
        baseMapper.insert(userSocial);
    }

    @Override
    public void deleteBySourceAndUserId(String source, Long userId) {
        baseMapper.lambdaUpdate().eq(SysUserSocial::getSource, source).eq(SysUserSocial::getUserId, userId).remove();
    }

    @Override
    public void deleteByUserIds(List<Long> userIds) {
        if (CollUtil.isEmpty(userIds)) {
            return;
        }
        baseMapper.lambdaUpdate().in(SysUserSocial::getUserId, userIds).remove();
    }
}
