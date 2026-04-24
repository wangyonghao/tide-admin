package top.wyhao.starter.tenant.core;

/**
 * 租户实体类接口
 * 实现该接口的实体类将自动加入租户过滤条件
 * @author wyhao
 */
public interface TenantedEntity {
    /**
     * 租户Id
     */
    String getTenantId();
}
