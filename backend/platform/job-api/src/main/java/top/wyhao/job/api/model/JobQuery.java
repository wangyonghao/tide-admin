package top.wyhao.job.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import top.wyhao.starter.web.core.model.PageQuery;

@Data
@Schema(description = "任务查询")
public class JobQuery extends PageQuery {

    private String name;
    private String handlerCode;
    private Integer status;
}
