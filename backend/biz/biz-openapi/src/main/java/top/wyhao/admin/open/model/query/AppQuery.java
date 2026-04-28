
package top.wyhao.admin.open.model.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import top.wyhao.starter.data.annotation.Query;
import top.wyhao.starter.data.enums.QueryType;
import top.wyhao.starter.web.core.model.SortQuery;

import java.io.Serial;
import java.io.Serializable;

/**
 * 应用查询条件
 *
 * @author chengzi
 * @author Charles7c
 * @since 2024/10/17 16:03
 */
@Data
@Schema(description = "应用查询条件")
public class AppQuery extends SortQuery {

    /**
     * 关键词
     */
    @Schema(description = "关键词", example = "应用1")
    @Query(columns = {"name", "description"}, type = QueryType.LIKE)
    private String description;
}