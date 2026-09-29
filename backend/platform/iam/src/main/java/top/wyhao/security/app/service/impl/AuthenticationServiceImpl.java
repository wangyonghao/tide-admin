package top.wyhao.security.app.service.impl;

import cn.dev33.satoken.exception.NotLoginException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import top.wyhao.security.app.handler.Authenticator;
import top.wyhao.security.app.handler.AuthenticatorFactory;
import top.wyhao.security.adapter.web.dto.AuthenticationRequest;
import top.wyhao.security.adapter.web.vo.AuthenticationResult;
import top.wyhao.security.app.service.AuthenticationService;
import top.wyhao.common.satoken.util.LoginUtil;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final AuthenticatorFactory authenticatorFactory;
    @Override
    public AuthenticationResult authenticate(@Valid AuthenticationRequest authenticationRequest) {
        // 根据 grantType 获取对应处理器
        Authenticator authenticator =  authenticatorFactory.getHandler(authenticationRequest.getGrantType());
        // 登录
        return authenticator.authenticate(authenticationRequest);
    }

    @Override
    public void logout() {
        try {
            LoginUtil.logout();
        } catch (NotLoginException ignored) {
        }
        // todo 退出登录日志
    }

}
