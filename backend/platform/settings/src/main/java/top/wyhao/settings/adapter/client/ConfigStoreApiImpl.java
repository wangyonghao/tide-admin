package top.wyhao.settings.adapter.client;

import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.wyhao.settings.client.ConfigStoreApi;
import top.wyhao.settings.entity.SysConfig;
import top.wyhao.settings.exception.ConfigException;
import top.wyhao.settings.mapper.SysConfigMapper;

/**
 * 通用配置存储实现。
 */
@Service
@RequiredArgsConstructor
public class ConfigStoreApiImpl implements ConfigStoreApi {

    private final SysConfigMapper configMapper;

    @Override
    public <T> T get(String configKey, Class<T> type) {
        return configMapper.getConfig(configKey, type);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void put(String configKey, Object config) {
        SysConfig existConfig = configMapper.lambdaQuery().eq(SysConfig::getConfigKey, configKey).one();
        String configValue = JSONUtil.toJsonStr(config);

        if (existConfig != null) {
            SysConfig updateConfig = new SysConfig();
            updateConfig.setId(existConfig.getId());
            updateConfig.setConfigValue(configValue);
            int updated = configMapper.updateById(updateConfig);
            if (updated <= 0) {
                throw ConfigException.updateFailed();
            }
            return;
        }

        SysConfig newConfig = new SysConfig();
        newConfig.setConfigKey(configKey);
        newConfig.setConfigValue(configValue);
        newConfig.setDescription(configKey + "配置");
        configMapper.insert(newConfig);
    }
}
