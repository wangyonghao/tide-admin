package top.wyhao.identity.adapter.client;

import cn.hutool.core.bean.BeanUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import top.wyhao.cmn.core.enums.StatusEnum;
import top.wyhao.identity.app.service.UserAdminService;
import top.wyhao.identity.app.service.UserProfileService;
import top.wyhao.identity.app.service.UserSocialService;
import top.wyhao.identity.client.CredentialUser;
import top.wyhao.identity.client.SocialLink;
import top.wyhao.identity.client.UserApi;
import top.wyhao.identity.client.UserProfile;
import top.wyhao.identity.client.UserProfileApi;
import top.wyhao.identity.domain.gateway.UserRepository;
import top.wyhao.identity.domain.model.SysUser;
import top.wyhao.identity.domain.model.SysUserSocial;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * 用户身份 API 实现
 */
@Service
@RequiredArgsConstructor
public class UserApiImpl implements UserApi, UserProfileApi {

    private final UserAdminService userAdminService;
    private final UserProfileService userProfileService;
    private final UserRepository userRepository;
    private final UserSocialService userSocialService;

    @Override
    public long countByDepartmentIds(Collection<Long> departmentIds) {
        return userAdminService.countByDepartmentIds(List.copyOf(departmentIds));
    }

    @Override
    public CredentialUser findById(Long id) {
        return toCredential(userRepository.findById(id));
    }

    @Override
    public CredentialUser findByUsername(String username) {
        return toCredential(userAdminService.getByUsername(username));
    }

    @Override
    public CredentialUser findByPhone(String phone) {
        return toCredential(userAdminService.getByPhone(phone));
    }

    @Override
    public CredentialUser findByEmail(String email) {
        return toCredential(userAdminService.getByEmail(email));
    }

    @Override
    public CredentialUser registerSocialUser(String username, String displayName, Integer gender, Long departmentId) {
        SysUser user = new SysUser();
        user.setUsername(username);
        user.setDisplayName(displayName);
        user.setGender(gender);
        user.setDepartmentId(departmentId);
        user.setStatus(StatusEnum.ENABLE.getValue());
        userAdminService.save(user);
        return toCredential(user);
    }

    @Override
    public CredentialUser registerLocalUser(String username, String rawPassword, Long departmentId, java.util.List<Long> roleIds) {
        return toCredential(userProfileService.registerLocal(username, rawPassword, departmentId, roleIds));
    }

    @Override
    public SocialLink findSocial(String source, String openId) {
        SysUserSocial social = userSocialService.getBySourceAndOpenId(source, openId);
        if (social == null) {
            return null;
        }
        return new SocialLink(social.getUserId(), social.getSource(), social.getOpenId());
    }

    @Override
    public void saveSocialLogin(Long userId, String source, String openId, String metaJson) {
        SysUserSocial social = userSocialService.getBySourceAndOpenId(source, openId);
        if (social == null) {
            social = new SysUserSocial();
        }
        social.setUserId(userId);
        social.setSource(source);
        social.setOpenId(openId);
        social.setMetaJson(metaJson);
        social.setLastLoginTime(LocalDateTime.now());
        userSocialService.saveOrUpdate(social);
    }

    @Override
    public UserProfile profile(Long id) {
        UserProfile profile = new UserProfile();
        BeanUtil.copyProperties(userAdminService.detail(id), profile);
        return profile;
    }

    private CredentialUser toCredential(SysUser user) {
        if (user == null) {
            return null;
        }
        return new CredentialUser(
                user.getId(),
                user.getUsername(),
                user.getDisplayName(),
                user.getPassword(),
                user.getStatus(),
                user.getGender(),
                user.getDepartmentId(),
                user.getPwdUpdateTime(),
                user.getPwdExpireDate()
        );
    }
}
