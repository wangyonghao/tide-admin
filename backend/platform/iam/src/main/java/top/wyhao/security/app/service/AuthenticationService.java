package top.wyhao.security.app.service;

import jakarta.validation.Valid;
import top.wyhao.security.adapter.web.dto.AuthenticationRequest;
import top.wyhao.security.adapter.web.dto.RegisterRequest;
import top.wyhao.security.adapter.web.vo.AuthenticationResult;

public interface AuthenticationService {
    AuthenticationResult authenticate(@Valid AuthenticationRequest authenticationRequest);

    /**
     * 自助注册并直接登录。
     */
    AuthenticationResult register(@Valid RegisterRequest request);

    void logout();
}
