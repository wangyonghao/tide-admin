
package top.wyhao.admin.system.model.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import top.wyhao.starter.core.enums.ResultStatusEnum;
import top.wyhao.cmn.db.query.QueryCondition;
import top.wyhao.cmn.db.query.QueryType;

/**
 * 短信日志查询条件
 *


 * @since 2025/03/15 22:15
 */
@Data
@Schema(description = "短信日志查询条件")
public class SmsLogQuery {
    /**
     * 配置 ID
     */
    @Schema(description = "配置 ID", example = "1")
    @QueryCondition(type = QueryType.EQ)
    private Long configId;

    /**
     * 手机号
     */
    @Schema(description = "手机号", example = "18888888888")
    @QueryCondition(type = QueryType.EQ)
    private String phone;

    /**
     * 发送状态
     */
    @Schema(description = "发送状态", example = "1")
    @QueryCondition(type = QueryType.EQ)
    private ResultStatusEnum status;

    /**
     * 排序条件
     */
    @Schema(description = "排序条件", example = "createTime,desc")
    private String[] sort;
}