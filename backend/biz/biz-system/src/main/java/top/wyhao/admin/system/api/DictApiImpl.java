
package top.wyhao.admin.system.api;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import top.wyhao.starter.web.excel.DictApi;
import top.wyhao.admin.system.service.DictService;
import top.wyhao.starter.web.core.model.resp.LabelValueResp;

import java.util.List;

/**
 * 字典业务 API 实现
 *
 * @author Charles7c
 * @since 2025/7/23 20:57
 */
@Service
@RequiredArgsConstructor
public class DictApiImpl implements DictApi {

    private final DictService dictService;

    @Override
    public List<LabelValueResp<String>> listByDictType(String dictType) {
        return dictService.listByDictType(dictType);
    }

    @Override
    public List<LabelValueResp> listAll() {
        return List.of(); // TODO(WYH) 待实现
    }
}
