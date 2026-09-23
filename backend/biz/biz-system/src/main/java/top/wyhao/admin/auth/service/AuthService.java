package top.wyhao.admin.auth.service;

import jakarta.validation.Valid;
import top.wyhao.admin.auth.model.dto.LoginRequest;
import top.wyhao.admin.auth.model.vo.LoginResult;

public interface AuthService {
    LoginResult login(@Valid LoginRequest loginRequest);

    void logout();
}
