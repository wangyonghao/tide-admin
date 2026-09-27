package top.wyhao.audit.assembler;

import org.mapstruct.Mapper;
import top.wyhao.audit.entity.SysOperationLog;
import top.wyhao.starter.web.convert.MapStructConfig;
import top.wyhao.starter.web.log.OperationLog;

import java.util.List;
import top.wyhao.audit.model.vo.OperationLogDetailResult;
import top.wyhao.audit.model.vo.OperationLogExcelResult;
import top.wyhao.audit.model.vo.OperationLogResult;

/**
 * 操作日志对象转换
 */
@Mapper(config = MapStructConfig.class)
public interface OperationLogAssembler {

    SysOperationLog toEntity(OperationLog operationLog);

    OperationLogResult toResult(SysOperationLog log);

    List<OperationLogResult> toResultList(List<SysOperationLog> logs);

    OperationLogDetailResult toDetail(SysOperationLog log);

    OperationLogExcelResult toExcel(OperationLogExcelResult source);

    List<OperationLogExcelResult> toExcelList(List<OperationLogExcelResult> sources);
}
