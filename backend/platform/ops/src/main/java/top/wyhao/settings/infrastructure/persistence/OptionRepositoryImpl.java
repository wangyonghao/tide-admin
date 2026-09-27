package top.wyhao.settings.infrastructure.persistence;

import cn.hutool.core.text.CharSequenceUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import top.wyhao.cmn.db.query.PageFactory;
import top.wyhao.cmn.db.query.PageParam;
import top.wyhao.cmn.db.query.QueryWrapperBuilder;
import top.wyhao.cmn.db.query.SortableQuery;
import top.wyhao.cmn.db.query.Sorts;
import top.wyhao.settings.domain.gateway.OptionRepository;
import top.wyhao.settings.domain.model.SysOption;
import top.wyhao.settings.infrastructure.persistence.mapper.SysOptionMapper;

import java.util.List;

/**
 * 选项仓储实现
 */
@Repository
@RequiredArgsConstructor
public class OptionRepositoryImpl implements OptionRepository {

    private final SysOptionMapper optionMapper;

    @Override
    public SysOption findById(Long id) {
        return optionMapper.selectById(id);
    }

    @Override
    public List<SysOption> findByIds(List<Long> ids) {
        return optionMapper.selectByIds(ids);
    }

    @Override
    public void insert(SysOption option) {
        optionMapper.insert(option);
    }

    @Override
    public void updateById(SysOption option) {
        optionMapper.updateById(option);
    }

    @Override
    public void deleteByIds(List<Long> ids) {
        optionMapper.deleteByIds(ids);
    }

    @Override
    public IPage<SysOption> page(SortableQuery query, PageParam pageParam) {
        return optionMapper.selectPage(
                PageFactory.build(pageParam, query, SysOption.class,
                        Sorts.asc(SysOption::getOptionType).thenAsc(SysOption::getSort)),
                QueryWrapperBuilder.build(query, SysOption.class));
    }

    @Override
    public List<SysOption> listEnabledByType(String type) {
        return optionMapper.lambdaQuery()
                .eq(SysOption::getOptionType, type)
                .eq(SysOption::getEnabled, true)
                .orderByAsc(SysOption::getSort)
                .orderByAsc(SysOption::getId)
                .list();
    }

    @Override
    public List<String> listDistinctTypes() {
        return optionMapper.query()
                .select("DISTINCT option_type")
                .orderByAsc("option_type")
                .list()
                .stream()
                .map(SysOption::getOptionType)
                .filter(CharSequenceUtil::isNotBlank)
                .toList();
    }

    @Override
    public boolean existsValue(String type, String value, Long excludeId) {
        return optionMapper.lambdaQuery()
                .eq(SysOption::getOptionType, type)
                .eq(SysOption::getValue, value)
                .ne(excludeId != null, SysOption::getId, excludeId)
                .exists();
    }

    @Override
    public long countIdGreaterThan(Long id) {
        return optionMapper.lambdaQuery().gt(SysOption::getId, id).count();
    }

    @Override
    public boolean deleteIdGreaterThan(Long id) {
        return optionMapper.lambdaUpdate().gt(SysOption::getId, id).remove();
    }
}
