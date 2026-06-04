
package top.wyhao.admin.system.model;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import top.wyhao.cmn.db.query.QueryCondition;
import top.wyhao.cmn.db.query.QueryType;
import top.wyhao.starter.core.enums.ResultStatusEnum;
import top.wyhao.starter.excel.converter.ExcelBaseEnumConverter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 短信日志创建或修改请求参数 Record
 */
public class SmsLogModel {
    public record Request(
            /**
             * 配置 ID
             */
            Long configId,

            /**
             * 手机号
             */
            String phone,

            /**
             * 参数配置
             */
            String params,

            /**
             * 发送状态
             */
            ResultStatusEnum status,

            /**
             * 返回数据
             */
            String resMsg
    ) implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;
    }

    @ExcelIgnoreUnannotated
    @Schema(description = "短信日志响应参数")
public record Result(
        @Schema(description = "ID", example = "1")
        @ExcelProperty(value = "ID", order = 1)
        Long id,

        @JsonIgnore
        Long createUser,

        @Schema(description = "创建人", example = "超级管理员")
        @ExcelProperty(value = "创建人", order = Integer.MAX_VALUE - 4)
        String createUserString,

        @Schema(description = "创建时间", example = "2023-08-08 08:08:08", type = "string")
        @ExcelProperty(value = "创建时间", order = Integer.MAX_VALUE - 3)
        LocalDateTime createTime,

        @Schema(description = "是否禁用修改", example = "true")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        Boolean disabled,

        @Schema(description = "配置 ID")
        @ExcelProperty(value = "配置 ID")
        Long configId,

        @Schema(description = "手机号", example = "18888888888")
        @ExcelProperty(value = "手机号")
        String phone,

        @Schema(description = "参数配置")
        @ExcelProperty(value = "参数配置")
        String params,

        @Schema(description = "发送状态", example = "1")
        @ExcelProperty(value = "发送状态", converter = ExcelBaseEnumConverter.class)
        ResultStatusEnum status,

        @Schema(description = "返回数据")
        @ExcelProperty(value = "返回数据")
        String resMsg
) {
}

    /**
     * 短信日志查询条件
     *


     * @since 2025/03/15 22:15
     */
    @Schema(description = "短信日志查询条件")
    public static record SmsLogQuery(
            /**
             * 配置 ID
             */
            @Schema(description = "配置 ID", example = "1")
            @QueryCondition(type = QueryType.EQ)
            Long configId,

            /**
             * 手机号
             */
            @Schema(description = "手机号", example = "18888888888")
            @QueryCondition(type = QueryType.EQ)
            String phone,

            /**
             * 发送状态
             */
            @Schema(description = "发送状态", example = "1")
            @QueryCondition(type = QueryType.EQ)
            ResultStatusEnum status,

            /**
             * 排序条件
             */
            @Schema(description = "排序条件", example = "createTime,desc")
            String[] sort
    ) {}
}