package top.wyhao.admin.system.assembler;

import org.mapstruct.Mapper;
import top.wyhao.admin.system.entity.SysLoginLog;
import top.wyhao.admin.system.model.LoginLogModel;
import top.wyhao.starter.web.convert.MapStructConfig;

import java.util.List;

/**
 * 登录日志对象转换
 */
@Mapper(config = MapStructConfig.class)
public interface LoginLogAssembler {

    LoginLogModel.Result toResult(SysLoginLog log);

    List<LoginLogModel.Result> toResultList(List<SysLoginLog> logs);

    LoginLogModel.Excel toExcel(SysLoginLog log);

    List<LoginLogModel.Excel> toExcelList(List<SysLoginLog> logs);
}
