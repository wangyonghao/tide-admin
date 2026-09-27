package top.wyhao.security.infrastructure.persistence;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import top.wyhao.cmn.core.util.CollUtils;
import top.wyhao.security.domain.gateway.RoleDeptRepository;
import top.wyhao.security.domain.model.SysRoleDept;
import top.wyhao.security.infrastructure.persistence.mapper.SysRoleDeptMapper;

import java.util.Collection;
import java.util.List;

/**
 * 角色与部门关联仓储实现。
 */
@Repository
@RequiredArgsConstructor
public class RoleDeptRepositoryImpl implements RoleDeptRepository {

    private final SysRoleDeptMapper roleDeptMapper;

    @Override
    public List<Long> listDeptIdsByRoleId(Long roleId) {
        return roleDeptMapper.selectDeptIdByRoleId(roleId);
    }

    @Override
    public void deleteByRoleId(Long roleId) {
        roleDeptMapper.lambdaUpdate().eq(SysRoleDept::getRoleId, roleId).remove();
    }

    @Override
    public void deleteByDeptIds(List<Long> deptIds) {
        if (CollUtil.isEmpty(deptIds)) {
            return;
        }
        roleDeptMapper.lambdaUpdate().in(SysRoleDept::getDeptId, deptIds).remove();
    }

    @Override
    public boolean insertBatch(List<SysRoleDept> roleDepts) {
        return roleDeptMapper.insertBatch(roleDepts);
    }

    @Override
    public boolean replaceByRoleId(Long roleId, List<Long> deptIds) {
        List<Long> ids = CollUtil.emptyIfNull(deptIds);
        List<Long> oldIds = listDeptIdsByRoleId(roleId);
        if (CollUtil.isEmpty(CollUtil.disjunction(ids, oldIds))) {
            return false;
        }
        deleteByRoleId(roleId);
        if (CollUtil.isEmpty(ids)) {
            return true;
        }
        List<SysRoleDept> roleDepts = CollUtils.mapToList(ids, deptId -> new SysRoleDept(roleId, deptId));
        return insertBatch(roleDepts);
    }

    @Override
    public void deleteAll() {
        roleDeptMapper.delete(Wrappers.<SysRoleDept>query().eq("1", 1));
    }

    @Override
    public boolean deleteByRoleIdNotIn(Collection<Long> keepRoleIds) {
        return roleDeptMapper.lambdaUpdate().notIn(SysRoleDept::getRoleId, keepRoleIds).remove();
    }
}
