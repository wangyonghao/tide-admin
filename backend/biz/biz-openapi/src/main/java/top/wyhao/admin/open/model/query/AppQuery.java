
package top.wyhao.admin.open.model.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import top.wyhao.cmn.db.query.QueryCondition;
import top.wyhao.cmn.db.query.QueryType;

/**
 * 应用查询条件


 * @since 2024/10/17 16:03
 */
@Data
@Schema(description = "应用查询条件")
public class AppQuery{

    /**
     * 关键词
     */
    @Schema(description = "关键词", example = "应用1")
    @QueryCondition(columns = {"name", "description"}, type = QueryType.LIKE)
    private String description;

    /**
     * 排序条件
     */
    @Schema(description = "排序条件", example = "createTime,desc")
    private String[] sort;
}