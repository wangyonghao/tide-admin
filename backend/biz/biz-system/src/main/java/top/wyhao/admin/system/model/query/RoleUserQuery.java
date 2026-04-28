
package top.wyhao.admin.system.model.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import top.wyhao.starter.web.core.model.PageQuery;

/**
 * 角色关联用户查询条件
 */
@Data
@Schema(description = "角色成员查询条件")
public class RoleUserQuery extends PageQuery {
    /**
     * 角色 ID
     */
    @Schema(description = "角色 ID", example = "1")
    private Long roleId;

    /**
     * 关键词(用户名/昵称）
     */
    @Schema(description = "关键词", example = "zhangsan")
    private String keyword;
}
