package top.wyhao.admin.system.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import top.wyhao.cmn.db.query.Query;
import top.wyhao.cmn.db.query.SortableQuery;

/**
 * 字典查询条件
 *
 * @since 2026/5/13
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "字典查询条件")
public class DictQuery extends SortableQuery {

    @Schema(description = "关键词")
    @Query(field = "dict_type,label,value,description", type = Query.Type.LIKE)
    private String keyword;
}
