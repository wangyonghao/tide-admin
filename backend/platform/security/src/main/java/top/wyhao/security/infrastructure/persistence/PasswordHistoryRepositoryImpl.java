package top.wyhao.identity.infrastructure.persistence;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import top.wyhao.identity.domain.gateway.PasswordHistoryRepository;
import top.wyhao.identity.domain.model.SysUserPasswordHistory;
import top.wyhao.identity.infrastructure.persistence.mapper.SysUserPasswordHistoryMapper;
import top.wyhao.cmn.core.util.CollUtils;

import java.util.List;

/**
 * 用户历史密码仓储实现。
 */
@Repository
@RequiredArgsConstructor
public class PasswordHistoryRepositoryImpl implements PasswordHistoryRepository {

    private final SysUserPasswordHistoryMapper historyMapper;

    @Override
    public void add(Long userId, String password, int keepCount) {
        historyMapper.insert(new SysUserPasswordHistory(userId, password));
        historyMapper.deleteExpired(userId, keepCount);
    }

    @Override
    public void deleteByUserIds(List<Long> userIds) {
        historyMapper.lambdaUpdate().in(SysUserPasswordHistory::getUserId, userIds).remove();
    }

    @Override
    public List<String> recentPasswords(Long userId, int count) {
        List<SysUserPasswordHistory> list = historyMapper.lambdaQuery()
                .select(SysUserPasswordHistory::getPassword)
                .eq(SysUserPasswordHistory::getUserId, userId)
                .orderByDesc(SysUserPasswordHistory::getCreateTime)
                .last("LIMIT %s".formatted(count))
                .list();
        return CollUtils.mapToList(list, SysUserPasswordHistory::getPassword);
    }

    @Override
    public void deleteAll() {
        historyMapper.delete(Wrappers.<SysUserPasswordHistory>query().eq("1", 1));
    }
}
