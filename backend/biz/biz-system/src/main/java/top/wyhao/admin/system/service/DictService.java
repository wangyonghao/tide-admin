
package top.wyhao.admin.system.service;

import top.wyhao.admin.system.model.entity.DictDO;
import top.wyhao.admin.system.model.query.DictQuery;
import top.wyhao.admin.system.model.vo.DictResult;
import top.wyhao.starter.data.service.IService;
import top.wyhao.starter.web.core.model.PageQuery;
import top.wyhao.starter.web.core.model.resp.LabelValueResp;
import top.wyhao.starter.web.core.model.PageResult;

import java.util.List;

/**
 * 字典业务接口
 *
 * @author Charles7c
 * @since 2023/9/11 21:29
 */
public interface DictService extends IService<DictDO> {

    PageResult<DictResult> page(DictQuery query, PageQuery pageQuery);

    /**
     * 根据字典类型查询字典列表
     *
     * @param dictType 字典类型
     * @return 字典列表
     */
    List<LabelValueResp<String>> listByDictType(String dictType);

    /**
     * 查询枚举字典
     *
     * @return 枚举字典列表
     */
    List<LabelValueResp<String>> listEnumDict();
}