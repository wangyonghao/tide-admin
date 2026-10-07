package top.wyhao.identity.infrastructure.persistence;

import cn.hutool.core.collection.CollUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import top.wyhao.identity.domain.gateway.MembershipRepository;
import top.wyhao.identity.domain.model.MembershipScopeType;
import top.wyhao.identity.domain.model.SysMembership;
import top.wyhao.identity.infrastructure.persistence.mapper.SysMembershipMapper;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * 成员关系仓储实现。
 */
@Repository
@RequiredArgsConstructor
public class MembershipRepositoryImpl implements MembershipRepository {

    private final SysMembershipMapper membershipMapper;

    @Override
    public void insert(SysMembership membership) {
        membershipMapper.insert(membership);
    }

    @Override
    public void insertBatch(List<SysMembership> memberships) {
        if (CollUtil.isEmpty(memberships)) {
            return;
        }
        membershipMapper.insertBatch(memberships);
    }

    @Override
    public void softDeleteByUserIdAndScopeType(Long userId, MembershipScopeType scopeType) {
        membershipMapper.lambdaUpdate()
                .eq(SysMembership::getUserId, userId)
                .eq(SysMembership::getScopeType, scopeType)
                .eq(SysMembership::getDeleted, 0)
                .set(SysMembership::getDeleted, 1)
                .set(SysMembership::getUpdateTime, LocalDateTime.now())
                .update();
    }

    @Override
    public void softDeleteByUserIds(Collection<Long> userIds) {
        if (CollUtil.isEmpty(userIds)) {
            return;
        }
        membershipMapper.lambdaUpdate()
                .in(SysMembership::getUserId, userIds)
                .eq(SysMembership::getDeleted, 0)
                .set(SysMembership::getDeleted, 1)
                .set(SysMembership::getUpdateTime, LocalDateTime.now())
                .update();
    }

    @Override
    public void softDeleteByUserIdsAndScopeType(Collection<Long> userIds, MembershipScopeType scopeType) {
        if (CollUtil.isEmpty(userIds)) {
            return;
        }
        membershipMapper.lambdaUpdate()
                .in(SysMembership::getUserId, userIds)
                .eq(SysMembership::getScopeType, scopeType)
                .eq(SysMembership::getDeleted, 0)
                .set(SysMembership::getDeleted, 1)
                .set(SysMembership::getUpdateTime, LocalDateTime.now())
                .update();
    }

    @Override
    public void softDeleteByScopeAndUserIds(MembershipScopeType scopeType, Long scopeId, Collection<Long> userIds) {
        if (CollUtil.isEmpty(userIds)) {
            return;
        }
        membershipMapper.lambdaUpdate()
                .eq(SysMembership::getScopeType, scopeType)
                .eq(SysMembership::getScopeId, scopeId)
                .in(SysMembership::getUserId, userIds)
                .eq(SysMembership::getDeleted, 0)
                .set(SysMembership::getDeleted, 1)
                .set(SysMembership::getUpdateTime, LocalDateTime.now())
                .update();
    }

    @Override
    public List<Long> listActiveUserIdsByScope(MembershipScopeType scopeType, Long scopeId) {
        return membershipMapper.lambdaQuery()
                .select(SysMembership::getUserId)
                .eq(SysMembership::getScopeType, scopeType)
                .eq(SysMembership::getScopeId, scopeId)
                .eq(SysMembership::getDeleted, 0)
                .list()
                .stream()
                .map(SysMembership::getUserId)
                .toList();
    }

    @Override
    public List<Long> listActiveScopeIdsByUser(Long userId, MembershipScopeType scopeType) {
        if (userId == null) {
            return Collections.emptyList();
        }
        return membershipMapper.lambdaQuery()
                .select(SysMembership::getScopeId)
                .eq(SysMembership::getUserId, userId)
                .eq(SysMembership::getScopeType, scopeType)
                .eq(SysMembership::getDeleted, 0)
                .list()
                .stream()
                .map(SysMembership::getScopeId)
                .toList();
    }
}
