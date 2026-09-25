package top.wyhao.identity.app.auth.handler;

import org.springframework.stereotype.Component;
import top.wyhao.identity.domain.auth.GrantType;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class LoginHandlerFactory {

    private final Map<String, LoginHandler> handlers;

    public LoginHandlerFactory(List<LoginHandler> list) {
        handlers = list.stream()
                .collect(Collectors.toMap(
                        handler -> handler.grantType().getValue(),
                        Function.identity()));
    }

    public LoginHandler getHandler(String grantType) {
        LoginHandler handler = handlers.get(grantType);
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
