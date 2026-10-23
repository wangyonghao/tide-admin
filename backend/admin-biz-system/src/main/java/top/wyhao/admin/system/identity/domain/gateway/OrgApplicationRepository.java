package top.wyhao.admin.system.identity.domain.gateway;

import top.wyhao.admin.system.identity.domain.model.SysOrgApplication;

/**
 * 组织申请仓储。
 */
public interface OrgApplicationRepository {

    void insert(SysOrgApplication application);

    void updateById(SysOrgApplication application);

    SysOrgApplication findById(Long id);

    boolean existsPending(Long tenantId, Long userId);
}
