package top.wyhao.job.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.wyhao.job.domain.model.SysJobLog;

@Mapper
public interface SysJobLogMapper extends BaseMapper<SysJobLog> {
}
