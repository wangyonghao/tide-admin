package top.wyhao.job.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.wyhao.job.domain.model.SysJob;

@Mapper
public interface SysJobMapper extends BaseMapper<SysJob> {
}
