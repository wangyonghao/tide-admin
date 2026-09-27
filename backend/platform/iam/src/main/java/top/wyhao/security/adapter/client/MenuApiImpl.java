package top.wyhao.security.adapter.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import top.wyhao.security.app.service.MenuService;
import top.wyhao.security.client.MenuApi;

import java.util.List;

/**
 * 菜单 API 实现
 */
@Service
@RequiredArgsConstructor
public class MenuApiImpl implements MenuApi {

    private final MenuService menuService;

    @Override
    public List<?> getMenuTreeByUserId(Long userId) {
        return menuService.getMenuTreeByUserId(userId);
    }
}
