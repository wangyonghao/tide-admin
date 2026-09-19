package top.wyhao.admin.generator.assembler;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import top.wyhao.admin.generator.model.entity.GenConfig;
import top.wyhao.admin.generator.model.entity.GenFieldConfig;
import top.wyhao.admin.generator.model.entity.InnerGenConfig;
import top.wyhao.starter.web.convert.MapStructConfig;

/**
 * 代码生成配置对象转换
 */
@Mapper(config = MapStructConfig.class)
public interface GenConfigAssembler {

    void copy(GenConfig source, @MappingTarget InnerGenConfig target);

    void copy(GenConfig source, @MappingTarget GenConfig target);

    GenFieldConfig copy(GenFieldConfig source);
}
