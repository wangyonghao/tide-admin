package top.wyhao.settings.app.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.wyhao.settings.client.MailConfigVO;
import top.wyhao.cmn.db.query.PageResult;
import top.wyhao.settings.adapter.web.dto.ConfigQuery;
import top.wyhao.settings.adapter.web.dto.ConfigRequest;
import top.wyhao.settings.adapter.web.vo.ConfigResult;
import top.wyhao.settings.adapter.web.vo.config.RegisterConfigVO;
import top.wyhao.settings.adapter.web.vo.config.StorageConfigVO;
import top.wyhao.settings.app.assembler.ConfigAssembler;
import top.wyhao.settings.app.service.ConfigService;
import top.wyhao.settings.client.ConfigKeys;
import top.wyhao.settings.client.ConfigStoreApi;
import top.wyhao.settings.domain.exception.ConfigException;
import top.wyhao.settings.domain.gateway.ConfigRepository;
import top.wyhao.settings.domain.model.SysConfig;
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

    private final ConfigRepository configRepository;
    private final ConfigAssembler configAssembler;
    private final ConfigStoreApi configStoreApi;

    @Override
    public PageResult<ConfigResult> page(ConfigQuery query, PageQuery pageQuery) {
        IPage<SysConfig> page = configRepository.page(pageQuery.getPage(), pageQuery.getPageSize(), query);
        return PageResult.of(page).map(configAssembler::toResult);
    }

    @Override
    public ConfigResult detail(Long id) {
        SysConfig configDO = configRepository.findById(id);
        if (configDO == null) {
            throw ConfigException.notFound();
        }
        return configAssembler.toResult(configDO);
    }

    @Override
    public ConfigResult getByKey(String configKey) {
        SysConfig configDO = configRepository.findByKey(configKey);
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
    public MailConfigVO getMailConfig() {
        return configStoreApi.get(ConfigKeys.MAIL, MailConfigVO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMailConfig(MailConfigVO config) {
        configStoreApi.put(ConfigKeys.MAIL, config);
    }

    public void checkMailConfig(MailConfigVO mailConfig) {
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
        configRepository.insert(configDO);
        return configDO.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, ConfigRequest request) {
        SysConfig oldConfig = configRepository.findById(id);
        if (oldConfig == null) {
            throw ConfigException.notFound();
        }
        if (CharSequenceUtil.isNotBlank(request.getConfigKey())) {
            this.checkUnique(request.getConfigKey(), id);
        }
        SysConfig configDO = configAssembler.toEntity(request);
        configDO.setId(id);
        int updated = configRepository.updateById(configDO);
        if (updated <= 0) {
            throw ConfigException.updateConflict();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateByKey(String configKey, ConfigRequest request) {
        SysConfig existConfig = configRepository.findByKey(configKey);
        if (existConfig == null) {
            throw ConfigException.notFound();
        }
        SysConfig configDO = new SysConfig();
        configDO.setId(existConfig.getId());
        configDO.setConfigValue(request.getConfigValue());
        configDO.setDescription(request.getDescription());
        int updated = configRepository.updateById(configDO);
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
        configRepository.deleteByIds(ids);
    }

    @Override
    public void export(ConfigQuery query, HttpServletResponse response) {
        List<SysConfig> list = configRepository.listByQuery(query);
        List<ConfigResult> resultList = configAssembler.toResultList(list);
        ExcelUtils.export(resultList, "系统配置", ConfigResult.class, response);
    }

    private void checkUnique(String configKey, Long id) {
        if (configRepository.existsKey(configKey, id)) {
            throw ConfigException.keyExists();
        }
    }
}
