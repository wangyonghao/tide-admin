
package top.wyhao.admin.tenant.model.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import top.wyhao.starter.core.enums.StatusEnum;
import top.wyhao.cmn.db.query.QueryCondition;
import top.wyhao.cmn.db.query.QueryType;

import java.io.Serial;
import java.io.Serializable;

/**
 * 套餐查询条件
 *


 * @since 2024/11/26 11:25
 */
@Data
@Schema(description = "套餐查询条件")
public class PackageQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 关键词
     */
    @Schema(description = "关键词", example = "初级套餐")
    @QueryCondition(columns = {"name", "description"}, type = QueryType.LIKE)
    private String description;

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