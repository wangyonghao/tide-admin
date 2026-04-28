
package top.wyhao.admin.auth.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import top.wyhao.admin.system.model.vo.MenuTreeVO;
import top.wyhao.admin.system.model.vo.user.UserDetailResult;

import java.util.List;

/**
 * 登录用户响应参数
 *
 * @author Charles7c
 * @since 2022/12/29 20:15
 */
@Data
@Schema(description = "登录用户响应参数")
public class AuthInfo {
    /**
     * 用户信息
     */
    @Schema(description = "用户信息", example = "用户信息")
    private UserDetailResult user;
    /**
     * 角色编码集合
     */
    @Schema(description = "角色编码集合", example = "[\"test\"]")
    private List<String> roles;
    /**
     * 权限码集合
     */
    @Schema(description = "权限码集合", example = "[\"system:user:list\",\"system:user:create\"]")
    private List<String> permissions;

    @Schema(description = "用户菜单", example = "")
    private List<MenuTreeVO> menus;
}
