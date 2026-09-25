
package top.wyhao.identity.app.auth.handler;

import cn.hutool.core.text.CharSequenceUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import top.wyhao.identity.domain.auth.AuthException;
import top.wyhao.identity.adapter.web.auth.dto.LoginRequest;
import top.wyhao.identity.adapter.web.auth.dto.PhoneLoginRequest;
import top.wyhao.identity.domain.auth.GrantType;
import top.wyhao.identity.adapter.web.auth.vo.LoginResult;
import top.wyhao.identity.app.assembler.UserAssembler;
import top.wyhao.identity.domain.model.SysUser;
import top.wyhao.identity.app.service.UserService;
import top.wyhao.starter.cache.redisson.util.RedisUtils;
import top.wyhao.identity.client.UserContextHolder;
import top.wyhao.cmn.core.constant.CacheConstants;
import top.wyhao.cmn.core.model.LoginUser;
import top.wyhao.starter.web.http.ServletUtils;

/**
 * 手机号登录处理器
 */
@RequiredArgsConstructor
@Component
public class PhoneLoginHandler implements LoginHandler {
    private final UserService userService;
    private final UserAssembler userAssembler;

    @Override
    public GrantType grantType() {
        return GrantType.PHONE;
    }

    @Override
    public LoginResult login(LoginRequest request) {
        PhoneLoginRequest req = (PhoneLoginRequest) request;
        this.preLogin(req);
        // 验证手机号
        SysUser user = userService.getByPhone(req.getPhone());
        if (user == null) {
            throw AuthException.phoneNotBound();
        }
        // 检查用户状态
        LoginHandlerHelper.checkUserStatus(user);
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
        HttpServletRequest httpServletRequest = ServletUtils.getRequest();
        String userAgent = httpServletRequest != null ? httpServletRequest.getHeader("User-Agent") : null;
        LoginHandlerHelper.loginSuccess(user.getUsername(), ip, userAgent);

        return new LoginResult("200", UserContextHolder.getToken(), null);
    }

    public void preLogin(PhoneLoginRequest req) {
        String phone = req.getPhone();
        String captchaKey = CacheConstants.CAPTCHA_KEY_PREFIX + phone;
        String captcha = RedisUtils.get(captchaKey);
        if (CharSequenceUtil.isBlank(captcha)) {
            throw AuthException.captchaOutdated();
        }
        if (!CharSequenceUtil.equalsIgnoreCase(req.getCaptcha(), captcha)) {
            throw AuthException.captchaOutdated();
        }
        RedisUtils.delete(captchaKey);
    }
}