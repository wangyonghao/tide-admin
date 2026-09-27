package top.wyhao.settings.infrastructure.persistence.mapper;

import org.apache.ibatis.annotations.Mapper;
import top.wyhao.cmn.db.model.BaseMapper;
import top.wyhao.settings.domain.model.SysOption;

/**
 * 字典选项 Mapper
 */
@Mapper
public interface SysOptionMapper extends BaseMapper<SysOption> {
}
