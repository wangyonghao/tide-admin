package top.wyhao.organization.client;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 部门业务 API（组织域对外提供）
 */
public interface DeptApi {

    /**
     * 查询部门自身及全部下级 ID
     */
    List<Long> listSelfAndDescendantIds(Long deptId);

    /**
     * 校验部门路径是否均有效，返回有效数量
     */
    int countValidDeptPaths(Collection<String> deptPaths);

    /**
     * 将部门路径解析为 ID 映射
     */
    Map<String, Long> resolveDeptIdsByPaths(Collection<String> deptPaths);

    /**
     * 部门是否禁用
     */
    boolean isDisabled(Long deptId);
}
