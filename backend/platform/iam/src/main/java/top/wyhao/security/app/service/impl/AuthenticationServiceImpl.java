package top.wyhao.security.app.service.impl;

import cn.dev33.satoken.exception.NotLoginException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import top.wyhao.security.app.handler.AuthenticationHandler;
import top.wyhao.security.app.handler.AuthenticationHandlerFactory;
import top.wyhao.security.adapter.web.dto.AuthenticationRequest;
import top.wyhao.security.adapter.web.vo.AuthenticationResult;
import top.wyhao.security.app.service.AuthenticationService;
import top.wyhao.common.security.util.LoginUtil;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final AuthenticationHandlerFactory authenticationHandlerFactory;
    @Override
    public AuthenticationResult authenticate(@Valid AuthenticationRequest authenticationRequest) {
        // 根据 grantType 获取对应处理器
        AuthenticationHandler authenticationHandler =  authenticationHandlerFactory.getHandler(authenticationRequest.getGrantType());
        // 登录
        return authenticationHandler.authenticate(authenticationRequest);
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
