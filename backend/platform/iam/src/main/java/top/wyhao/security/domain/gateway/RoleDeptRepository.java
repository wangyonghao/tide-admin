package top.wyhao.security.domain.gateway;

import top.wyhao.security.domain.model.SysRoleDept;

import java.util.Collection;
import java.util.List;

/**
 * 角色与部门关联仓储。
 */
public interface RoleDeptRepository {

    List<Long> listDeptIdsByRoleId(Long roleId);

    void deleteByRoleId(Long roleId);

    void deleteByDeptIds(List<Long> deptIds);

    boolean insertBatch(List<SysRoleDept> roleDepts);

    void deleteAll();

    boolean deleteByRoleIdNotIn(Collection<Long> keepRoleIds);
}
