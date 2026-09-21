package top.wyhao.job.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import top.wyhao.cmn.db.model.BaseEntity;

/**
 * 定时任务定义
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_job")
public class SysJob extends BaseEntity {

    private String name;
    private String handlerCode;
    private String cron;
    private String scheduleMode;
    private String schedulePayload;
    private String scheduleLabel;
    private String params;
    /** 0 停止 1 激活 */
    private Integer status;
    private String remark;
}
