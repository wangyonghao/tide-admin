package top.wyhao.identity.app.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.text.CharSequenceUtil;
import com.alicp.jetcache.anno.CacheUpdate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import top.wyhao.cmn.core.constant.CacheConstants;
import top.wyhao.cmn.core.enums.GenderEnum;
import top.wyhao.cmn.core.enums.StatusEnum;
import top.wyhao.file.app.service.FileService;
import top.wyhao.file.domain.model.File;
import top.wyhao.identity.adapter.web.dto.UserBasicInfoUpdateReq;
import top.wyhao.identity.app.service.UserProfileService;
import top.wyhao.identity.client.PasswordApi;
import top.wyhao.identity.client.PasswordPolicyConfig;
import top.wyhao.identity.client.PasswordPolicyConfigApi;
import top.wyhao.identity.domain.exception.UserException;
import top.wyhao.identity.domain.gateway.UserRepository;
import top.wyhao.identity.domain.model.SysUser;
import top.wyhao.identity.domain.service.UserLifecycleRules;
import top.wyhao.identity.domain.service.UserUniquenessChecker;
import top.wyhao.security.client.RoleApi;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户自助操作实现（个人中心 / 注册）。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private final PasswordEncoder passwordEncoder;
    private final RoleApi roleApi;
    private final PasswordApi passwordApi;
    private final FileService fileService;
    private final UserRepository userRepository;
    private final PasswordPolicyConfigApi passwordPolicyConfigApi;
    private final UserUniquenessChecker uniquenessChecker;
    private final UserLifecycleRules lifecycleRules;

    @Value("${avatar.support-suffix}")
    private String[] avatarSupportSuffix;
    private static final long AVATAR_MAX_SIZE = 1024 * 1024 * 2;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long updateAvatar(MultipartFile avatarFile, Long userId) {
        checkAvatar(avatarFile);

        SysUser user = lifecycleRules.requireUser(userId);
        Long oldAvatarFileId = user.getAvatar();

        File uploaded = fileService.upload(avatarFile, userId);
        userRepository.updateAvatar(userId, uploaded.getId());

        if (oldAvatarFileId != null) {
            try {
                fileService.delete(oldAvatarFileId, userId);
            } catch (Exception e) {
                log.warn("删除旧头像文件失败: fileId={}", oldAvatarFileId, e);
            }
        }

        return uploaded.getId();
    }

    private void checkAvatar(MultipartFile avatarFile) {
        String avatarImageType = FileNameUtil.extName(avatarFile.getOriginalFilename());
        if (!CharSequenceUtil.equalsAnyIgnoreCase(avatarImageType, avatarSupportSuffix)) {
            throw UserException.avatarFormatNotSupported(String.join(",", avatarSupportSuffix));
        }

        long avatarSize = avatarFile.getSize();
        if (avatarSize > AVATAR_MAX_SIZE) {
            throw UserException.avatarSizeExceeded(AVATAR_MAX_SIZE / 1024 / 1024);
        }
    }

    @Override
    @CacheUpdate(key = "#userId", value = "#req.displayName", name = CacheConstants.USER_KEY_PREFIX)
    public void updateBasicInfo(UserBasicInfoUpdateReq req, Long userId) {
        lifecycleRules.requireUser(userId);
        userRepository.updateBasicInfo(userId, req.getDisplayName(), req.getGender());
    }

    @Override
    public void updatePhone(String newPhone, String oldPassword, Long userId) {
        passwordApi.assertMatches(userId, oldPassword);
        SysUser user = lifecycleRules.requireUser(userId);
        lifecycleRules.assertPhoneChanged(user.getPhone(), newPhone);
        uniquenessChecker.assertPhoneAvailable(newPhone, userId);
        SysUser updateUser = new SysUser();
        updateUser.setId(userId);
        updateUser.setPhone(newPhone);
        userRepository.updatePartial(updateUser);
    }

    @Override
    public void updateEmail(String newEmail, String oldPassword, Long userId) {
        SysUser user = lifecycleRules.requireUser(userId);
        passwordApi.assertMatches(userId, oldPassword);
        lifecycleRules.assertEmailChanged(user.getEmail(), newEmail);
        uniquenessChecker.assertEmailAvailable(newEmail, userId);

        SysUser updateUser = new SysUser();
        updateUser.setId(userId);
        updateUser.setEmail(newEmail);
        userRepository.updatePartial(updateUser);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysUser registerLocal(String username, String rawPassword, Long deptId, List<Long> roleIds) {
        uniquenessChecker.assertUsernameAvailable(username);
        if (CollUtil.isEmpty(roleIds)) {
            throw UserException.of("REGISTER_ROLE_REQUIRED", "未配置注册默认角色");
        }
        SysUser user = new SysUser();
        user.setUsername(username);
        user.setDisplayName(username);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setGender(GenderEnum.UNKNOWN.getValue());
        user.setDeptId(deptId);
        user.setStatus(StatusEnum.ENABLE.getValue());
        user.setIsBuiltin(false);
        user.setPwdUpdateTime(LocalDateTime.now());
        PasswordPolicyConfig passwordPolicy = passwordPolicyConfigApi.get();
        int expireDays = passwordPolicy != null && passwordPolicy.getPasswordExpireDays() != null
                ? passwordPolicy.getPasswordExpireDays()
                : 90;
        user.setPwdExpireDate(LocalDate.now().plusDays(expireDays));
        userRepository.insert(user);
        roleApi.assignRolesToUser(roleIds, user.getId());
        return user;
    }
}
