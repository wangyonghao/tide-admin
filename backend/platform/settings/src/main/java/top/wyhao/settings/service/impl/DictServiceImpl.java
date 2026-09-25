
package top.wyhao.settings.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import top.wyhao.settings.entity.SysDict;
import top.wyhao.settings.mapper.SysDictMapper;
import top.wyhao.settings.service.DictService;
import top.wyhao.cmn.db.query.PageFactory;
import top.wyhao.cmn.db.query.PageParam;
import top.wyhao.cmn.db.query.QueryWrapperBuilder;
import top.wyhao.starter.core.util.CollUtils;
import top.wyhao.starter.web.core.model.LabelValueResult;

import java.util.List;
import java.util.stream.Collectors;
import top.wyhao.settings.model.dto.DictQuery;

/**
 * 字典业务实现
 *

 * @since 2026/5/13
 */
@Service
@RequiredArgsConstructor
public class DictServiceImpl extends ServiceImpl<SysDictMapper, SysDict> implements DictService {
    @Override
    public IPage<SysDict> page(DictQuery query, PageParam pageParam) {
        return baseMapper.selectPage(PageFactory.build(pageParam, query, SysDict.class),
                QueryWrapperBuilder.build(query, SysDict.class));
    }

    @Override
    public List<LabelValueResult<String>> listByDictType(String dictType) {
        List<SysDict> list = baseMapper.lambdaQuery().eq(SysDict::getDictType, dictType).list();
        return list.stream().map(item -> new LabelValueResult<>(item.getLabel(), item.getValue())).collect(Collectors.toList());
    }

    @Override
    public List<LabelValueResult<String>> listEnumDict() {
        // 查询所有字典类型，去重
        List<String> dictTypes = this.lambdaQuery()
                .select(SysDict::getDictType)
                .groupBy(SysDict::getDictType)
                .list()
                .stream()
                .map(SysDict::getDictType)
                .collect(Collectors.toList());
        return CollUtils.mapToList(dictTypes, type -> new LabelValueResult<>(type, type));
    }
}