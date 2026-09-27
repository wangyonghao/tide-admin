package top.wyhao.settings.app.assembler;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import top.wyhao.settings.adapter.web.dto.ConfigRequest;
import top.wyhao.settings.adapter.web.vo.ConfigResult;
import top.wyhao.settings.domain.model.SysConfig;
import top.wyhao.starter.web.convert.MapStructConfig;

import java.util.List;

/**
 * 系统配置对象转换
 */
@Mapper(config = MapStructConfig.class)
public interface ConfigAssembler {

    @Mapping(source = "createTime", target = "createdAt")
    @Mapping(source = "updateTime", target = "updatedAt")
    @Mapping(target = "version", ignore = true)
    ConfigResult toResult(SysConfig config);

    List<ConfigResult> toResultList(List<SysConfig> configs);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    SysConfig toEntity(ConfigRequest request);
}
