package top.wyhao.identity.client;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 登录与密码校验需要的用户凭证，不包含档案管理字段。
 */
public record CredentialUser(
        Long id,
        String username,
        String displayName,
        String password,
        Integer status,
        Integer gender,
        Long departmentId,
        LocalDateTime pwdUpdateTime,
        LocalDate pwdExpireDate
) {
}
