package top.wyhao.security.client;

import java.util.List;

/**
 * 角色-部门关联 API（授权域对外提供，供组织域删除部门时清理）
 */
public interface RoleDeptApi {

    /**
     * 根据部门 ID 删除关联
     */
    void deleteByDeptIds(List<Long> deptIds);
}
