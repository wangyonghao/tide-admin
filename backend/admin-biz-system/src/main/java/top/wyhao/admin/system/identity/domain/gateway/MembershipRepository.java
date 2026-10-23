package top.wyhao.admin.system.identity.domain.gateway;

import top.wyhao.admin.system.identity.domain.model.MembershipScopeType;
import top.wyhao.admin.system.identity.domain.model.SysMembership;

import java.util.Collection;
import java.util.List;

/**
 * 成员关系仓储。
 */
public interface MembershipRepository {

    void insert(SysMembership membership);

    void insertBatch(List<SysMembership> memberships);

    void softDeleteByUserIdAndScopeType(Long userId, MembershipScopeType scopeType);

    void softDeleteByUserIds(Collection<Long> userIds);

    void softDeleteByUserIdsAndScopeType(Collection<Long> userIds, MembershipScopeType scopeType);

    void softDeleteByScopeAndUserIds(MembershipScopeType scopeType, Long scopeId, Collection<Long> userIds);

    List<Long> listActiveUserIdsByScope(MembershipScopeType scopeType, Long scopeId);

    List<Long> listActiveScopeIdsByUser(Long userId, MembershipScopeType scopeType);

    List<SysMembership> listByUserAndScopeType(Long userId, MembershipScopeType scopeType);

    SysMembership findActive(Long userId, MembershipScopeType scopeType, Long scopeId);

    SysMembership findPrimary(Long userId, MembershipScopeType scopeType);

    void clearPrimary(Long userId, MembershipScopeType scopeType);

    void update(SysMembership membership);
}
