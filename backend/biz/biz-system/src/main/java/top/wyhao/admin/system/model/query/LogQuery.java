
package top.wyhao.admin.system.model.query;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;
import top.wyhao.starter.core.enums.StatusEnum;
import top.wyhao.cmn.db.query.QueryCondition;
import top.wyhao.cmn.db.query.QueryType;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 日志查询条件
 */
@Data
@Schema(description = "日志查询条件")
public class LogQuery {
    /**
     * 业务对象类型
     */
    @Schema(description = "业务对象类型", example = "业务对象类型")
    private String objectType;

    @Schema(description = "业务对象ID", example = "业务对象ID")
    private String objectId;
    /**
     * IP
     */
    @Schema(description = "操作IP", example = "")
    private String operatorIp;

    /**
     * 操作人
     */
    @Schema(description = "操作人", example = "admin")
    private String operatorName;

    /**
     * 日志描述
     */
    @Schema(description = "日志描述", example = "新增数据")
    @QueryCondition(type = QueryType.LIKE)
    private String operation;

    /**
     * 操作时间
     */
    @Schema(description = "操作时间", example = "2023-08-08 00:00:00,2023-08-08 23:59:59")
    @Size(max = 2, message = "操作时间必须是一个范围")
    @QueryCondition(type = QueryType.BETWEEN)
    private List<LocalDateTime> createTime;

    /**
     * 状态
     */
    @Schema(description = "状态", example = "1")
    private StatusEnum status;

    /**
     * 排序条件
     */
    @Schema(description = "排序条件", example = "createTime,desc")
    private String[] sort;
}
