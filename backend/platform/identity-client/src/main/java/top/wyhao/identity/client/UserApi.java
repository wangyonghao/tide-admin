package top.wyhao.identity.client;

import java.util.Collection;
import java.util.List;

/**
 * 用户身份 API（身份域对外提供）
 */
public interface UserApi {

    /**
     * 统计部门下的用户数
     */
    long countByDeptIds(Collection<Long> deptIds);

    /**
     * 查询用户角色码
     */
    List<String> findUserRoles(Long userId);
}
