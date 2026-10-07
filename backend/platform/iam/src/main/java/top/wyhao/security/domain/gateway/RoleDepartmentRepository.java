package top.wyhao.security.domain.gateway;

import top.wyhao.security.domain.model.SysRoleDepartment;

import java.util.Collection;
import java.util.List;

/**
 * 角色与部门关联仓储。
 */
public interface RoleDepartmentRepository {

    List<Long> listDepartmentIdsByRoleId(Long roleId);

    void deleteByRoleId(Long roleId);

    void deleteByDepartmentIds(List<Long> departmentIds);

    boolean insertBatch(List<SysRoleDepartment> roleDepartments);

    /**
     * 覆盖式保存角色部门关联；无变更返回 false。
     */
    boolean replaceByRoleId(Long roleId, List<Long> departmentIds);

    void deleteAll();

    boolean deleteByRoleIdNotIn(Collection<Long> keepRoleIds);
}
