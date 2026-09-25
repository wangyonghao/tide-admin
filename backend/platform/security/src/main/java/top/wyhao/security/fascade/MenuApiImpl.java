package top.wyhao.security.fascade;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import top.wyhao.security.service.MenuService;
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
