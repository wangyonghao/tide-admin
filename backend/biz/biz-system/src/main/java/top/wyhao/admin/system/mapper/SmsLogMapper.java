
package top.wyhao.admin.system.mapper;

import org.apache.ibatis.annotations.Mapper;
import top.wyhao.starter.data.mapper.BaseMapper;
import top.wyhao.admin.system.model.entity.SmsLogDO;

/**
 * 短信日志 Mapper
 *
 * @author luoqiz
 * @since 2025/03/15 22:15
 */
@Mapper
public interface SmsLogMapper extends BaseMapper<SmsLogDO> {}