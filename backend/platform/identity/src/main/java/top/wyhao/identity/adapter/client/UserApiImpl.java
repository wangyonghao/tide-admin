package top.wyhao.identity.adapter.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import top.wyhao.identity.app.service.UserService;
import top.wyhao.identity.client.UserApi;

import java.util.Collection;
import java.util.List;

/**
 * 用户身份 API 实现
 */
@Service
@RequiredArgsConstructor
public class UserApiImpl implements UserApi {

    private final UserService userService;

    @Override
    public long countByDeptIds(Collection<Long> deptIds) {
        return userService.countByDeptIds(List.copyOf(deptIds));
    }

    @Override
    public List<String> findUserRoles(Long userId) {
        return userService.findUserRoles(userId);
    }
}
