package top.wyhao.identity.app.assembler;

import org.mapstruct.Mapper;
import top.wyhao.identity.domain.model.SysLoginLog;
import top.wyhao.starter.web.convert.MapStructConfig;

import java.util.List;
import top.wyhao.identity.adapter.web.vo.LoginLogExcelResult;
import top.wyhao.identity.adapter.web.vo.LoginLogResult;

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
