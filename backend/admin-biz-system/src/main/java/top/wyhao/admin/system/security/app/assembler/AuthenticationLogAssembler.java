package top.wyhao.admin.system.security.app.assembler;

import org.mapstruct.Mapper;
import top.wyhao.admin.system.security.domain.model.SysAuthenticationLog;
import top.wyhao.web.convert.MapStructConfig;

import java.util.List;
import top.wyhao.admin.system.security.adapter.web.vo.AuthenticationLogExcelResult;
import top.wyhao.admin.system.security.adapter.web.vo.AuthenticationLogResult;

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
