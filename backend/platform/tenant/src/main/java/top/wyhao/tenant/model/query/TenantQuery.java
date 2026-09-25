
package top.wyhao.tenant.model.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import top.wyhao.cmn.db.query.Query;

import java.io.Serial;
import java.io.Serializable;

/**
 * 租户查询条件
 *


 * @since 2024/11/26 17:20
 */
@Data
@Schema(description = "租户查询条件")
public class TenantQuery{
    /**
     * 关键词
     */
    @Schema(description = "关键词", example = "Xxx租户")
    @Query(field = "name,description", type = Query.Type.LIKE)
    private String description;

    /**
     * 编码
     */
    @Schema(description = "编码", example = "T0stxiJK6RMH")
    @Query(type = Query.Type.EQ)
    private String code;

    /**
     * 域名
     */
    @Schema(description = "域名", example = "admin.wyhao.top")
    @Query(type = Query.Type.LIKE)
    private String domain;

    /**
     * 套餐 ID
     */
    @Schema(description = "套餐 ID", example = "1")
    @Query(type = Query.Type.EQ)
    private Long packageId;
}