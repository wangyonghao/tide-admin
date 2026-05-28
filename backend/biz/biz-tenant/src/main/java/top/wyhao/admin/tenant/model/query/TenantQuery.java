
package top.wyhao.admin.tenant.model.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import top.wyhao.cmn.db.query.QueryCondition;
import top.wyhao.cmn.db.query.QueryType;

import java.io.Serial;
import java.io.Serializable;

/**
 * 租户查询条件
 *


 * @since 2024/11/26 17:20
 */
@Data
@Schema(description = "租户查询条件")
public class TenantQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 关键词
     */
    @Schema(description = "关键词", example = "Xxx租户")
    @QueryCondition(columns = {"name", "description"}, type = QueryType.LIKE)
    private String description;

    /**
     * 编码
     */
    @Schema(description = "编码", example = "T0stxiJK6RMH")
    @QueryCondition(type = QueryType.EQ)
    private String code;

    /**
     * 域名
     */
    @Schema(description = "域名", example = "admin.wyhao.top")
    @QueryCondition(type = QueryType.LIKE)
    private String domain;

    /**
     * 套餐 ID
     */
    @Schema(description = "套餐 ID", example = "1")
    @QueryCondition(type = QueryType.EQ)
    private Long packageId;
}