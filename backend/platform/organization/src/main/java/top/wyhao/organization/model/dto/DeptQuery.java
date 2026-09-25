package top.wyhao.organization.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import top.wyhao.cmn.db.query.Query;
import top.wyhao.cmn.core.enums.StatusEnum;

/**
 * 部门查询条件
 */
@Data
@Schema(description = "部门查询条件")
public class DeptQuery {

    @Schema(description = "关键词", example = "测试部")
    @Query(field = "name,code", type = Query.Type.LIKE)
    private String keyword;

    @Schema(description = "状态", example = "1")
    @Query(type = Query.Type.EQ)
    private StatusEnum status;

    @Schema(description = "排序条件", example = "createTime,desc")
    private String[] sort;
}
