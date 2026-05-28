
package top.wyhao.admin.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import top.wyhao.admin.system.entity.SysDict;
import top.wyhao.admin.system.mapper.SysDictMapper;
import top.wyhao.admin.system.model.DictRecord;
import top.wyhao.admin.system.service.DictService;
import top.wyhao.cmn.db.util.WrapperUtil;
import top.wyhao.starter.core.util.CollUtils;
import top.wyhao.starter.web.core.model.LabelValueResult;
import top.wyhao.starter.web.core.model.PageQuery;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 字典业务实现
 *

 * @since 2026/5/13
 */
@Service
@RequiredArgsConstructor
public class DictServiceImpl extends ServiceImpl<SysDictMapper, SysDict> implements DictService {
    @Override
    public IPage<SysDict> page(DictRecord.Query query, PageQuery pageQuery) {
        // 构建查询条件（WrapperUtil 现在支持 Record 参数上的 @Query 注解）
        QueryWrapper<SysDict> queryWrapper = WrapperUtil.build(query);
        // 分页查询
        Page<SysDict> page = new Page<>(pageQuery.getPage(), pageQuery.getSize());
        return baseMapper.selectPage(page, queryWrapper);
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