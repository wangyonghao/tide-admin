package top.wyhao.security.app.service;

import jakarta.validation.Valid;
import top.wyhao.security.adapter.web.dto.AuthenticationRequest;
import top.wyhao.security.adapter.web.vo.AuthenticationResult;

public interface AuthenticationService {
    AuthenticationResult authenticate(@Valid AuthenticationRequest authenticationRequest);

    void logout();
}
