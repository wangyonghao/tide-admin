package top.wyhao.admin.generator.exception;

import cn.hutool.core.util.StrUtil;
import top.wyhao.cmn.core.exception.BizException;

/**
 * 代码生成业务异常
 */
public class GeneratorException extends BizException {

    public GeneratorException(String message) {
        super(message);
    }

    public GeneratorException(String code, String message) {
        super(code, message);
    }

    public static GeneratorException of(String message) {
        return new GeneratorException(message);
    }

    public static GeneratorException of(String code, String message) {
        return new GeneratorException(code, message);
    }

    public static GeneratorException typeMappingNotConfigured() {
        return of("GENERATOR_TYPE_MAPPING_NOT_CONFIGURED", "请先配置对应数据库的类型映射");
    }

    public static GeneratorException genConfigNotConfigured(String tableName) {
        return of("GENERATOR_GEN_CONFIG_NOT_CONFIGURED", StrUtil.format("请先进行数据表 [{}] 生成配置", tableName));
    }

    public static GeneratorException fieldConfigNotConfigured(String tableName) {
        return of("GENERATOR_FIELD_CONFIG_NOT_CONFIGURED", StrUtil.format("请先进行数据表 [{}] 字段配置", tableName));
    }
}
