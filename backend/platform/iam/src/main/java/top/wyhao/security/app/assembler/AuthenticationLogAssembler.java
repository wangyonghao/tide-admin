package top.wyhao.security.app.assembler;

import org.mapstruct.Mapper;
import top.wyhao.security.domain.model.SysAuthenticationLog;
import top.wyhao.web.convert.MapStructConfig;

import java.util.List;
import top.wyhao.security.adapter.web.vo.AuthenticationLogExcelResult;
import top.wyhao.security.adapter.web.vo.AuthenticationLogResult;

/**
 * 登录日志对象转换
 */
@Mapper(config = MapStructConfig.class)
public interface AuthenticationLogAssembler {

    AuthenticationLogResult toResult(SysAuthenticationLog log);

    List<AuthenticationLogResult> toResultList(List<SysAuthenticationLog> logs);

    AuthenticationLogExcelResult toExcel(SysAuthenticationLog log);

    List<AuthenticationLogExcelResult> toExcelList(List<SysAuthenticationLog> logs);
}
