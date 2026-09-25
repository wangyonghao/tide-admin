package top.wyhao.identity.auth.service;

import jakarta.validation.Valid;
import top.wyhao.identity.auth.model.dto.LoginRequest;
import top.wyhao.identity.auth.model.vo.LoginResult;

public interface AuthService {
    LoginResult login(@Valid LoginRequest loginRequest);

    void logout();
}
