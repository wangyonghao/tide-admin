package top.wyhao.admin.auth.service;

import jakarta.validation.Valid;
import top.wyhao.admin.auth.model.LoginRequest;
import top.wyhao.admin.auth.model.LoginResult;

public interface AuthService {
    LoginResult login(@Valid LoginRequest loginRequest);

    void logout();
}
