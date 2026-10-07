package top.wyhao.identity.app.service.impl;

import cn.hutool.core.collection.CollUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.wyhao.cmn.core.util.CollUtils;
import top.wyhao.identity.app.service.MembershipService;
import top.wyhao.identity.domain.gateway.MembershipRepository;
import top.wyhao.identity.domain.model.MembershipScopeType;
import top.wyhao.identity.domain.model.SysMembership;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 成员关系服务实现。
 */
@Service
@RequiredArgsConstructor
public class MembershipServiceImpl implements MembershipService {

    private final MembershipRepository membershipRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void replacePrimaryDepartment(Long userId, Long departmentId) {
        if (userId == null) {
            return;
        }
        membershipRepository.softDeleteByUserIdAndScopeType(userId, MembershipScopeType.DEPARTMENT);
        if (departmentId == null) {
            return;
        }
        membershipRepository.insert(SysMembership.primaryDepartment(userId, departmentId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void replacePrimaryDepartments(List<long[]> userDepartmentPairs) {
        if (CollUtil.isEmpty(userDepartmentPairs)) {
            return;
        }
        List<Long> userIds = new ArrayList<>(userDepartmentPairs.size());
        List<SysMembership> toInsert = new ArrayList<>(userDepartmentPairs.size());
        for (long[] pair : userDepartmentPairs) {
            if (pair == null || pair.length < 2) {
                continue;
            }
            long userId = pair[0];
            long departmentId = pair[1];
            userIds.add(userId);
            toInsert.add(SysMembership.primaryDepartment(userId, departmentId));
        }
        membershipRepository.softDeleteByUserIdsAndScopeType(userIds, MembershipScopeType.DEPARTMENT);
        membershipRepository.insertBatch(toInsert);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void replaceRoles(Long userId, List<Long> roleIds) {
        if (userId == null) {
            return;
        }
        membershipRepository.softDeleteByUserIdAndScopeType(userId, MembershipScopeType.ROLE);
        List<Long> ids = CollUtil.emptyIfNull(roleIds).stream().distinct().toList();
        if (ids.isEmpty()) {
            return;
        }
        membershipRepository.insertBatch(CollUtils.mapToList(ids, roleId -> SysMembership.role(userId, roleId)));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addRoleMembers(Long roleId, Collection<Long> userIds) {
        if (roleId == null || CollUtil.isEmpty(userIds)) {
            return;
        }
        Set<Long> existing = new HashSet<>(membershipRepository.listActiveUserIdsByScope(MembershipScopeType.ROLE, roleId));
        List<SysMembership> toInsert = userIds.stream()
                .filter(userId -> userId != null && !existing.contains(userId))
                .distinct()
                .map(userId -> SysMembership.role(userId, roleId))
                .toList();
        membershipRepository.insertBatch(toInsert);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeRoleMembers(Long roleId, Collection<Long> userIds) {
        if (roleId == null || CollUtil.isEmpty(userIds)) {
            return;
        }
        membershipRepository.softDeleteByScopeAndUserIds(MembershipScopeType.ROLE, roleId, userIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeAllRolesByUserIds(Collection<Long> userIds) {
        membershipRepository.softDeleteByUserIdsAndScopeType(userIds, MembershipScopeType.ROLE);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeAllByUserIds(Collection<Long> userIds) {
        membershipRepository.softDeleteByUserIds(userIds);
    }
}
