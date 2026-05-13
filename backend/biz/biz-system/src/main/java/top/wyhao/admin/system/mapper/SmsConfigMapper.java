
package top.wyhao.admin.system.mapper;

import org.apache.ibatis.annotations.Mapper;
import top.wyhao.starter.data.mapper.BaseMapper;
import top.wyhao.admin.system.entity.SmsConfigDO;

/**
 * 短信配置 Mapper
 *
 * @author luoqiz
 * @since 2025/03/15 18:41
 */
@Mapper
public interface SmsConfigMapper extends BaseMapper<SmsConfigDO> {}