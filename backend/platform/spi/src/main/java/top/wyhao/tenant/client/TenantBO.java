package top.wyhao.tenant.client;

import lombok.Data;

/**
 * 租户信息（跨模块契约）
 */
@Data
public class TenantBO {

    private Long id;

    private String name;

    private String adminUsername;

    private String adminPassword;

    private Long packageId;
}
