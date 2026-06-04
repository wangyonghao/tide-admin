
package top.wyhao.admin.system.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import top.wyhao.admin.system.exception.DictException;
import top.wyhao.admin.system.model.DictModel;
import top.wyhao.admin.system.entity.SysDict;
import top.wyhao.admin.system.service.DictService;
import top.wyhao.starter.cache.redisson.util.RedisUtils;
import top.wyhao.starter.core.constant.CacheConstants;
import top.wyhao.starter.core.model.Result;
import top.wyhao.starter.core.util.validation.Check;
import top.wyhao.starter.web.core.model.LabelValueResult;
import top.wyhao.starter.web.core.model.PageQuery;
import top.wyhao.starter.web.core.model.PageResult;

import java.util.List;

/**
 * 字典管理 API
 *
 * @since 2023/9/11 21:29
 */
@Tag(name = "字典管理 API")
@RestController
@RequiredArgsConstructor
public class DictController {

    private final DictService dictService;

    @Operation(summary = "分页查询列表", description = "分页查询列表")
    @SaCheckPermission("system:dict:page")
    @GetMapping("/system/dict/page")
    public PageResult<DictModel.Result> page(DictModel.Query query, PageQuery pageQuery) {
        IPage<SysDict> dictPage = dictService.page(query, pageQuery);

        // 转换为响应对象
        IPage<DictModel.Result> respPage = dictPage.convert(dict ->
                new DictModel.Result(
                        dict.getId(),
                        dict.getDictType(),
                        dict.getValue(),
                        dict.getLabel(),
                        dict.getExt(),
                        dict.getSort(),
                        dict.getEnabled(),
                        dict.getDescription()
                )
        );
        return PageResult.build(respPage);
    }

    @Operation(summary = "新增", description = "新增")
    @SaCheckPermission("system:dict:create")
    @PostMapping("/system/dict")
    public Result<Void> create(@Valid @RequestBody DictModel.Request req) {
        // 检查字典类型+值是否重复
        boolean isExists = dictService.lambdaQuery()
                .eq(SysDict::getDictType, req.dictType())
                .eq(SysDict::getValue, req.value())
                .exists();
        Check.when(isExists, "字典类型 [{}] 中值为 [{}] 的字典已存在", req.dictType(), req.value());

        SysDict dict = new SysDict();
        dict.setDictType(req.dictType());
        dict.setValue(req.value());
        dict.setLabel(req.label());
        dict.setExt(req.extra());
        dict.setSort(req.sort() != null ? req.sort() : 0);
        dict.setEnabled(req.enabled() != null ? req.enabled() : true);
        dict.setDescription(req.description());
        dictService.save(dict);
        return Result.ok();
    }

    @Operation(summary = "修改", description = "修改")
    @SaCheckPermission("system:dict:update")
    @PutMapping("/system/dict/{id}")
    public void update(@Valid @RequestBody DictModel.Request req, @Parameter(description = "ID", example = "1") @PathVariable Long id) {
        // 检查字典类型+值是否重复
        boolean valueExist = dictService.lambdaQuery()
                .eq(SysDict::getDictType, req.dictType())
                .eq(SysDict::getValue, req.value())
                .ne(SysDict::getId, id)
                .exists();

        Check.when(valueExist, DictException.valueExist(req.dictType(), req.value()));

        SysDict dict = new SysDict();
        dict.setId(id);
        dict.setDictType(req.dictType());
        dict.setValue(req.value());
        dict.setLabel(req.label());
        dict.setExt(req.extra());
        dict.setSort(req.sort());
        dict.setEnabled(req.enabled());
        dict.setDescription(req.description());
        dictService.updateById(dict);

        // 清除缓存
        RedisUtils.deleteByPattern(CacheConstants.DICT_KEY_PREFIX + req.dictType());
    }

    @Operation(summary = "批量删除", description = "批量删除")
    @SaCheckPermission("system:dict:delete")
    @DeleteMapping("/system/dict")
    public void delete(@RequestBody List<Long> ids) {
        Check.when(ids.isEmpty(), "请选择要删除的数据");

        // 获取需要清除缓存的字典类型
        List<String> dictTypes = dictService.lambdaQuery()
                .select(SysDict::getDictType)
                .in(SysDict::getId, ids)
                .groupBy(SysDict::getDictType)
                .list()
                .stream()
                .map(SysDict::getDictType)
                .toList();

        dictService.removeByIds(ids);

        // 清除缓存
        dictTypes.forEach(type -> RedisUtils.deleteByPattern(CacheConstants.DICT_KEY_PREFIX + type));
    }

    @Operation(summary = "清除缓存", description = "清除缓存")
    @SaCheckPermission("system:dict:clearCache")
    @DeleteMapping("/system/dict/cache/{dictType}")
    public void clearCache(@Parameter(description = "字典类型", example = "notice_type") @PathVariable String dictType) {
        RedisUtils.deleteByPattern(CacheConstants.DICT_KEY_PREFIX + dictType);
    }

    @Operation(summary = "查询字典", description = "查询字典列表")
    @Parameter(name = "dictType", description = "字典类型", example = "notice_type", in = ParameterIn.PATH)
    @GetMapping("/system/dict/{dictType}")
    public List<LabelValueResult<String>> listDict(@PathVariable String dictType) {
        return dictService.listByDictType(dictType);
    }
}