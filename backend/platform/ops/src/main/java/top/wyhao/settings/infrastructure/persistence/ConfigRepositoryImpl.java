package top.wyhao.settings.infrastructure.persistence;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import top.wyhao.cmn.db.query.QueryWrapperBuilder;
import top.wyhao.settings.domain.gateway.ConfigRepository;
import top.wyhao.settings.domain.model.SysConfig;
import top.wyhao.settings.infrastructure.persistence.mapper.SysConfigMapper;

import java.util.List;

/**
 * 系统配置仓储实现
 */
@Repository
@RequiredArgsConstructor
public class ConfigRepositoryImpl implements ConfigRepository {

    private final SysConfigMapper configMapper;

    @Override
    public SysConfig findById(Long id) {
        return configMapper.selectById(id);
    }

    @Override
    public SysConfig findByKey(String configKey) {
        return configMapper.lambdaQuery().eq(SysConfig::getConfigKey, configKey).one();
    }

    @Override
    public void insert(SysConfig config) {
        configMapper.insert(config);
    }

    @Override
    public int updateById(SysConfig config) {
        return configMapper.updateById(config);
    }

    @Override
    public void deleteByIds(List<Long> ids) {
        configMapper.deleteByIds(ids);
    }

    @Override
    public List<SysConfig> listByQuery(Object query) {
        return configMapper.selectList(QueryWrapperBuilder.build(query, SysConfig.class));
    }

    @Override
    public IPage<SysConfig> page(long page, long pageSize, Object query) {
        return configMapper.selectPage(
                new Page<>(page, pageSize),
                QueryWrapperBuilder.build(query, SysConfig.class));
    }

    @Override
    public boolean existsKey(String configKey, Long excludeId) {
        QueryWrapper<SysConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("config_key", configKey);
        if (excludeId != null) {
            queryWrapper.ne("id", excludeId);
        }
        return configMapper.selectCount(queryWrapper) > 0;
    }

    @Override
    public <T> T getAs(String configKey, Class<T> type) {
        return configMapper.getConfig(configKey, type);
    }
}
