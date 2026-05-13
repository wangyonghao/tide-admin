
package top.wyhao.admin.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import top.wyhao.admin.system.mapper.DictMapper;
import top.wyhao.admin.system.entity.SysDict;
import top.wyhao.admin.system.model.DictQuery;
import top.wyhao.admin.system.model.vo.DictResult;
import top.wyhao.admin.system.service.DictService;
import top.wyhao.starter.core.util.CollUtils;
import top.wyhao.starter.data.util.QueryWrapperUtil;
import top.wyhao.starter.web.core.model.PageQuery;
import top.wyhao.starter.web.core.model.LabelValueResult;
import top.wyhao.starter.web.core.model.PageResult;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 字典业务实现
 *
 * @author Yonghao Wang
 * @since 2026/5/13
 */
@Service
@RequiredArgsConstructor
public class DictServiceImpl extends ServiceImpl<DictMapper, SysDict> implements DictService {
    @Override
    public IPage<SysDict> page(DictQuery query, PageQuery pageQuery) {
        // 构建查询条件
        QueryWrapper<SysDict> queryWrapper =  QueryWrapperUtil.build(query);
        // 分页查询
        Page<SysDict> page = new Page<>(pageQuery.getPage(), pageQuery.getSize());
        return baseMapper.selectPage(page, queryWrapper);
    }

    @Override
    public List<LabelValueResult<String>> listByDictType(String dictType) {
        return baseMapper.listByDictType(dictType);
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