package top.wyhao.admin.system.assembler;

import org.mapstruct.Mapper;
import top.wyhao.admin.system.entity.SysConfig;
import top.wyhao.admin.system.model.ConfigModel;
import top.wyhao.admin.system.model.result.ConfigResult;
import top.wyhao.starter.web.convert.MapStructConfig;

import java.util.List;

/**
 * 系统配置对象转换
 */
@Mapper(config = MapStructConfig.class)
public interface ConfigAssembler {

    ConfigResult toResult(SysConfig config);

    List<ConfigResult> toResultList(List<SysConfig> configs);

    SysConfig toEntity(ConfigModel.Request request);
}
