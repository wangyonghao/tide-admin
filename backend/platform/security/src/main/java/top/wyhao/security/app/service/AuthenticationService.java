package top.wyhao.identity.app.auth.service;

import jakarta.validation.Valid;
import top.wyhao.identity.adapter.web.auth.dto.LoginRequest;
import top.wyhao.identity.adapter.web.auth.vo.LoginResult;

public interface AuthService {
    LoginResult login(@Valid LoginRequest loginRequest);

    void logout();
}
