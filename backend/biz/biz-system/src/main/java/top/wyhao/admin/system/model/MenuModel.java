
package top.wyhao.admin.system.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.groups.Default;
import org.hibernate.validator.constraints.Length;
import top.wyhao.admin.system.model.enums.MenuType;
import top.wyhao.cmn.db.query.QueryCondition;
import top.wyhao.cmn.db.query.QueryType;
import top.wyhao.starter.core.enums.StatusEnum;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 菜单创建或修改请求参数 Record
 */
public class MenuModel {
    @Schema(description = "菜单创建或修改请求参数")
    public record Request(
            /**
             * ID
             */
            @NotNull(groups = Update.class)
            Long id,

            /**
             * 类型
             */
            @Schema(description = "类型", example = "2")
            @NotNull(message = "类型无效", groups = Create.class)
            MenuType type,

            /**
             * 图标
             */
            @Schema(description = "图标", example = "user")
            @Length(max = 50, message = "图标长度不能超过 {max} 个字符")
            String icon,

            /**
             * 排序
             */
            @Schema(description = "排序", example = "1")
            @NotNull(message = "排序不能为空")
            @Min(value = 0, message = "排序最小值为 {value}")
            Integer sort,

            /**
             * 权限标识
             */
            @Schema(description = "权限标识", example = "system:user:list")
            @Length(max = 100, message = "权限标识长度不能超过 {max} 个字符")
            String permission,

            /**
             * 路由地址
             */
            @Schema(description = "路由地址", example = "/system/user")
            @Length(max = 255, message = "路由地址长度不能超过 {max} 个字符")
            String path,

            /**
             * 组件名称
             */
            @Schema(description = "组件名称", example = "User")
            @Length(max = 50, message = "组件名称长度不能超过 {max} 个字符")
            String name,

            /**
             * 组件路径
             */
            @Schema(description = "组件路径", example = "/system/user/index")
            @Length(max = 255, message = "组件路径长度不能超过 {max} 个字符")
            String component,

            /**
             * 重定向地址
             */
            @Schema(description = "重定向地址")
            String redirect,

            /**
             * 是否外链
             */
            @Schema(description = "是否外链", example = "false")
            Boolean isExternal,

            /**
             * 是否缓存
             */
            @Schema(description = "是否缓存", example = "false")
            Boolean isCache,

            /**
             * 是否隐藏
             */
            @Schema(description = "是否隐藏", example = "false")
            Boolean isHidden,

            /**
             * 上级菜单 ID
             */
            @Schema(description = "上级菜单 ID", example = "1000")
            Long parentId,

            /**
             * 状态
             */
            @Schema(description = "状态", example = "1")
            @NotNull(message = "状态无效")
            StatusEnum status
    ) implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;
    }

    public interface Update extends Default {}
    public interface Create extends Default {}

    /**
     * 菜单查询条件
     *
     * @since 2023/2/15 20:21
     */
    @Schema(description = "菜单查询条件")
    public static record MenuQuery(
            /**
             * 标题
             */
            @Schema(description = "标题", example = "用户管理")
            @QueryCondition(type = QueryType.LIKE)
            String title,

            /**
             * 状态
             */
            @Schema(description = "状态", example = "1")
            @QueryCondition(type = QueryType.EQ)
            String status,

            /**
             * 排除的菜单 ID 列表
             */
            @Schema(hidden = true, description = "排除的菜单 ID 列表", example = "[9000]")
            @QueryCondition(columns = "id", type = QueryType.NOT_IN)
            List<Long> excludeMenuIdList,

            /**
             * 排序条件
             */
            @Schema(description = "排序条件", example = "createTime,desc")
            String[] sort
    ) {}
}
