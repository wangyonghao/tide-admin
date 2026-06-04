
package top.wyhao.admin.system.model;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import top.wyhao.admin.system.model.enums.LogStatus;
import top.wyhao.cmn.db.query.QueryCondition;
import top.wyhao.cmn.db.query.QueryType;
import top.wyhao.starter.core.enums.StatusEnum;
import top.wyhao.starter.excel.converter.ExcelBaseEnumConverter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 日志响应参数 Record
 */

public class OperationLogModel {
    @Schema(description = "日志响应参数")
    public record Result(
            /**
             * ID
             */
            @Schema(description = "ID", example = "1")
            Long id,

            /**
             * 业务对象类型 如：user、sms、login、bug、task、project
             */
            @Schema(description = "业务对象类型", example = "user")
            String objectType,

            /**
             * 业务对象ID
             */
            @Schema(description = "业务对象ID", example = "1")
            Long objectId,

            /**
             * 操作类型 如：login、send、create、update、delete、logout
             */
            @Schema(description = "操作类型", example = "create")
            String operation,

            /**
             * 操作者ID
             */
            @Schema(description = "操作者ID", example = "1")
            Long operatorId,

            /**
             * 操作者名称
             */
            @Schema(description = "操作者名称", example = "张三")
            String operatorName,

            /**
             * 操作者IP
             */
            @Schema(description = "操作者IP", example = "192.168.1.1")
            String operatorIp,

            /**
             * 操作时间
             */
            @Schema(description = "操作时间", example = "2023-08-08 08:08:08", type = "string")
            LocalDateTime operateTime
    ) {
    }

    /**
     * 日志详情响应参数 Record
     */
    public record Detail(
            /**
             * ID
             */
            @Schema(description = "ID", example = "1")
            Long id,

            /**
             * 业务对象类型 如：user、sms、login、bug、task、project
             */
            @Schema(description = "业务对象类型", example = "user")
            String objectType,

            /**
             * 业务对象ID
             */
            @Schema(description = "业务对象ID", example = "1")
            Long objectId,

            /**
             * 操作类型 如：login、send、create、update、delete、logout
             */
            @Schema(description = "操作类型", example = "create")
            String operation,

            /**
             * 操作者ID
             */
            @Schema(description = "操作者ID", example = "1")
            Long operatorId,

            /**
             * 操作者名称
             */
            @Schema(description = "操作者名称", example = "张三")
            String operatorName,

            /**
             * 操作者IP
             */
            @Schema(description = "操作者IP", example = "192.168.1.1")
            String operatorIp,

            /**
             * 操作时间
             */
            @Schema(description = "操作时间", example = "2023-08-08 08:08:08", type = "string")
            LocalDateTime operateTime,

            /**
             * 状态 success / fail
             */
            @Schema(description = "状态", example = "success")
            String status,

            /**
             * 备注
             */
            @Schema(description = "备注", example = "操作成功")
            String remark,

            /**
             * 额外信息 JSON格式
             */
            @Schema(description = "额外信息", example = "{\"key\": \"value\"}")
            String extra
    ) {
    }

    /**
     * 日志查询条件
     */
    @Schema(description = "日志查询条件")
    public static record LogQuery(
            /**
             * 业务对象类型
             */
            @Schema(description = "业务对象类型", example = "业务对象类型")
            String objectType,

            /**
             * 业务对象ID
             */
            @Schema(description = "业务对象ID", example = "业务对象ID")
            String objectId,

            /**
             * IP
             */
            @Schema(description = "操作IP", example = "")
            String operatorIp,

            /**
             * 操作人
             */
            @Schema(description = "操作人", example = "admin")
            String operatorName,

            /**
             * 日志描述
             */
            @Schema(description = "日志描述", example = "新增数据")
            @QueryCondition(type = QueryType.LIKE)
            String operation,

            /**
             * 操作时间
             */
            @Schema(description = "操作时间", example = "2023-08-08 00:00:00,2023-08-08 23:59:59")
            @Size(max = 2, message = "操作时间必须是一个范围")
            @QueryCondition(type = QueryType.BETWEEN)
            List<LocalDateTime> createTime,

            /**
             * 状态
             */
            @Schema(description = "状态", example = "1")
            StatusEnum status,

            /**
             * 排序条件
             */
            @Schema(description = "排序条件", example = "createTime,desc")
            String[] sort
    ) {}

    /**
     * 操作日志导出响应参数
     */
    @ExcelIgnoreUnannotated
    @Schema(description = "操作日志导出响应参数")
    public record Excel(
            /**
             * ID
             */
            @Schema(description = "ID", example = "1")
            @ExcelProperty(value = "ID")
            Long id,

            /**
             * 操作时间
             */
            @Schema(description = "操作时间", example = "2023-08-08 08:08:08", type = "string")
            @ExcelProperty(value = "操作时间")
            LocalDateTime createTime,

            /**
             * 操作人
             */
            @Schema(description = "操作人", example = "张三")
            @ExcelProperty(value = "操作人")
            String createUserString,

            /**
             * 操作内容
             */
            @Schema(description = "操作内容", example = "账号登录")
            @ExcelProperty(value = "操作内容")
            String description,

            /**
             * 所属模块
             */
            @Schema(description = "所属模块", example = "部门管理")
            @ExcelProperty(value = "所属模块")
            String module,

            /**
             * 状态
             */
            @Schema(description = "状态", example = "1")
            @ExcelProperty(value = "状态", converter = ExcelBaseEnumConverter.class)
            LogStatus status,

            /**
             * 操作 IP
             */
            @Schema(description = "操作 IP", example = "")
            @ExcelProperty(value = "操作 IP")
            String ip,

            /**
             * 操作地点
             */
            @Schema(description = "操作地点", example = "中国北京北京市")
            @ExcelProperty(value = "操作地点")
            String address,

            /**
             * 耗时（ms）
             */
            @Schema(description = "耗时（ms）", example = "58")
            @ExcelProperty(value = "耗时（ms）")
            Long timeTaken,

            /**
             * 浏览器
             */
            @Schema(description = "浏览器", example = "Chrome 115.0.0.0")
            @ExcelProperty(value = "浏览器")
            String browser,

            /**
             * 终端系统
             */
            @Schema(description = "终端系统", example = "Windows 10")
            @ExcelProperty(value = "终端系统")
            String os
    ) {
    }
}
