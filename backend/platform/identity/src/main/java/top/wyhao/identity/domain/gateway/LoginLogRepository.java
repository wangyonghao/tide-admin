package top.wyhao.identity.domain.gateway;

import top.wyhao.cmn.db.query.PageResult;
import top.wyhao.identity.domain.model.LoginLogCriteria;
import top.wyhao.identity.domain.model.SysLoginLog;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 登录日志仓储。
 */
public interface LoginLogRepository {

    void insert(SysLoginLog loginLog);

    PageResult<SysLoginLog> page(LoginLogCriteria criteria, long page, long pageSize);

    List<SysLoginLog> list(LoginLogCriteria criteria);

    int deleteBefore(LocalDateTime expireTime);
}
