package top.wyhao.settings.exception;

import cn.hutool.core.util.StrUtil;
import top.wyhao.starter.core.exception.BizException;

/**
 * 字典业务异常
 */
public class DictException extends BizException {

    public DictException(String message) {
        super(message);
    }

    public DictException(String code, String message) {
        super(code, message);
    }

    public static DictException of(String message) {
        return new DictException(message);
    }

    public static DictException of(String code, String message) {
        return new DictException(code, message);
    }

    public static DictException notFound(Long id) {
        return of("DICT_NOT_FOUND", StrUtil.format("ID为 [{}] 的字典未找到", id));
    }

    public static DictException valueExist(String dictType, String value) {
        return of("DICT_VALUE_EXIST", StrUtil.format("字典类型 [{}] 中值为 [{}] 的字典已存在", dictType, value));
    }

    public static DictException deleteIdsEmpty() {
        return of("DICT_DELETE_IDS_EMPTY", "请选择要删除的数据");
    }
}
