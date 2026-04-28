
package top.wyhao.admin.system.model.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import top.wyhao.starter.data.annotation.Query;
import top.wyhao.starter.data.enums.QueryType;

import java.io.Serial;
import java.io.Serializable;

/**
 * 字典查询条件
 *
 * @author Charles7c
 * @since 2023/9/11 21:29
 */
@Data
@Schema(description = "字典查询条件")
public class DictQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 关键词
     */
    @Schema(description = "关键词")
    @Query(columns = {"dict_type", "label", "value", "description"}, type = QueryType.LIKE)
    private String description;

    /**
     * 字典类型
     */
    @Schema(description = "字典类型", example = "notice_type")
    @Query(type = QueryType.EQ)
    private String dictType;

    /**
     * 是否启用
     */
    @Schema(description = "是否启用", example = "true")
    @Query(type = QueryType.EQ)
    private Boolean enabled;
}