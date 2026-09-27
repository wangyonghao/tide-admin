package top.wyhao.settings.infrastructure.persistence.mapper;

import cn.hutool.core.text.CharSequenceUtil;
import org.apache.ibatis.annotations.Mapper;
import top.wyhao.cmn.db.model.BaseMapper;
import top.wyhao.settings.domain.model.SysConfig;
import top.wyhao.starter.web.json.util.JSONUtils;

/**
 * 系统配置 Mapper
 *
 * @since 2024/04/26
 */
@Mapper
public interface SysConfigMapper extends BaseMapper<SysConfig> {

    /**
     * 获取配置并转换为指定类型
     *
     * @param configKey 配置键
     * @param clazz     目标类型
     * @return 配置对象
     */
    default <T> T getConfig(String configKey, Class<T> clazz) {
        SysConfig configDO = lambdaQuery().eq(SysConfig::getConfigKey, configKey).one();

        if (configDO == null || CharSequenceUtil.isBlank(configDO.getConfigValue())) {
            return null;
        }

        return JSONUtils.toBean(configDO.getConfigValue(), clazz);
    }
}
