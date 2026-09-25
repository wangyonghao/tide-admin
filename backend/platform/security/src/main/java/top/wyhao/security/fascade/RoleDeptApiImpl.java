package top.wyhao.security.fascade;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import top.wyhao.security.service.RoleDeptService;
import top.wyhao.security.client.RoleDeptApi;

import java.util.List;

/**
 * 角色-部门关联 API 实现
 */
@Service
@RequiredArgsConstructor
public class RoleDeptApiImpl implements RoleDeptApi {

    private final RoleDeptService roleDeptService;

    @Override
    public void deleteByDeptIds(List<Long> deptIds) {
        roleDeptService.deleteByDeptIds(deptIds);
    }
}
