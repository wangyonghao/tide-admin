package top.wyhao.identity.app.auth.service.impl;

import cn.dev33.satoken.exception.NotLoginException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import top.wyhao.identity.app.auth.handler.LoginHandler;
import top.wyhao.identity.app.auth.handler.LoginHandlerFactory;
import top.wyhao.identity.adapter.web.auth.dto.LoginRequest;
import top.wyhao.identity.adapter.web.auth.vo.LoginResult;
import top.wyhao.identity.app.auth.service.AuthService;
import top.wyhao.common.security.util.LoginUtil;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final LoginHandlerFactory loginHandlerFactory;
    @Override
    public LoginResult login(@Valid LoginRequest loginRequest) {
        // 根据 grantType 获取对应处理器
        LoginHandler loginHandler =  loginHandlerFactory.getHandler(loginRequest.getGrantType());
        // 登录
        return loginHandler.login(loginRequest);
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
