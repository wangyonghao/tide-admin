package top.wyhao.starter.tenant.core;

/**
 * 租户实体类接口
 * @author Yonghao Wang
 */
public interface TenantedEntity {
    /**
     * 租户Id
     */
    Long getTenantId();
}
