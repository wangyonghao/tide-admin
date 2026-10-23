package top.wyhao.admin.system.identity.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import top.wyhao.admin.system.identity.domain.gateway.OrgApplicationRepository;
import top.wyhao.admin.system.identity.domain.model.OrgApplicationStatus;
import top.wyhao.admin.system.identity.domain.model.SysOrgApplication;
import top.wyhao.admin.system.identity.infrastructure.persistence.mapper.SysOrgApplicationMapper;

/**
 * 组织申请仓储实现。
 */
@Repository
@RequiredArgsConstructor
public class OrgApplicationRepositoryImpl implements OrgApplicationRepository {

    private final SysOrgApplicationMapper applicationMapper;

    @Override
    public void insert(SysOrgApplication application) {
        applicationMapper.insert(application);
    }

    @Override
    public void updateById(SysOrgApplication application) {
        applicationMapper.updateById(application);
    }

    @Override
    public SysOrgApplication findById(Long id) {
        return applicationMapper.selectById(id);
    }

    @Override
    public boolean existsPending(Long tenantId, Long userId) {
        return applicationMapper.lambdaQuery()
                .eq(SysOrgApplication::getTenantId, tenantId)
                .eq(SysOrgApplication::getUserId, userId)
                .eq(SysOrgApplication::getStatus, OrgApplicationStatus.PENDING.getValue())
                .eq(SysOrgApplication::getDeleted, 0)
                .exists();
    }
}
