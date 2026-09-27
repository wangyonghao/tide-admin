package top.wyhao.security.app.service.impl;

import cn.hutool.core.collection.CollUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.wyhao.cmn.core.util.CollUtils;
import top.wyhao.security.app.service.RoleDeptService;
import top.wyhao.security.domain.gateway.RoleDeptRepository;
import top.wyhao.security.domain.model.SysRoleDept;

import java.util.List;

/**
 * 角色和部门关联业务实现
 */
@Service
@RequiredArgsConstructor
public class RoleDeptServiceImpl implements RoleDeptService {

    private final RoleDeptRepository roleDeptRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean add(List<Long> deptIds, Long roleId) {
        List<Long> oldDeptIdList = roleDeptRepository.listDeptIdsByRoleId(roleId);
        if (CollUtil.isEmpty(CollUtil.disjunction(deptIds, oldDeptIdList))) {
            return false;
        }
        roleDeptRepository.deleteByRoleId(roleId);
        List<SysRoleDept> roleDeptList = CollUtils.mapToList(deptIds, deptId -> new SysRoleDept(roleId, deptId));
        return roleDeptRepository.insertBatch(roleDeptList);
    }

    @Override
    public void deleteByRoleId(Long roleId) {
        roleDeptRepository.deleteByRoleId(roleId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByDeptIds(List<Long> deptIds) {
        if (CollUtil.isEmpty(deptIds)) {
            return;
        }
        roleDeptRepository.deleteByDeptIds(deptIds);
    }

    @Override
    public List<Long> listDeptIdByRoleId(Long roleId) {
        return roleDeptRepository.listDeptIdsByRoleId(roleId);
    }
}
