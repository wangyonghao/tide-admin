package top.wyhao.identity.app.service;

import java.util.Collection;
import java.util.List;

/**
 * 成员关系服务（双写阶段：与 department_id / user_role 同步）。
 */
public interface MembershipService {

    /**
     * 替换用户主部门成员关系（恰好一条 DEPARTMENT + is_primary）。
     */
    void replacePrimaryDepartment(Long userId, Long departmentId);

    /**
     * 批量替换用户主部门（导入等场景）。
     */
    void replacePrimaryDepartments(List<long[]> userDepartmentPairs);

    /**
     * 替换用户全部角色成员关系。
     */
    void replaceRoles(Long userId, List<Long> roleIds);

    /**
     * 为角色追加成员（已存在则跳过）。
     */
    void addRoleMembers(Long roleId, Collection<Long> userIds);

    /**
     * 移除角色下指定成员。
     */
    void removeRoleMembers(Long roleId, Collection<Long> userIds);

    /**
     * 移除用户的全部角色成员关系。
     */
    void removeAllRolesByUserIds(Collection<Long> userIds);

    /**
     * 移除用户的全部成员关系（部门 + 角色）。
     */
    void removeAllByUserIds(Collection<Long> userIds);
}
