package top.wyhao.settings.app.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import com.alicp.jetcache.anno.CacheInvalidate;
import com.alicp.jetcache.anno.Cached;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.wyhao.cmn.core.constant.CacheConstants;
import top.wyhao.cmn.core.constant.StringConstants;
import top.wyhao.cmn.core.util.CollUtils;
import top.wyhao.cmn.db.query.PageParam;
import top.wyhao.cmn.db.query.PageResult;
import top.wyhao.settings.adapter.web.dto.OptionQuery;
import top.wyhao.settings.adapter.web.dto.OptionRequest;
import top.wyhao.settings.adapter.web.vo.OptionResult;
import top.wyhao.settings.app.service.OptionService;
import top.wyhao.settings.domain.exception.OptionException;
import top.wyhao.settings.domain.gateway.OptionRepository;
import top.wyhao.settings.domain.model.SysOption;
import top.wyhao.redisson.util.RedisUtils;
import top.wyhao.web.core.model.LabelValueResult;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * 选项业务实现
 */
@Service
@RequiredArgsConstructor
public class OptionServiceImpl implements OptionService {

    private final OptionRepository optionRepository;
    private final ObjectProvider<OptionService> self;

    @Override
    public PageResult<OptionResult> page(OptionQuery query, PageParam pageParam) {
        IPage<SysOption> page = optionRepository.page(query, pageParam);
        return PageResult.of(page).map(this::toResult);
    }

    @Override
    public OptionResult detail(Long id) {
        return toResult(require(id));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(OptionRequest request) {
        checkValueUnique(request.getOptionType(), request.getValue(), null);
        SysOption entity = toEntity(request);
        if (entity.getSort() == null) {
            entity.setSort(0);
        }
        if (entity.getEnabled() == null) {
            entity.setEnabled(true);
        }
        optionRepository.insert(entity);
        evict(entity.getOptionType());
        return entity.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, OptionRequest request) {
        SysOption old = require(id);
        checkValueUnique(request.getOptionType(), request.getValue(), id);
        SysOption entity = toEntity(request);
        entity.setId(id);
        optionRepository.updateById(entity);
        evict(old.getOptionType());
        evict(request.getOptionType());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(List<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            throw OptionException.deleteIdsEmpty();
        }
        List<SysOption> options = optionRepository.findByIds(ids);
        optionRepository.deleteByIds(ids);
        Set<String> types = new LinkedHashSet<>();
        for (SysOption option : options) {
            types.add(option.getOptionType());
        }
        types.forEach(this::evict);
    }

    @Override
    @CacheInvalidate(name = CacheConstants.OPTION_KEY_PREFIX, key = "#type")
    public void clearCache(String type) {
        RedisUtils.deleteByPattern(CacheConstants.OPTION_KEY_PREFIX + type + StringConstants.ASTERISK);
    }

    @Override
    @Cached(key = "#type", name = CacheConstants.OPTION_KEY_PREFIX)
    public List<LabelValueResult<String>> listByType(String type) {
        if (CharSequenceUtil.isBlank(type)) {
            return List.of();
        }
        List<SysOption> list = optionRepository.listEnabledByType(type);
        return list.stream()
                .map(item -> new LabelValueResult<>(item.getLabel(), item.getValue(), item.getExt()))
                .toList();
    }

    @Override
    public List<LabelValueResult<String>> listTypes() {
        return CollUtils.mapToList(optionRepository.listDistinctTypes(), type -> new LabelValueResult<>(type, type));
    }

    private SysOption require(Long id) {
        SysOption option = optionRepository.findById(id);
        if (option == null) {
            throw OptionException.notFound(id);
        }
        return option;
    }

    private void checkValueUnique(String type, String value, Long excludeId) {
        if (optionRepository.existsValue(type, value, excludeId)) {
            throw OptionException.valueExist(type, value);
        }
    }

    private void evict(String type) {
        if (CharSequenceUtil.isNotBlank(type)) {
            self.getObject().clearCache(type);
        }
    }

    private SysOption toEntity(OptionRequest request) {
        SysOption entity = new SysOption();
        entity.setOptionType(request.getOptionType());
        entity.setValue(request.getValue());
        entity.setLabel(request.getLabel());
        entity.setExt(request.getExtra());
        entity.setSort(request.getSort());
        entity.setEnabled(request.getEnabled());
        entity.setDescription(request.getDescription());
        return entity;
    }

    private OptionResult toResult(SysOption entity) {
        return new OptionResult(
                entity.getId(),
                entity.getOptionType(),
                entity.getValue(),
                entity.getLabel(),
                entity.getExt(),
                entity.getSort(),
                entity.getEnabled(),
                entity.getDescription());
    }
}
