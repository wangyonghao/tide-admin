package top.wyhao.admin.system.exception;

import cn.hutool.core.util.StrUtil;
import top.wyhao.starter.core.exception.BusinessException;

/**
 * 菜单 业务异常
 */
public class MenuException extends BusinessException {
    public MenuException(String message) {
        super(message);
    }

    public MenuException(String code, String message) {
        super(code, message);
    }

    public static MenuException of(String message) {
        return new MenuException(message);
    }

    public static MenuException of(String code, String message) {
        return new MenuException(code, message);
    }

    public static MenuException titleExist(String name) {
        return new MenuException("TITLE_EXIST",StrUtil.format("标题为 [{}] 的菜单已存在", name));
    }

    public static void notFound() {
        throw new MenuException("MENU_NOT_FOUND");
    }
}
