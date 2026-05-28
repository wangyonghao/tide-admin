
package top.wyhao.admin.system.model.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import top.wyhao.starter.core.enums.StatusEnum;
import top.wyhao.cmn.db.query.QueryCondition;
import top.wyhao.cmn.db.query.QueryType;

/**
 * 部门查询条件
 *

 * @since 2023/1/22 17:52
 */
@Data
@Schema(description = "部门查询条件")
public class DeptQuery {
    /**
     * 关键词
     */
    @Schema(description = "关键词", example = "测试部")
    @QueryCondition(columns = {"name", "code"}, type = QueryType.LIKE)
    private String keyword;

    /**
     * 状态
     */
    @Schema(description = "状态", example = "1")
    @QueryCondition(type = QueryType.EQ)
    private StatusEnum status;

    /**
     * 排序条件
     */
    @Schema(description = "排序条件", example = "createTime,desc")
    private String[] sort;
}
