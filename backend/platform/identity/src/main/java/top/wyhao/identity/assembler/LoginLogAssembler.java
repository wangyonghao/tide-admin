package top.wyhao.identity.assembler;

import org.mapstruct.Mapper;
import top.wyhao.identity.entity.SysLoginLog;
import top.wyhao.starter.web.convert.MapStructConfig;

import java.util.List;
import top.wyhao.identity.model.vo.LoginLogExcelResult;
import top.wyhao.identity.model.vo.LoginLogResult;

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
