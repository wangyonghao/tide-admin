package top.wyhao.security.app.handler;

import org.springframework.stereotype.Component;
import top.wyhao.security.domain.model.GrantType;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class AuthenticationHandlerFactory {

    private final Map<String, AuthenticationHandler> handlers;

    public AuthenticationHandlerFactory(List<AuthenticationHandler> list) {
        handlers = list.stream()
                .collect(Collectors.toMap(
                        handler -> handler.grantType().getValue(),
                        Function.identity()));
    }

    public AuthenticationHandler getHandler(String grantType) {
        AuthenticationHandler handler = handlers.get(grantType);
        if (handler == null) {
            // 兼容历史值 PASSWORD → ACCOUNT
            if ("PASSWORD".equals(grantType)) {
                handler = handlers.get(GrantType.ACCOUNT.getValue());
            }
        }
        if (handler == null) {
            throw new IllegalArgumentException("Unsupported grantType: " + grantType);
        }
        return handler;
    }
}
