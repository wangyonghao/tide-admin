package top.wyhao.identity.domain.gateway;

import java.util.List;

/**
 * 用户历史密码仓储。
 */
public interface PasswordHistoryRepository {

    void add(Long userId, String password, int keepCount);

    void deleteByUserIds(List<Long> userIds);

    List<String> recentPasswords(Long userId, int count);

    void deleteAll();
}
