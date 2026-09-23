package top.wyhao.admin.system.assembler;

import org.mapstruct.Mapper;
import top.wyhao.admin.system.entity.SysLoginLog;
import top.wyhao.starter.web.convert.MapStructConfig;

import java.util.List;
import top.wyhao.admin.system.model.vo.LoginLogExcelResult;
import top.wyhao.admin.system.model.vo.LoginLogResult;

/**
 * 登录日志对象转换
 */
@Mapper(config = MapStructConfig.class)
public interface LoginLogAssembler {

    LoginLogResult toResult(SysLoginLog log);

    List<LoginLogResult> toResultList(List<SysLoginLog> logs);

    LoginLogExcelResult toExcel(SysLoginLog log);

    List<LoginLogExcelResult> toExcelList(List<SysLoginLog> logs);
}
