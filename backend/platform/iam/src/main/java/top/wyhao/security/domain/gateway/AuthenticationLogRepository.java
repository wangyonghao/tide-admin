package top.wyhao.security.domain.gateway;

import top.wyhao.cmn.db.query.PageResult;
import top.wyhao.security.domain.model.AuthenticationLogCriteria;
import top.wyhao.security.domain.model.SysAuthenticationLog;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 登录日志仓储。
 */
public interface AuthenticationLogRepository {

    void insert(SysAuthenticationLog loginLog);

    PageResult<SysAuthenticationLog> page(AuthenticationLogCriteria criteria, long page, long pageSize);

    List<SysAuthenticationLog> list(AuthenticationLogCriteria criteria);

    int deleteBefore(LocalDateTime expireTime);
}
