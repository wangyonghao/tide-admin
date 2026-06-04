package top.wyhao.admin.system.exception;

import cn.hutool.core.util.StrUtil;

public class DictException {

    public static DeptException notFound(Long id) {
        return new DeptException("DICT_NOT_FOUND", StrUtil.format("ID为 [{}] 的字典未找到", id));
    }

    public static DeptException valueExist(String dictType, String value) {
        return new DeptException("DICT_VALUE_EXIST", StrUtil.format("字典类型 [{}] 中值为 [{}] 的字典已存在", dictType, value));
    }
}
