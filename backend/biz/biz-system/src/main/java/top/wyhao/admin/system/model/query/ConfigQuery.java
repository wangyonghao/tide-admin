
package top.wyhao.admin.system.model.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 系统配置查询条件
 *

 * @since 2024/04/26
 */
@Data
@Schema(description = "系统配置查询条件")
public class ConfigQuery {

    /**
     * 配置键（模糊查询）
     */
    @Schema(description = "配置键", example = "site")
    private String configKey;

    /**
     * 关键词（搜索配置键或描述）
     */
    @Schema(description = "关键词", example = "站点")
    private String searchWords;

    /**
     * 排序条件
     */
    @Schema(description = "排序条件", example = "createTime,desc")
    private String[] sort;
}
