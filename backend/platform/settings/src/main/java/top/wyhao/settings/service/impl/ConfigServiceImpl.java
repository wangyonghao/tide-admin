package top.wyhao.settings.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.wyhao.cmn.core.model.MailConfig;
import top.wyhao.cmn.db.query.PageResult;
import top.wyhao.cmn.db.query.QueryWrapperBuilder;
import top.wyhao.settings.assembler.ConfigAssembler;
import top.wyhao.settings.client.ConfigKeys;
import top.wyhao.settings.client.ConfigStoreApi;
import top.wyhao.settings.entity.SysConfig;
import top.wyhao.settings.exception.ConfigException;
import top.wyhao.settings.mapper.SysConfigMapper;
import top.wyhao.settings.model.dto.ConfigQuery;
import top.wyhao.settings.model.dto.ConfigRequest;
import top.wyhao.settings.model.result.config.RegisterConfigVO;
import top.wyhao.settings.model.result.config.StorageConfigVO;
import top.wyhao.settings.model.vo.ConfigResult;
import top.wyhao.settings.service.ConfigService;
import top.wyhao.starter.excel.util.ExcelUtils;
import top.wyhao.starter.web.core.model.PageQuery;

import java.util.List;

/**
 * 系统配置业务实现。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ConfigServiceImpl implements ConfigService {

    private final SysConfigMapper configMapper;
    private final ConfigAssembler configAssembler;
    private final ConfigStoreApi configStoreApi;

    @Override
    public PageResult<ConfigResult> page(ConfigQuery query, PageQuery pageQuery) {
        IPage<ConfigResult> page = configMapper.selectConfigPage(
            new Page<>(pageQuery.getPage(), pageQuery.getPageSize()),
            QueryWrapperBuilder.build(query, SysConfig.class)
        );
        return PageResult.of(page);
    }

    @Override
    public ConfigResult detail(Long id) {
        SysConfig configDO = configMapper.selectById(id);
        if (configDO == null) {
            throw ConfigException.notFound();
        }
        return configAssembler.toResult(configDO);
    }

    @Override
    public ConfigResult getByKey(String configKey) {
        QueryWrapper<SysConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("config_key", configKey);
        SysConfig configDO = configMapper.selectOne(queryWrapper);
        if (configDO == null) {
            throw ConfigException.notFound();
        }
        return configAssembler.toResult(configDO);
    }

    @Override
    public RegisterConfigVO getRegisterConfig() {
        return configStoreApi.get(ConfigKeys.REGISTER, RegisterConfigVO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRegisterConfig(RegisterConfigVO config) {
        configStoreApi.put(ConfigKeys.REGISTER, config);
    }

    @Override
    public MailConfig getMailConfig() {
        return configStoreApi.get(ConfigKeys.MAIL, MailConfig.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMailConfig(MailConfig config) {
        configStoreApi.put(ConfigKeys.MAIL, config);
    }

    public void checkMailConfig(MailConfig mailConfig) {
        if (mailConfig == null) {
            throw ConfigException.mailConfigNotFound();
        }
        if (CharSequenceUtil.isBlank(mailConfig.getHost())) {
            throw ConfigException.mailHostNotConfigured();
        }
        if (CharSequenceUtil.isBlank(mailConfig.getUsername())) {
            throw ConfigException.mailUsernameNotConfigured();
        }
        if (CharSequenceUtil.isBlank(mailConfig.getPassword())) {
            throw ConfigException.mailPasswordNotConfigured();
        }
    }

    @Override
    public StorageConfigVO getStorageConfig() {
        return configStoreApi.get(ConfigKeys.STORAGE, StorageConfigVO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStorageConfig(StorageConfigVO config) {
        configStoreApi.put(ConfigKeys.STORAGE, config);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(ConfigRequest request) {
        this.checkUnique(request.getConfigKey(), null);
        SysConfig configDO = configAssembler.toEntity(request);
        configMapper.insert(configDO);
        return configDO.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, ConfigRequest request) {
        SysConfig oldConfig = configMapper.selectById(id);
        if (oldConfig == null) {
            throw ConfigException.notFound();
        }
        if (CharSequenceUtil.isNotBlank(request.getConfigKey())) {
            this.checkUnique(request.getConfigKey(), id);
        }
        SysConfig configDO = configAssembler.toEntity(request);
        configDO.setId(id);
        int updated = configMapper.updateById(configDO);
        if (updated <= 0) {
            throw ConfigException.updateConflict();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateByKey(String configKey, ConfigRequest request) {
        QueryWrapper<SysConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("config_key", configKey);
        SysConfig existConfig = configMapper.selectOne(queryWrapper);
        if (existConfig == null) {
            throw ConfigException.notFound();
        }
        SysConfig configDO = new SysConfig();
        configDO.setId(existConfig.getId());
        configDO.setConfigValue(request.getConfigValue());
        configDO.setDescription(request.getDescription());
        int updated = configMapper.updateById(configDO);
        if (updated <= 0) {
            throw ConfigException.updateConflict();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(List<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return;
        }
        configMapper.deleteByIds(ids);
    }

    @Override
    public void export(ConfigQuery query, HttpServletResponse response) {
        List<SysConfig> list = configMapper.selectList(QueryWrapperBuilder.build(query, SysConfig.class));
        List<ConfigResult> resultList = configAssembler.toResultList(list);
        ExcelUtils.export(resultList, "系统配置", ConfigResult.class, response);
    }

    private void checkUnique(String configKey, Long id) {
        QueryWrapper<SysConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("config_key", configKey);
        if (id != null) {
            queryWrapper.ne("id", id);
        }
        Long count = configMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw ConfigException.keyExists();
        }
    }
}
