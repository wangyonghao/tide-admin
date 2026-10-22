package top.wyhao.admin.system.security.domain.gateway;

import top.wyhao.mybatis.query.PageResult;
import top.wyhao.admin.system.security.domain.model.AuthenticationLogCriteria;
import top.wyhao.admin.system.security.domain.model.SysAuthenticationLog;

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
