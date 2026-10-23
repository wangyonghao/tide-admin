package top.wyhao.job.infrastructure.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import top.wyhao.job.domain.gateway.JobLogRepository;
import top.wyhao.job.domain.model.SysJobLog;
import top.wyhao.job.infrastructure.persistence.mapper.SysJobLogMapper;

/**
 * 定时任务执行日志仓储实现
 */
@Repository
@RequiredArgsConstructor
public class JobLogRepositoryImpl implements JobLogRepository {

    private final SysJobLogMapper sysJobLogMapper;

    @Override
    public void insert(SysJobLog log) {
        sysJobLogMapper.insert(log);
    }

    @Override
    public void updateById(SysJobLog log) {
        sysJobLogMapper.updateById(log);
    }

    @Override
    public SysJobLog findLatestRunning(Long jobId, int runningStatus) {
        return sysJobLogMapper.selectOne(
                new LambdaQueryWrapper<SysJobLog>()
                        .eq(SysJobLog::getJobId, jobId)
                        .eq(SysJobLog::getStatus, runningStatus)
                        .orderByDesc(SysJobLog::getStartTime)
                        .last("LIMIT 1"));
    }

    @Override
    public Page<SysJobLog> page(long page, long pageSize, Long jobId, String handlerCode, Integer status) {
        return sysJobLogMapper.selectPage(
                new Page<>(page, pageSize),
                new LambdaQueryWrapper<SysJobLog>()
                        .eq(jobId != null, SysJobLog::getJobId, jobId)
                        .eq(StringUtils.hasText(handlerCode), SysJobLog::getHandlerCode, handlerCode)
                        .eq(status != null, SysJobLog::getStatus, status)
                        .orderByDesc(SysJobLog::getStartTime));
    }
}
