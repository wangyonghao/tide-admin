
package top.wyhao.admin.system.model;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import top.wyhao.cmn.db.query.QueryCondition;
import top.wyhao.cmn.db.query.QueryType;
import top.wyhao.starter.core.enums.StatusEnum;
import top.wyhao.starter.core.validation.EnumValue;

import java.util.List;

/**
 * 部门创建或修改请求参数 Record
 */
public class DeptModel {
    @Schema(description = "部门创建或修改请求参数")
    public record Request(
            @Schema(description = "编码", example = "A001")
            @NotBlank(message = "编码不能为空")
            @Length(max = 30, message = "编码长度不能超过 {max} 个字符")
            String code,

            @Schema(description = "名称", example = "测试部")
            @NotBlank(message = "名称不能为空")
            @Length(max = 50, message = "名称长度不能超过 {max} 个字符")
            String name,

            @NotNull(message = "部门类型不能为空")
            Integer type,

            @Schema(description = "上级部门 ID", example = "2")
            @NotNull(message = "上级部门不能为空")
            Long parentId,

            @Schema(description = "排序", example = "1")
            @Min(value = 1, message = "排序最小值为 {value}")
            Integer sort,

            @Schema(description = "描述", example = "测试部描述信息")
            @Length(max = 200, message = "描述长度不能超过 {max} 个字符")
            String description,

            @Schema(description = "状态", example = "1")
            @EnumValue(enumValues = {"1", "2"}, message = "状态值必须是 1 或 2")
            Integer status
    ) {}

    @ExcelIgnoreUnannotated
    @Schema(description = "部门响应参数")
    public record Result(
            @Schema(description = "ID", example = "1")
            @ExcelProperty(value = "ID", order = 1)
            Long id,

            @Schema(description = "名称", example = "测试部")
            @ExcelProperty(value = "名称", order = 2)
            String name,

            @Schema(description = "编码", example = "A01")
            @ExcelProperty(value = "编码", order = 3)
            String code,

            @Schema(description = "类型", example = "1-分公司，2-部门，3-用户组")
            @ExcelProperty(value = "类型(1-分公司，2-部门，3-用户组)", order = 4)
            String type,

            @Schema(description = "上级部门 ID", example = "2")
            @ExcelProperty(value = "上级部门 ID", order = 4)
            Long parentId,

            @Schema(description = "排序", example = "1")
            @ExcelProperty(value = "排序", order = 4)
            Integer sort,

            @Schema(description = "是否为系统内置数据", example = "false")
            @ExcelProperty(value = "系统内置", order = 6)
            Boolean isBuiltin,

            @Schema(description = "描述", example = "测试部描述信息")
            @ExcelProperty(value = "描述", order = 7)
            String description,

            @Schema(description = "状态", example = "1")
            @ExcelProperty(value = "状态", order = 5)
            Integer status,

            @Schema(description = "是否禁用修改", example = "true")
            @JsonInclude(JsonInclude.Include.NON_NULL)
            Boolean disabled,

            @Schema(description = "下级部门", example = "true")
            List<DeptModel.Result> children
    ) {}

    @Schema(description = "部门查询条件")
    public record Query(
            @Schema(description = "关键词", example = "测试部")
            @QueryCondition(columns = {"name", "code"}, type = QueryType.LIKE)
            String keyword,

            @Schema(description = "状态", example = "1")
            @QueryCondition(type = QueryType.EQ)
            StatusEnum status,

            @Schema(description = "排序条件", example = "createTime,desc")
            String[] sort
    ) {}
}
