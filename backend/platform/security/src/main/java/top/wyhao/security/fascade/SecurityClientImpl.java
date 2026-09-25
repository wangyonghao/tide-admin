package top.wyhao.security.fascade;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import top.wyhao.security.client.SecurityClient;
import top.wyhao.security.mapper.SysMenuMapper;

import java.util.List;

/**
 * 授权域客户端实现
 */
@Service
@RequiredArgsConstructor
public class SecurityClientImpl implements SecurityClient {

    private final SysMenuMapper menuMapper;

    @Override
    public List<String> listPermissionsByUserId(Long userId) {
        return menuMapper.selectPermissionByUserId(userId);
    }
}
