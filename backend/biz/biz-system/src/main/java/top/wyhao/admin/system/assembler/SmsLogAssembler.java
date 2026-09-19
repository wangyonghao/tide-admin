package top.wyhao.admin.system.assembler;

import org.mapstruct.Mapper;
import top.wyhao.admin.system.entity.SysSmsLog;
import top.wyhao.admin.system.model.SmsLogModel;
import top.wyhao.starter.web.convert.MapStructConfig;

import java.util.List;

/**
 * 短信日志对象转换
 */
@Mapper(config = MapStructConfig.class)
public interface SmsLogAssembler {

    SmsLogModel.Result toResult(SysSmsLog log);

    List<SmsLogModel.Result> toResultList(List<SysSmsLog> logs);

    SysSmsLog toEntity(SmsLogModel.Request request);
}
