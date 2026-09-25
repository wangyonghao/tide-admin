package top.wyhao.security.provider;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import top.wyhao.security.service.MenuService;
import top.wyhao.starter.core.spi.MenuApi;

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
