
package top.wyhao.admin.auth.model;

import io.swagger.v3.oas.annotations.media.Schema;
import top.wyhao.admin.system.model.UserModel;
import top.wyhao.admin.system.model.result.MenuTreeVO;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 用户认证信息
 */
@Schema(description = "用户认证信息")
public record AuthInfoResult(
        /**
         * 用户信息
         */
        @Schema(description = "用户信息", example = "用户信息")
        UserModel.Detail user,

        /**
         * 角色编码集合
         */
        @Schema(description = "角色编码集合", example = "[\"test\"]")
        List<String> roles,

        /**
         * 权限码集合
         */
        @Schema(description = "权限码集合", example = "[\"system:user:list\",\"system:user:create\"]")
        List<String> permissions,

        /**
         * 用户菜单
         */
        @Schema(description = "用户菜单", example = "")
        List<MenuTreeVO> menus
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
