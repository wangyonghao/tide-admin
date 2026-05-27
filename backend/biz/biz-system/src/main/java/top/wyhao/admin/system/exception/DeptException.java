package top.wyhao.admin.system.exception;

import cn.hutool.core.util.StrUtil;
import top.wyhao.starter.core.exception.BusinessException;

public class DeptException extends BusinessException {
    public DeptException(String message) {
        super(message);
    }

    public DeptException(String code, String message) {
        super(code, message);
    }

    public static DeptException notFound(Long id) {
        return new DeptException("DEPT_NOT_FOUND", StrUtil.format("ID为 [{}] 的部门未找到", id));
    }

    public static DeptException nameExist(String name) {
        return new DeptException("DEPT_NAME_EXIST", StrUtil.format("名称为 [{}] 的部门已存在", name));
    }
}
