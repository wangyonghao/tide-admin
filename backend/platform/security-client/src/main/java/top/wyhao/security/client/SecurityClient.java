package top.wyhao.security.client;

import java.util.List;

/**
 * 授权域客户端
 * <p>
 * 由 security 模块实现。identity 等模块只依赖本接口，避免与 security 循环依赖。
 * </p>
 */
public interface SecurityClient {

    /**
     * 查询用户权限码
     *
     * @param userId 用户 ID
     * @return 权限码列表
     */
    List<String> listPermissionsByUserId(Long userId);
}
