package top.wyhao.identity.client;

import lombok.Data;
import lombok.NoArgsConstructor;
import top.wyhao.cmn.core.model.RoleDataScope;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 登录用户（跨模块契约）
 */
@Data
@NoArgsConstructor
public class LoginUser {

    private Long userId;

    private String username;

    private Long tenantId;

    private Long departmentId;

    private String departmentName;

    private LocalDateTime pwdResetTime;

    private Integer passwordExpirationDays;

    private String deviceType;

    private List<RoleDataScope> roleDataScopes;
}
