package top.wyhao.security.adapter.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import top.wyhao.security.client.RoleDeptApi;
import top.wyhao.security.domain.gateway.RoleDeptRepository;

import java.util.List;

/**
 * 角色-部门关联 API 实现
 */
@Service
@RequiredArgsConstructor
public class RoleDeptApiImpl implements RoleDeptApi {

    private final RoleDeptRepository roleDeptRepository;

    @Override
    public void deleteByDeptIds(List<Long> deptIds) {
        roleDeptRepository.deleteByDeptIds(deptIds);
    }
}
