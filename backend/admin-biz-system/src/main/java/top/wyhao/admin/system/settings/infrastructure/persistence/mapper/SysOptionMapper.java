package top.wyhao.settings.infrastructure.persistence.mapper;

import org.apache.ibatis.annotations.Mapper;
import top.wyhao.mybatis.model.BaseMapper;
import top.wyhao.settings.domain.model.SysOption;

/**
 * 字典选项 Mapper
 */
@Mapper
public interface SysOptionMapper extends BaseMapper<SysOption> {
}
