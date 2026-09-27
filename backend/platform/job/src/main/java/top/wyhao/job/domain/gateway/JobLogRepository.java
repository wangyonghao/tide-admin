package top.wyhao.job.domain.gateway;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import top.wyhao.job.domain.model.SysJobLog;

/**
 * 定时任务执行日志仓储
 */
public interface JobLogRepository {

    void insert(SysJobLog log);

    void updateById(SysJobLog log);

    /**
     * 查询指定任务最近一条运行中的日志
     */
    SysJobLog findLatestRunning(Long jobId, int runningStatus);

    Page<SysJobLog> page(long page, long pageSize, Long jobId, String handlerCode, Integer status);
}
