
package top.wyhao.identity.adapter.web;

import cn.hutool.core.text.CharSequenceUtil;
import com.xkcoding.justauth.autoconfigure.JustAuthProperties;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import me.zhyd.oauth.AuthRequestBuilder;
import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.model.AuthUser;
import me.zhyd.oauth.request.AuthRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.wyhao.identity.domain.model.SysUserSocial;
import top.wyhao.identity.domain.exception.UserException;
import top.wyhao.identity.adapter.web.dto.UserBasicInfoUpdateReq;
import top.wyhao.identity.domain.model.SocialSource;
import top.wyhao.identity.adapter.web.result.user.UserSocialBindResp;
import top.wyhao.identity.app.service.UserService;
import top.wyhao.identity.app.service.UserSocialService;
import top.wyhao.starter.cache.redisson.util.RedisUtils;
import top.wyhao.identity.client.UserContextHolder;
import top.wyhao.cmn.core.constant.CacheConstants;
import top.wyhao.identity.domain.auth.AuthException;
import top.wyhao.cmn.core.util.CollUtils;
import top.wyhao.cmn.core.util.RsaUtils;

import java.io.IOException;
import java.util.List;
import top.wyhao.identity.adapter.web.vo.ProfileAvatarResult;
import top.wyhao.identity.adapter.web.dto.ProfileEmailUpdateRequest;
import top.wyhao.identity.adapter.web.dto.ProfilePasswordUpdateRequest;
import top.wyhao.identity.adapter.web.dto.ProfilePhoneUpdateRequest;

/**
 * 个人信息 API
 *

 * @since 2023/1/2 11:41
 */
@Tag(name = "个人信息 API")
@Validated
@RestController
@RequiredArgsConstructor
public class UserProfileController {

    private static final String DECRYPT_FAILED = "当前密码解密失败";
    private final UserService userService;
    private final UserSocialService userSocialService;
    private final JustAuthProperties authProperties;

    @Operation(summary = "修改头像", description = "用户修改个人头像")
    @PatchMapping("/user/profile/avatar")
    public ProfileAvatarResult updateAvatar(@NotNull(message = "头像不能为空") MultipartFile avatarFile) throws IOException {
        if (avatarFile.isEmpty()) {
            throw UserException.avatarEmpty();
        }
        Long newAvatar = userService.updateAvatar(avatarFile, UserContextHolder.getUserId());
        return new ProfileAvatarResult(newAvatar);
    }

    @Operation(summary = "修改基础信息", description = "修改用户基础信息")
    @PatchMapping("/user/profile/basic/info")
    public void updateBasicInfo(@RequestBody @Valid UserBasicInfoUpdateReq req) {
        userService.updateBasicInfo(req, UserContextHolder.getUserId());
    }

    @Operation(summary = "修改密码", description = "修改用户登录密码")
    @PatchMapping("/user/profile/password")
    public void updatePassword(@RequestBody @Valid ProfilePasswordUpdateRequest updateReq) {
        String oldPassword = RsaUtils.decryptPasswordByRsaPrivateKey(updateReq.getOldPassword(), DECRYPT_FAILED);
        String newPassword = RsaUtils.decryptPasswordByRsaPrivateKey(updateReq.getNewPassword(), "新密码解密失败");
        userService.updatePassword(oldPassword, newPassword, UserContextHolder.getUserId());
    }

    @Operation(summary = "修改手机号", description = "修改手机号")
    @PatchMapping("/user/profile/phone")
    public void updatePhone(@RequestBody @Valid ProfilePhoneUpdateRequest updateReq) {
        String oldPassword = RsaUtils.decryptPasswordByRsaPrivateKey(updateReq.getOldPassword(), DECRYPT_FAILED);
        String captchaKey = CacheConstants.CAPTCHA_KEY_PREFIX + updateReq.getPhone();
        String captcha = RedisUtils.get(captchaKey);
        if (CharSequenceUtil.isBlank(captcha)) {
            throw AuthException.captchaOutdated();
        }
        if (!CharSequenceUtil.equalsIgnoreCase(updateReq.getCaptcha(), captcha)) {
            throw AuthException.captchaIncorrect();
        }
        RedisUtils.delete(captchaKey);
        userService.updatePhone(updateReq.getPhone(), oldPassword, UserContextHolder.getUserId());
    }

    @Operation(summary = "修改邮箱", description = "修改用户邮箱")
    @PatchMapping("/user/profile/email")
    public void updateEmail(@RequestBody @Valid ProfileEmailUpdateRequest request) {
        String oldPassword = RsaUtils.decryptPasswordByRsaPrivateKey(request.getOldPassword(), DECRYPT_FAILED);
        String captchaKey = CacheConstants.CAPTCHA_KEY_PREFIX + request.getEmail();
        String captcha = RedisUtils.getAndDelete(captchaKey);
        if (CharSequenceUtil.isBlank(captcha)) {
            throw AuthException.captchaOutdated();
        }
        if (!CharSequenceUtil.equalsIgnoreCase(request.getCaptcha(), captcha)) {
            throw AuthException.captchaIncorrect();
        }
        userService.updateEmail(request.getEmail(), oldPassword, UserContextHolder.getUserId());
    }

    @Operation(summary = "查询绑定的三方账号", description = "查询绑定的三方账号")
    @GetMapping("/user/profile/social")
    public List<UserSocialBindResp> listSocialBind() {
        List<SysUserSocial> userSocialList = userSocialService.listByUserId(UserContextHolder.getUserId());
        return CollUtils.mapToList(userSocialList, userSocial -> {
            String source = userSocial.getSource();
            UserSocialBindResp userSocialBind = new UserSocialBindResp();
            userSocialBind.setSource(source);
            userSocialBind.setDescription(SocialSource.valueOf(source).getDescription());
            return userSocialBind;
        });
    }

    @Operation(summary = "绑定三方账号", description = "绑定三方账号")
    @Parameter(name = "source", description = "来源", example = "gitee", in = ParameterIn.PATH)
    @PostMapping("/user/profile/social/{source}")
    public void bindSocial(@PathVariable String source, @RequestBody AuthCallback callback) {
        AuthRequest authRequest = this.getAuthRequest(source);
        AuthResponse<AuthUser> response = authRequest.login(callback);
        if (!response.ok()) {
            throw AuthException.socialAuthFailed(response.getMsg());
        }
        AuthUser authUser = response.getData();
        userSocialService.bind(authUser, UserContextHolder.getUserId());
    }

    @Operation(summary = "解绑三方账号", description = "解绑三方账号")
    @Parameter(name = "source", description = "来源", example = "gitee", in = ParameterIn.PATH)
    @DeleteMapping("/user/profile/social/{source}")
    public void unbindSocial(@PathVariable String source) {
        userSocialService.deleteBySourceAndUserId(source, UserContextHolder.getUserId());
    }

    private AuthRequest getAuthRequest(String source) {
        try {
            AuthConfig authConfig = authProperties.getType().get(source.toUpperCase());
            return AuthRequestBuilder.builder().source(source).authConfig(authConfig).build();
        } catch (Exception e) {
            throw AuthException.platformNotSupport(source);
        }
    }
}
