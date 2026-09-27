package top.wyhao.job.adapter.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import top.wyhao.starter.web.core.model.PageQuery;

@Data
@Schema(description = "任务日志查询")
public class JobLogQuery extends PageQuery {

    private String jobId;
    private String handlerCode;
    private Integer status;
}
