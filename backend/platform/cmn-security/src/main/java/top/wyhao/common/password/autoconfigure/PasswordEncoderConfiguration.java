package top.wyhao.common.password.autoconfigure;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 密码哈希算法（基础设施）。业务域只注入 {@link PasswordEncoder}，不在 biz 模块声明 Bean。
 */
@AutoConfiguration
public class PasswordEncoderConfiguration {

    @Bean
    public PasswordEncoder passwordEncoder() {
        /*
         * 可用算法：
         *   BCRYPT - new BCryptPasswordEncoder()
         *   SCRYPT - SCryptPasswordEncoder.defaultsForSpringSecurity_v5_8()
         *   PBKDF2 - Pbkdf2PasswordEncoder.defaultsForSpringSecurity_v5_8()
         *   ARGON2 - Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8()
         */
        return new BCryptPasswordEncoder();
    }
}
