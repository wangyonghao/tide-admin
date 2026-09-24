package top.wyhao.admin.system.assembler;

import org.mapstruct.Mapper;
import top.wyhao.admin.system.entity.SysSmsLog;
import top.wyhao.starter.web.convert.MapStructConfig;

import java.util.List;
import top.wyhao.admin.system.model.dto.SmsLogRequest;
import top.wyhao.admin.system.model.vo.SmsLogResult;

/**
 * 短信日志对象转换
 */
@Mapper(config = MapStructConfig.class)
public interface SmsLogAssembler {

    SmsLogResult toResult(SysSmsLog log);

    List<SmsLogResult> toResultList(List<SysSmsLog> logs);

    SysSmsLog toEntity(SmsLogRequest request);
}
