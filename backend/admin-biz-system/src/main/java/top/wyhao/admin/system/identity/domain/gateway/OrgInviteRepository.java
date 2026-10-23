package top.wyhao.admin.system.identity.domain.gateway;

import top.wyhao.admin.system.identity.domain.model.SysOrgInvite;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 组织邀请仓储。
 */
public interface OrgInviteRepository {

    void insert(SysOrgInvite invite);

    void updateById(SysOrgInvite invite);

    SysOrgInvite findPendingByTenantAndEmail(Long tenantId, String email);

    SysOrgInvite findByTokenHash(String tokenHash);

    List<SysOrgInvite> listPendingByEmail(String email, LocalDateTime now);
}
