
package top.wyhao.admin.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
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
import top.wyhao.admin.system.entity.SysConfig;
import top.wyhao.admin.system.mapper.SysConfigMapper;
import top.wyhao.admin.system.model.ConfigModel;
import top.wyhao.admin.system.model.result.ConfigResult;
import top.wyhao.admin.system.model.result.config.*;
import top.wyhao.admin.system.service.ConfigService;
import top.wyhao.cmn.db.util.WrapperUtil;
import top.wyhao.starter.core.model.MailConfig;
import top.wyhao.starter.core.util.validation.Check;
import top.wyhao.starter.excel.util.ExcelUtils;
import top.wyhao.starter.web.core.model.PageQuery;
import top.wyhao.starter.web.core.model.PageResult;

import java.util.List;
import java.util.stream.Collectors;

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

    @Override
    public PageResult<ConfigResult> page(ConfigModel.Query query, PageQuery pageQuery) {
        QueryWrapper<SysConfig> queryWrapper = this.buildQueryWrapper(query);
        WrapperUtil.applySort(queryWrapper, WrapperUtil.parseSort(query.sort()), SysConfig.class);
        IPage<ConfigResult> page = configMapper.selectConfigPage(
                new Page<>(pageQuery.getPage(), pageQuery.getSize()),
                queryWrapper
        );
        return PageResult.build(page);
    }

    @Override
    public ConfigResult detail(Long id) {
        SysConfig configDO = configMapper.selectById(id);
        Check.notNull(configDO, "配置不存在");

        return BeanUtil.copyProperties(configDO, ConfigResult.class);
    }

    @Override
    public ConfigResult getByKey(String configKey) {
        QueryWrapper<SysConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("config_key", configKey);
        SysConfig configDO = configMapper.selectOne(queryWrapper);
        Check.notNull(configDO, "配置不存在");

        return BeanUtil.copyProperties(configDO, ConfigResult.class);
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
        Check.notNull(mailConfig, "邮件配置不存在");
        Check.notBlank(mailConfig.getHost(), "邮件服务器地址未配置");
        Check.notBlank(mailConfig.getUsername(), "发件人邮箱未配置");
        Check.notBlank(mailConfig.getPassword(), "邮箱密码未配置");
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
        String templateId = configMapper.getConfig("sms-template-" + scene, String.class);
        return templateId;
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
    public Long create(ConfigModel.Request request) {
        // 检查唯一性
        this.checkUnique(request.configKey(), null);

        SysConfig configDO = BeanUtil.copyProperties(request, SysConfig.class);
        configMapper.insert(configDO);
        return configDO.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, ConfigModel.Request request) {
        SysConfig oldConfig = configMapper.selectById(id);
        Check.notNull(oldConfig, "配置不存在");

        // 检查唯一性（排除自己）
        if (CharSequenceUtil.isNotBlank(request.configKey())) {
            this.checkUnique(request.configKey(), id);
        }

        SysConfig configDO = BeanUtil.copyProperties(request, SysConfig.class);
        configDO.setId(id);

        int updated = configMapper.updateById(configDO);
        Check.when(updated > 0, "更新失败，配置可能已被修改，请刷新后重试");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateByKey(String configKey, ConfigModel.Request request) {
        QueryWrapper<SysConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("config_key", configKey);
        SysConfig existConfig = configMapper.selectOne(queryWrapper);
        Check.notNull(existConfig, "配置不存在");

        SysConfig configDO = new SysConfig();
        configDO.setId(existConfig.getId());
        configDO.setConfigValue(request.configValue());
        configDO.setDescription(request.description());

        int updated = configMapper.updateById(configDO);
        Check.when(updated > 0, "更新失败，配置可能已被修改，请刷新后重试");
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
    public void export(ConfigModel.Query query, HttpServletResponse response) {
        QueryWrapper<SysConfig> queryWrapper = this.buildQueryWrapper(query);
        WrapperUtil.applySort(queryWrapper, WrapperUtil.parseSort(query.sort()), SysConfig.class);
        List<SysConfig> list = configMapper.selectList(queryWrapper);

        List<ConfigResult> resultList = list.stream()
                .map(config -> BeanUtil.copyProperties(config, ConfigResult.class))
                .collect(Collectors.toList());

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
            Check.when(updated > 0, "更新配置失败");
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
    private QueryWrapper<SysConfig> buildQueryWrapper(ConfigModel.Query query) {
        String configKey = query.configKey();
        String searchWords = query.searchWords();

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
        Check.when(count == 0, "配置键已存在");
    }
}