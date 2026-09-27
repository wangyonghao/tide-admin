package top.wyhao.security.infrastructure.persistence;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import top.wyhao.security.domain.gateway.UserRoleRepository;
import top.wyhao.security.domain.model.SysUserRole;
import top.wyhao.security.infrastructure.persistence.mapper.SysUserRoleMapper;

import java.util.Collection;
import java.util.List;

/**
 * 用户与角色关联仓储实现。
 */
@Repository
@RequiredArgsConstructor
public class UserRoleRepositoryImpl implements UserRoleRepository {

    private final SysUserRoleMapper userRoleMapper;

    @Override
    public boolean insertBatch(List<SysUserRole> userRoles) {
        return userRoleMapper.insertBatch(userRoles);
    }

    @Override
    public boolean existsByRoleId(Long roleId) {
        return userRoleMapper.lambdaQuery().eq(SysUserRole::getRoleId, roleId).exists();
    }

    @Override
    public List<Long> listRoleIdsByUserId(Long userId) {
        return userRoleMapper.lambdaQuery()
                .select(SysUserRole::getRoleId)
                .eq(SysUserRole::getUserId, userId)
                .list()
                .stream()
                .map(SysUserRole::getRoleId)
                .toList();
    }

    @Override
    public List<Long> listUserIdsByRoleId(Long roleId) {
        return userRoleMapper.lambdaQuery()
                .select(SysUserRole::getUserId)
                .eq(SysUserRole::getRoleId, roleId)
                .list()
                .stream()
                .map(SysUserRole::getUserId)
                .toList();
    }

    @Override
    public void deleteByUserId(Long userId) {
        userRoleMapper.lambdaUpdate().eq(SysUserRole::getUserId, userId).remove();
    }

    @Override
    public void deleteByUserIds(List<Long> userIds) {
        userRoleMapper.lambdaUpdate().in(SysUserRole::getUserId, userIds).remove();
    }

    @Override
    public void deleteByRoleIdAndUserIds(Long roleId, List<Long> userIds) {
        userRoleMapper.lambdaUpdate()
                .eq(SysUserRole::getRoleId, roleId)
                .in(SysUserRole::getUserId, userIds)
                .remove();
    }

    @Override
    public List<String> listRoleCodesByUserId(Long userId) {
        return userRoleMapper.selectRoleCodesByUserId(userId);
    }

    @Override
    public void deleteAll() {
        userRoleMapper.delete(Wrappers.<SysUserRole>query().eq("1", 1));
    }

    @Override
    public boolean deleteByRoleIdNotIn(Collection<Long> keepRoleIds) {
        return userRoleMapper.lambdaUpdate().notIn(SysUserRole::getRoleId, keepRoleIds).remove();
    }

    @Override
    public boolean deleteByUserIdNotIn(Collection<Long> keepUserIds) {
        return userRoleMapper.lambdaUpdate().notIn(SysUserRole::getUserId, keepUserIds).remove();
    }
}
