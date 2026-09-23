package top.wyhao.admin.system.exception;

import cn.hutool.core.util.StrUtil;
import top.wyhao.starter.core.exception.BizException;

/**
 * 部门业务异常
 */
public class DeptException extends BizException {

    public DeptException(String message) {
        super(message);
    }

    public DeptException(String code, String message) {
        super(code, message);
    }

    public static DeptException of(String message) {
        return new DeptException(message);
    }

    public static DeptException of(String code, String message) {
        return new DeptException(code, message);
    }

    public static DeptException notFound(Long id) {
        return of("DEPT_NOT_FOUND", StrUtil.format("ID为 [{}] 的部门未找到", id));
    }

    public static DeptException nameExist(String name) {
        return of("DEPT_NAME_EXIST", StrUtil.format("名称为 [{}] 的部门已存在", name));
    }
}
