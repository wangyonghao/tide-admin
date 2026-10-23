package top.wyhao.job.infrastructure.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import top.wyhao.job.domain.gateway.JobRepository;
import top.wyhao.job.domain.model.SysJob;
import top.wyhao.job.infrastructure.persistence.mapper.SysJobMapper;

import java.util.List;

/**
 * 定时任务仓储实现
 */
@Repository
@RequiredArgsConstructor
public class JobRepositoryImpl implements JobRepository {

    private final SysJobMapper sysJobMapper;

    @Override
    public SysJob findById(Long id) {
        return sysJobMapper.selectById(id);
    }

    @Override
    public void insert(SysJob job) {
        sysJobMapper.insert(job);
    }

    @Override
    public void updateById(SysJob job) {
        sysJobMapper.updateById(job);
    }

    @Override
    public void deleteById(Long id) {
        sysJobMapper.deleteById(id);
    }

    @Override
    public List<SysJob> listAll() {
        return sysJobMapper.selectList(null);
    }

    @Override
    public Page<SysJob> page(long page, long pageSize, String name, String handlerCode, Integer status) {
        return sysJobMapper.selectPage(
                new Page<>(page, pageSize),
                new LambdaQueryWrapper<SysJob>()
                        .like(StringUtils.hasText(name), SysJob::getName, name)
                        .eq(StringUtils.hasText(handlerCode), SysJob::getHandlerCode, handlerCode)
                        .eq(status != null, SysJob::getStatus, status)
                        .orderByDesc(SysJob::getCreateTime));
    }
}
