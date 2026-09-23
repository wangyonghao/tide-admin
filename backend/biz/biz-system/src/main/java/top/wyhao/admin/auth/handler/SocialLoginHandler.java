
package top.wyhao.admin.auth.handler;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.json.JSONUtil;
import com.xkcoding.justauth.autoconfigure.JustAuthProperties;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import me.zhyd.oauth.AuthRequestBuilder;
import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.model.AuthUser;
import me.zhyd.oauth.request.AuthRequest;
import org.springframework.stereotype.Component;
import top.wyhao.admin.auth.model.dto.LoginRequest;
import top.wyhao.admin.auth.model.dto.SocialLoginRequest;
import top.wyhao.admin.auth.model.enums.GrantType;
import top.wyhao.admin.auth.model.vo.LoginResult;
import top.wyhao.admin.system.assembler.UserAssembler;
import top.wyhao.admin.system.entity.SysUser;
import top.wyhao.admin.system.entity.SysUserSocial;
import top.wyhao.admin.system.model.SystemConstants;
import top.wyhao.admin.system.model.enums.MessageTemplates;
import top.wyhao.admin.system.model.enums.MessageType;
import top.wyhao.admin.system.service.*;
import top.wyhao.starter.core.UserContextHolder;
import top.wyhao.starter.core.autoconfigure.application.ApplicationProperties;
import top.wyhao.starter.core.constant.RegexConstants;
import top.wyhao.starter.core.enums.GenderEnum;
import top.wyhao.starter.core.enums.RoleCodeEnum;
import top.wyhao.starter.core.enums.StatusEnum;
import top.wyhao.admin.auth.exception.AuthException;
import top.wyhao.starter.core.model.LoginUser;
import top.wyhao.starter.web.http.ServletUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import top.wyhao.admin.system.model.dto.MessageRequest;

/**
 * 第三方账号登录处理器
 */
@Component
@RequiredArgsConstructor
public class SocialLoginHandler implements LoginHandler {

    private final JustAuthProperties authProperties;
    private final ApplicationProperties applicationProperties;

    private final UserService userService;
    private final UserSocialService userSocialService;
    private final RoleService roleService;
    private final MessageService messageService;
    private final UserAssembler userAssembler;

    @Override
    public GrantType grantType() {
        return GrantType.SOCIAL;
    }

    @Override
    public LoginResult login(LoginRequest request) {
        SocialLoginRequest req = (SocialLoginRequest) request;
        if (StpUtil.isLogin()) {
            StpUtil.logout();
        }
        // 获取第三方登录信息
        AuthRequest authRequest = this.getAuthRequest(req.getSource());
        AuthCallback callback = new AuthCallback();
        callback.setCode(req.getCode());
        callback.setState(req.getState());
        AuthResponse<AuthUser> response = authRequest.login(callback);
        if (!response.ok()) {
            throw AuthException.socialAuthFailed(response.getMsg());
        }
        AuthUser authUser = response.getData();
        // 如未绑定则自动注册新用户，保存或更新关联信息
        String source = authUser.getSource();
        String openId = authUser.getUuid();
        SysUserSocial userSocial = userSocialService.getBySourceAndOpenId(source, openId);
        SysUser user;
        if (userSocial == null) {
            String username = authUser.getUsername();
            String nickname = authUser.getNickname();
            SysUser existsUser = userService.getByUsername(username);
            String randomStr = RandomUtil.randomString(RandomUtil.BASE_CHAR, 5);
            if (existsUser != null || !ReUtil.isMatch(RegexConstants.USERNAME, username)) {
                username = randomStr + IdUtil.fastSimpleUUID();
            }
            if (!ReUtil.isMatch(RegexConstants.GENERAL_NAME, nickname)) {
                nickname = source.toLowerCase() + randomStr;
            }
            user = new SysUser();
            user.setUsername(username);
            user.setNickname(nickname);
            if (authUser.getGender() != null) {
                user.setGender(GenderEnum.getByValue(Integer.parseInt(authUser.getGender().getCode())).getValue());
            }
            user.setDeptId(SystemConstants.SUPER_DEPT_ID);
            user.setStatus(StatusEnum.ENABLE.getValue());
            userService.save(user);
            Long userId = user.getId();
            roleService.assignRolesToUser(Collections.singletonList(roleService
                    .getIdByCode(RoleCodeEnum.GENERAL_USER.getCode())), userId);
            userSocial = new SysUserSocial();
            userSocial.setUserId(userId);
            userSocial.setSource(source);
            userSocial.setOpenId(openId);
            this.sendSecurityMsg(user);
        } else {
            user = userAssembler.toEntity(userService.detail(userSocial.getUserId()));
        }
        // 检查用户状态
        LoginHandlerHelper.checkUserStatus(user);
        userSocial.setMetaJson(JSONUtil.toJsonStr(authUser));
        userSocial.setLastLoginTime(LocalDateTime.now());
        userSocialService.saveOrUpdate(userSocial);
        // 执行认证
        // 获取权限、角色、密码过期天数
        LoginUser loginUser = userAssembler.toLoginUser(user);
        loginUser.setDeviceType("PC");

        // 7. 登录（创建会话、签发Token）
        LoginHandlerHelper.doLogin(user.getId());

        // 8. 保存用户信息到会话
        LoginHandlerHelper.setSession(loginUser, "PC");

        // 9. 记录登录成功日志
        String ip = ServletUtils.getRequestIp();
        HttpServletRequest httpRequest = ServletUtils.getRequest();
        String userAgent = httpRequest != null ? httpRequest.getHeader("User-Agent") : null;
        LoginHandlerHelper.loginSuccess(user.getUsername(), ip, userAgent);

        return new LoginResult("200", UserContextHolder.getToken(), null);
    }

    /**
     * 获取 AuthRequest
     *
     * @param source 平台名称
     * @return AuthRequest
     */
    private AuthRequest getAuthRequest(String source) {
        try {
            AuthConfig authConfig = authProperties.getType().get(source.toUpperCase());
            return AuthRequestBuilder.builder().source(source).authConfig(authConfig).build();
        } catch (Exception e) {
            throw AuthException.platformNotSupport(source);
        }
    }

    /**
     * 发送安全消息
     *
     * @param user 用户信息
     */
    private void sendSecurityMsg(SysUser user) {
        MessageTemplates template = MessageTemplates.SOCIAL_REGISTER;
        MessageRequest req = new MessageRequest(
                template.getTitle().formatted(applicationProperties.getName()),
                template.getContent().formatted(user.getNickname()),
                MessageType.SECURITY,
                null
        );
        messageService.add(req, CollUtil.toList(user.getId().toString()));
    }
}
