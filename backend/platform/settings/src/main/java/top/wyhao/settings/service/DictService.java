
package top.wyhao.settings.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import top.wyhao.cmn.db.query.PageParam;
import top.wyhao.settings.entity.SysDict;
import top.wyhao.settings.model.dto.DictQuery;
import top.wyhao.starter.web.core.model.LabelValueResult;

import java.util.List;

/**
 * 字典业务接口
 *

 * @since 2023/9/11 21:29
 */
public interface DictService{

    IPage<SysDict> page(DictQuery query, PageParam pageParam);

    /**
     * 根据字典类型查询字典列表
     *
     * @param dictType 字典类型
     * @return 字典列表
     */
    List<LabelValueResult<String>> listByDictType(String dictType);

    /**
     * 查询枚举字典
     *
     * @return 枚举字典列表
     */
    List<LabelValueResult<String>> listEnumDict();
}