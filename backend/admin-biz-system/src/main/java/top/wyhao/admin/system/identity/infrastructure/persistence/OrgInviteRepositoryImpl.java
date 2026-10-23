package top.wyhao.admin.system.identity.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import top.wyhao.admin.system.identity.domain.gateway.OrgInviteRepository;
import top.wyhao.admin.system.identity.domain.model.OrgInviteStatus;
import top.wyhao.admin.system.identity.domain.model.SysOrgInvite;
import top.wyhao.admin.system.identity.infrastructure.persistence.mapper.SysOrgInviteMapper;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 组织邀请仓储实现。
 */
@Repository
@RequiredArgsConstructor
public class OrgInviteRepositoryImpl implements OrgInviteRepository {

    private final SysOrgInviteMapper inviteMapper;

    @Override
    public void insert(SysOrgInvite invite) {
        inviteMapper.insert(invite);
    }

    @Override
    public void updateById(SysOrgInvite invite) {
        inviteMapper.updateById(invite);
    }

    @Override
    public SysOrgInvite findPendingByTenantAndEmail(Long tenantId, String email) {
        return inviteMapper.lambdaQuery()
                .eq(SysOrgInvite::getTenantId, tenantId)
                .eq(SysOrgInvite::getEmail, email)
                .eq(SysOrgInvite::getStatus, OrgInviteStatus.PENDING.getValue())
                .eq(SysOrgInvite::getDeleted, 0)
                .one();
    }

    @Override
    public SysOrgInvite findByTokenHash(String tokenHash) {
        return inviteMapper.lambdaQuery()
                .eq(SysOrgInvite::getTokenHash, tokenHash)
                .eq(SysOrgInvite::getDeleted, 0)
                .one();
    }

    @Override
    public List<SysOrgInvite> listPendingByEmail(String email, LocalDateTime now) {
        return inviteMapper.lambdaQuery()
                .eq(SysOrgInvite::getEmail, email)
                .eq(SysOrgInvite::getStatus, OrgInviteStatus.PENDING.getValue())
                .eq(SysOrgInvite::getDeleted, 0)
                .gt(SysOrgInvite::getExpireTime, now)
                .list();
    }
}
