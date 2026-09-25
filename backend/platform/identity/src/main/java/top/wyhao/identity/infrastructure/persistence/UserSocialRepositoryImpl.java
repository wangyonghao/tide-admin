package top.wyhao.identity.infrastructure.persistence;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import top.wyhao.identity.domain.gateway.UserSocialRepository;
import top.wyhao.identity.domain.model.SysUserSocial;
import top.wyhao.identity.infrastructure.persistence.mapper.SysUserSocialMapper;

import java.util.Collection;
import java.util.List;

/**
 * 用户第三方账号仓储实现。
 */
@Repository
@RequiredArgsConstructor
public class UserSocialRepositoryImpl implements UserSocialRepository {

    private final SysUserSocialMapper socialMapper;

    @Override
    public SysUserSocial findBySourceAndOpenId(String source, String openId) {
        return socialMapper.selectBySourceAndOpenId(source, openId);
    }

    @Override
    public void insert(SysUserSocial social) {
        socialMapper.insert(social);
    }

    @Override
    public void updateLoginMeta(SysUserSocial social) {
        socialMapper.lambdaUpdate()
                .set(SysUserSocial::getMetaJson, social.getMetaJson())
                .set(SysUserSocial::getLastLoginTime, social.getLastLoginTime())
                .eq(SysUserSocial::getSource, social.getSource())
                .eq(SysUserSocial::getOpenId, social.getOpenId())
                .update();
    }

    @Override
    public List<SysUserSocial> listByUserId(Long userId) {
        return socialMapper.lambdaQuery().eq(SysUserSocial::getUserId, userId).list();
    }

    @Override
    public void deleteBySourceAndUserId(String source, Long userId) {
        socialMapper.lambdaUpdate()
                .eq(SysUserSocial::getSource, source)
                .eq(SysUserSocial::getUserId, userId)
                .remove();
    }

    @Override
    public void deleteByUserIds(List<Long> userIds) {
        socialMapper.lambdaUpdate().in(SysUserSocial::getUserId, userIds).remove();
    }

    @Override
    public void deleteAll() {
        socialMapper.delete(Wrappers.<SysUserSocial>query().eq("1", 1));
    }

    @Override
    public void deleteExcludingUserIds(Collection<Long> keepUserIds) {
        socialMapper.lambdaUpdate().notIn(SysUserSocial::getUserId, keepUserIds).remove();
    }
}
