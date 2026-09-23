package top.wyhao.admin.tenant.exception;

import cn.hutool.core.util.StrUtil;
import top.wyhao.starter.core.exception.BizException;

/**
 * 套餐业务异常
 */
public class PackageException extends BizException {

    public PackageException(String message) {
        super(message);
    }

    public PackageException(String code, String message) {
        super(code, message);
    }

    public static PackageException of(String message) {
        return new PackageException(message);
    }

    public static PackageException of(String code, String message) {
        return new PackageException(code, message);
    }

    public static PackageException nameExists(String name) {
        return of("PACKAGE_NAME_EXISTS", StrUtil.format("名称为 [{}] 的套餐已存在", name));
    }
}
