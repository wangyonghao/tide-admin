package top.wyhao.admin.system.security.app.service;

import jakarta.validation.Valid;
import top.wyhao.admin.system.security.app.dto.AuthenticationRequest;
import top.wyhao.admin.system.security.app.dto.RegisterRequest;
import top.wyhao.admin.system.security.app.dto.AuthenticationResult;

public interface AuthenticationService {
    AuthenticationResult authenticate(@Valid AuthenticationRequest authenticationRequest);

    /**
     * 自助注册并直接登录。
     */
    AuthenticationResult register(@Valid RegisterRequest request);

    void logout();
}
