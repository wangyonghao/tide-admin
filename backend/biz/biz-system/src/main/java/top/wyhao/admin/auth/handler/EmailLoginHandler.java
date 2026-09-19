
package top.wyhao.admin.auth.handler;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import top.wyhao.admin.auth.model.EmailLoginRequest;
import top.wyhao.admin.auth.model.LoginRequest;
import top.wyhao.admin.auth.model.LoginResult;
import top.wyhao.admin.auth.model.enums.GrantType;
import top.wyhao.admin.system.assembler.UserAssembler;
import top.wyhao.admin.system.entity.SysUser;
import top.wyhao.admin.system.service.UserService;
import top.wyhao.starter.cache.redisson.util.RedisUtils;
import top.wyhao.starter.core.UserContextHolder;
import top.wyhao.starter.core.constant.CacheConstants;
import top.wyhao.starter.core.util.validation.ValidationUtils;
import top.wyhao.starter.web.http.ServletUtils;

/**
 * 邮箱登录处理器
 */

@RequiredArgsConstructor
@Component
public class EmailLoginHandler implements LoginHandler {
    private final UserService userService;
    private final UserAssembler userAssembler;

    @Override
    public GrantType grantType() {
        return GrantType.EMAIL;
    }

    public LoginResult login(LoginRequest request) {
        EmailLoginRequest req = (EmailLoginRequest)request;
        String email = req.email();
        String captchaKey = CacheConstants.CAPTCHA_KEY_PREFIX + email;
        String captcha = RedisUtils.get(captchaKey);
        ValidationUtils.throwIfBlank(captcha, "验证码已失效");
        ValidationUtils.throwIfNotEqualIgnoreCase(req.captcha(), captcha, "验证码不正确");
        RedisUtils.delete(captchaKey);
        // 验证邮箱
        SysUser user = userService.getByEmail(req.email());
        ValidationUtils.throwIfNull(user, "此邮箱未绑定本系统账号");
        // 检查用户状态
        LoginHandlerHelper.checkUserStatus(user);

        // 7. 登录（创建会话、签发Token）
        LoginHandlerHelper.doLogin(user.getId());

        // 8. 保存用户信息到会话
        LoginHandlerHelper.setSession(userAssembler.toLoginUser(user), "PC");

        // 9. 记录登录成功日志
        String ip = ServletUtils.getRequestIp();
        HttpServletRequest httpServletRequest = ServletUtils.getRequest();
        String userAgent = httpServletRequest != null ? httpServletRequest.getHeader("User-Agent") : null;
        LoginHandlerHelper.loginSuccess(user.getUsername(), ip, userAgent);

        return new LoginResult("200", UserContextHolder.getToken(), null);
    }
}