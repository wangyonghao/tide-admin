
package top.wyhao.admin.system.model.query;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;
import top.wyhao.starter.core.enums.StatusEnum;
import top.wyhao.cmn.db.query.Query;
import top.wyhao.cmn.db.query.QueryType;
import top.wyhao.starter.web.core.model.SortQuery;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 日志查询条件
 */
@Data
@Schema(description = "日志查询条件")
public class LogQuery extends SortQuery {
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
    @Query(type = QueryType.LIKE)
    private String operation;

    /**
     * 操作时间
     */
    @Schema(description = "操作时间", example = "2023-08-08 00:00:00,2023-08-08 23:59:59")
    @Size(max = 2, message = "操作时间必须是一个范围")
    @Query(type = QueryType.BETWEEN)
    private List<LocalDateTime> createTime;

    /**
     * 状态
     */
    @Schema(description = "状态", example = "1")
    private StatusEnum status;
}
