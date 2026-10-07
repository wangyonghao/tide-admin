package top.wyhao.identity.domain.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import top.wyhao.identity.domain.exception.UserException;
import top.wyhao.identity.domain.model.PasswordHistory;
import top.wyhao.identity.domain.model.PasswordPolicy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * {@link PasswordRules} 示范单测：密码策略合规校验。
 * <p>
 * 断言用 {@code getCode()}/{@code getDefaultMessage()}，避免 {@code getMessage()} 触发 I18n（需 Spring）。
 */
@ExtendWith(MockitoExtension.class)
class PasswordRulesTest {

    private static final String USERNAME = "alice";

    @Mock
    private PasswordEncoder passwordEncoder;

    private PasswordRules passwordRules;

    @BeforeEach
    void setUp() {
        passwordRules = new PasswordRules(passwordEncoder);
    }

    @Test
    void shouldPassWhenCompliant() {
        PasswordPolicy policy = policy(8, false, true, 3);
        PasswordHistory history = PasswordHistory.parse("old-hash");
        when(passwordEncoder.matches("abc12345", "old-hash")).thenReturn(false);

        assertThatCode(() -> passwordRules.assertCompliant("abc12345", policy, USERNAME, history))
                .doesNotThrowAnyException();
        verify(passwordEncoder).matches("abc12345", "old-hash");
    }

    @Test
    void shouldRejectWhenTooShort() {
        PasswordPolicy policy = policy(10, false, true, 3);

        assertThatThrownBy(() -> passwordRules.assertCompliant("abc12", policy, USERNAME, PasswordHistory.empty()))
                .isInstanceOf(UserException.class)
                .satisfies(ex -> {
                    UserException ue = (UserException) ex;
                    assertThat(ue.getCode()).isEqualTo("USER_PASSWORD_POLICY_VIOLATED");
                    assertThat(ue.getDefaultMessage()).contains("密码最小长度为 10");
                });
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void shouldRejectWhenMissingDigitOrLowercase() {
        PasswordPolicy policy = policy(8, false, true, 3);

        assertThatThrownBy(() -> passwordRules.assertCompliant("ABCDEFGH", policy, USERNAME, PasswordHistory.empty()))
                .isInstanceOf(UserException.class)
                .satisfies(ex -> assertThat(((UserException) ex).getCode()).isEqualTo("USER_PASSWORD_FORMAT_INVALID"));
    }

    @Test
    void shouldRejectWhenSymbolsRequiredButMissing() {
        PasswordPolicy policy = policy(8, true, true, 3);

        assertThatThrownBy(() -> passwordRules.assertCompliant("abc12345", policy, USERNAME, PasswordHistory.empty()))
                .isInstanceOf(UserException.class)
                .satisfies(ex -> assertThat(((UserException) ex).getDefaultMessage()).contains("特殊字符"));
    }

    @Test
    void shouldPassWhenSymbolsRequiredAndPresent() {
        PasswordPolicy policy = policy(8, true, true, 3);

        assertThatCode(() -> passwordRules.assertCompliant("abc1234!", policy, USERNAME, PasswordHistory.empty()))
                .doesNotThrowAnyException();
    }

    @Test
    void shouldRejectWhenContainsUsername() {
        PasswordPolicy policy = policy(8, false, false, 3);

        assertThatThrownBy(() -> passwordRules.assertCompliant("alice123", policy, USERNAME, PasswordHistory.empty()))
                .isInstanceOf(UserException.class)
                .satisfies(ex -> assertThat(((UserException) ex).getDefaultMessage()).contains("用户名"));
    }

    @Test
    void shouldRejectWhenContainsReversedUsername() {
        PasswordPolicy policy = policy(8, false, false, 3);

        assertThatThrownBy(() -> passwordRules.assertCompliant("ecila99x", policy, USERNAME, PasswordHistory.empty()))
                .isInstanceOf(UserException.class)
                .satisfies(ex -> assertThat(((UserException) ex).getDefaultMessage()).contains("用户名"));
    }

    @Test
    void shouldRejectWhenReusedInHistory() {
        PasswordPolicy policy = policy(8, false, true, 5);
        PasswordHistory history = PasswordHistory.parse("h1|h2|h3|h4|h5");
        when(passwordEncoder.matches(eq("abc12345"), anyString())).thenReturn(false);
        when(passwordEncoder.matches("abc12345", "h3")).thenReturn(true);

        assertThatThrownBy(() -> passwordRules.assertCompliant("abc12345", policy, USERNAME, history))
                .isInstanceOf(UserException.class)
                .satisfies(ex -> assertThat(((UserException) ex).getDefaultMessage()).contains("历史前 5 次"));
    }

    private static PasswordPolicy policy(int minLength,
                                         boolean requireSymbols,
                                         boolean allowContainUsername,
                                         int historyTimes) {
        return new PasswordPolicy(
                minLength,
                PasswordPolicy.DEFAULT_MAX_LENGTH,
                requireSymbols,
                allowContainUsername,
                historyTimes,
                0
        );
    }
}
