package top.wyhao.job.domain.gateway;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import top.wyhao.job.domain.model.SysJob;

import java.util.List;

/**
 * 定时任务仓储
 */
public interface JobRepository {

    SysJob findById(Long id);

    void insert(SysJob job);

    void updateById(SysJob job);

    void deleteById(Long id);

    List<SysJob> listAll();

    Page<SysJob> page(long page, long pageSize, String name, String handlerCode, Integer status);
}
