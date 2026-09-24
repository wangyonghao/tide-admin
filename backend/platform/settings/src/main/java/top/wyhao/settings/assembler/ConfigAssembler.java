package top.wyhao.admin.system.assembler;

import org.mapstruct.Mapper;
import top.wyhao.admin.system.entity.SysConfig;
import top.wyhao.starter.web.convert.MapStructConfig;

import java.util.List;
import top.wyhao.admin.system.model.dto.ConfigRequest;
import top.wyhao.admin.system.model.vo.ConfigResult;

/**
 * 系统配置对象转换
 */
@Mapper(config = MapStructConfig.class)
public interface ConfigAssembler {

    ConfigResult toResult(SysConfig config);

    List<ConfigResult> toResultList(List<SysConfig> configs);

    SysConfig toEntity(ConfigRequest request);
}
