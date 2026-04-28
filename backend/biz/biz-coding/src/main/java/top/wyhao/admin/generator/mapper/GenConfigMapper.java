
package top.wyhao.admin.generator.mapper;

import org.apache.ibatis.annotations.Mapper;
import top.wyhao.admin.generator.model.entity.GenConfigDO;
import top.wyhao.starter.data.mapper.BaseMapper;

/**
 * 生成配置 Mapper
 *
 * @author Charles7c
 * @since 2023/4/12 23:56
 */
@Mapper
public interface GenConfigMapper extends BaseMapper<GenConfigDO> {
}
