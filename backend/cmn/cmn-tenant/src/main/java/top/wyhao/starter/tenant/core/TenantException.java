package top.wyhao.starter.tenant.core;


import top.wyhao.starter.core.exception.BusinessException;

import java.io.Serial;

/**
 * 租户化的实体基类
 * @author wyhao
 */
public class TenantException extends BusinessException {

    @Serial
    private static final long serialVersionUID = 1L;

    public TenantException(String code, Object... args) {
        super("tenant-" +code, args);
    }
}
