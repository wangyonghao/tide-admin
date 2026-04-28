
package top.wyhao.starter.web.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;

import io.undertow.Undertow;
import io.undertow.server.handlers.DisallowedMethodsHandler;
import io.undertow.util.HttpString;
import org.springframework.context.annotation.PropertySource;
import top.wyhao.starter.core.util.CollUtils;
import top.wyhao.starter.core.util.GeneralPropertySourceFactory;

import java.util.List;

/**
 * Undertow 自动配置
 *
 * @author Jasmine
 * @author Charles7c
 * @since 2.11.0
 */
@AutoConfiguration
@ConditionalOnWebApplication
@ConditionalOnClass(Undertow.class)
@PropertySource(value = "classpath:default-server.yml", factory = GeneralPropertySourceFactory.class)
public class UndertowAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(UndertowAutoConfiguration.class);

    /**
     * 默认禁止三个不安全的 HTTP 方法（如 CONNECT、TRACE、TRACK）
     */
    private static final List<String> DEFAULT_ALLOWED_METHODS = List.of("CONNECT", "TRACE", "TRACK");

    /**
     * Undertow 自定义配置
     */
    @Bean
    public WebServerFactoryCustomizer<UndertowServletWebServerFactory> customize() {
        return factory -> {
            factory.addDeploymentInfoCustomizers(deploymentInfo -> deploymentInfo
                .addInitialHandlerChainWrapper(handler -> new DisallowedMethodsHandler(handler, CollUtils
                    .mapToSet(DEFAULT_ALLOWED_METHODS, HttpString::tryFromString))));
            log.debug("[wyhao-starter] - Disallowed HTTP methods on Server Undertow: {}.", DEFAULT_ALLOWED_METHODS);
        };
    }
}
