package top.wyhao.admin.system.assembler;

import org.mapstruct.Mapper;
import top.wyhao.admin.system.entity.SysOperationLog;
import top.wyhao.admin.system.model.OperationLogModel;
import top.wyhao.starter.web.convert.MapStructConfig;
import top.wyhao.starter.web.log.OperationLog;

import java.util.List;

/**
 * 操作日志对象转换
 */
@Mapper(config = MapStructConfig.class)
public interface OperationLogAssembler {

    SysOperationLog toEntity(OperationLog operationLog);

    OperationLogModel.Result toResult(SysOperationLog log);

    List<OperationLogModel.Result> toResultList(List<SysOperationLog> logs);

    OperationLogModel.Detail toDetail(SysOperationLog log);

    OperationLogModel.Excel toExcel(OperationLogModel source);

    List<OperationLogModel.Excel> toExcelList(List<OperationLogModel> sources);
}
