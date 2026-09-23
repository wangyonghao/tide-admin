
package top.wyhao.admin.system.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.wyhao.admin.cmn.sms.SmsConfig;
import top.wyhao.admin.system.assembler.ConfigAssembler;
import top.wyhao.admin.system.entity.SysConfig;
import top.wyhao.admin.system.exception.ConfigException;
import top.wyhao.admin.system.mapper.SysConfigMapper;
import top.wyhao.admin.system.model.result.config.*;
import top.wyhao.admin.system.service.ConfigService;
import top.wyhao.cmn.db.query.QueryWrapperBuilder;
import top.wyhao.starter.core.model.MailConfig;
import top.wyhao.starter.excel.util.ExcelUtils;
import top.wyhao.starter.web.core.model.PageQuery;
import top.wyhao.starter.web.core.model.PageResult;

import java.util.List;
import top.wyhao.admin.system.model.dto.ConfigQuery;
import top.wyhao.admin.system.model.dto.ConfigRequest;
import top.wyhao.admin.system.model.vo.ConfigResult;

/**
 * 系统配置业务实现
 *
 * @since 2024/04/26
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ConfigServiceImpl implements ConfigService {

    private final SysConfigMapper configMapper;
    private final ConfigAssembler configAssembler;

    @Override
    public PageResult<ConfigResult> page(ConfigQuery query, PageQuery pageQuery) {
        IPage<ConfigResult> page = configMapper.selectConfigPage(
                new Page<>(pageQuery.getPage(), pageQuery.getPageSize()),
                QueryWrapperBuilder.build(query, SysConfig.class)
        );
        return PageResult.build(page);
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
    public SiteConfigVO getSiteConfig() {
        return this.configMapper.getConfig("site", SiteConfigVO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSiteConfig(SiteConfigVO config) {
        this.updateConfig("site", config);
    }

    @Override
    public LoginConfigVO getLoginConfig() {
        return this.configMapper.getConfig("login", LoginConfigVO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateLoginConfig(LoginConfigVO config) {
        this.updateConfig("login", config);
    }

    @Override
    public RegisterConfigVO getRegisterConfig() {
        return this.configMapper.getConfig("register", RegisterConfigVO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRegisterConfig(RegisterConfigVO config) {
        this.updateConfig("register", config);
    }

    @Override
    public MailConfig getMailConfig() {
        return this.configMapper.getConfig("mail", MailConfig.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMailConfig(MailConfig config) {
        this.updateConfig("mail", config);
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
    public SmsConfig getSmsConfig() {
        return this.configMapper.getConfig("sms", SmsConfig.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSmsConfig(SmsConfigVO config) {
        this.updateConfig("sms", config);
    }

    @Override
    public String getSmsTemplate(String scene) {
        return configMapper.getConfig("sms-template-" + scene, String.class);
    }

    @Override
    public StorageConfigVO getStorageConfig() {
        return this.configMapper.getConfig("storage", StorageConfigVO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStorageConfig(StorageConfigVO config) {
        this.updateConfig("storage", config);
    }

    @Override
    public SecurityConfigVO getSecurityConfig() {
        return this.configMapper.getConfig("security", SecurityConfigVO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSecurityConfig(SecurityConfigVO config) {
        this.updateConfig("security", config);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(ConfigRequest request) {
        // 检查唯一性
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

        // 检查唯一性（排除自己）
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

    /**
     * 更新配置
     *
     * @param configKey 配置键
     * @param config    配置对象
     */
    private void updateConfig(String configKey, Object config) {
        SysConfig existConfig = configMapper.lambdaQuery().eq(SysConfig::getConfigKey, configKey).one();
        String configValue = JSONUtil.toJsonStr(config);

        if (existConfig != null) {
            // 更新现有配置
            SysConfig updateConfig = new SysConfig();
            updateConfig.setId(existConfig.getId());
            updateConfig.setConfigValue(configValue);

            int updated = configMapper.updateById(updateConfig);
            if (updated <= 0) {
                throw ConfigException.updateFailed();
            }
        } else {
            // 创建新配置
            SysConfig newConfig = new SysConfig();
            newConfig.setConfigKey(configKey);
            newConfig.setConfigValue(configValue);
            newConfig.setDescription(configKey + "配置");

            configMapper.insert(newConfig);
        }
    }

    /**
     * 构建查询条件
     *
     * @param query 查询条件
     * @return 查询包装器
     */
    private QueryWrapper<SysConfig> buildQueryWrapper(ConfigQuery query) {
        String configKey = query.getConfigKey();
        String searchWords = query.getSearchWords();

        return new QueryWrapper<SysConfig>()
                .like(CharSequenceUtil.isNotBlank(configKey), "config_key", configKey)
                .and(CharSequenceUtil.isNotBlank(searchWords), q -> q
                        .like("config_key", searchWords)
                        .or()
                        .like("description", searchWords))
                .orderByAsc("config_key");
    }

    /**
     * 检查唯一性
     *
     * @param configKey 配置键
     * @param id        当前配置ID（更新时传入，新增时传null）
     */
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