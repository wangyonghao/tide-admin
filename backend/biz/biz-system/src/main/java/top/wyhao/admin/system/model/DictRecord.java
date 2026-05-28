package top.wyhao.admin.system.model;

import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;
import top.wyhao.cmn.db.query.QueryCondition;
import top.wyhao.cmn.db.query.QueryType;
import top.wyhao.starter.core.constant.RegexConstants;

import java.util.Map;

/**
 * 字典相关的 Record 类集合
 *
 * @since 2026/5/13
 */
public class DictRecord {

    /**
     * 字典查询条件
     */
    @Schema(description = "字典查询条件")
    public record Query(
            @Schema(description = "关键词")
            @QueryCondition(columns = {"dict_type", "label", "value", "description"}, type = QueryType.LIKE)
            String keyword
    ) {}

    /**
     * 字典创建或修改请求参数
     */
    @Schema(description = "字典创建或修改请求参数")
    public record Request(
            @Schema(description = "字典类型", example = "notice_type")
            @NotBlank(message = "字典类型不能为空")
            @Pattern(regexp = RegexConstants.GENERAL_CODE, message = "字典类型长度为 2-30 个字符，支持大小写字母、数字、下划线，以字母开头")
            String dictType,

            @Schema(description = "字典值", example = "1")
            @NotBlank(message = "字典值不能为空")
            @Length(max = 255, message = "字典值长度不能超过 {max} 个字符")
            String value,

            @Schema(description = "字典标签", example = "产品新闻")
            @NotBlank(message = "字典标签不能为空")
            @Length(max = 255, message = "字典标签长度不能超过 {max} 个字符")
            String label,

            @Schema(description = "扩展信息", example = "{\"color\": \"primary\"}")
            Map<String, Object> extra,

            @Schema(description = "排序", example = "1")
            Integer sort,

            @Schema(description = "是否启用", example = "true")
            Boolean enabled,

            @Schema(description = "描述", example = "公告类型描述信息")
            @Length(max = 500, message = "描述长度不能超过 {max} 个字符")
            String description
    ) {}

    /**
     * 字典响应参数
     */
    @Schema(description = "字典响应参数")
    public record Result(
            @Schema(description = "ID", example = "1")
            @ExcelProperty(value = "ID")
            Long id,

            @Schema(description = "字典类型", example = "notice_type")
            @ExcelProperty(value = "字典类型")
            String dictType,

            @Schema(description = "字典值", example = "1")
            @ExcelProperty(value = "字典值")
            String value,

            @Schema(description = "字典标签", example = "产品新闻")
            @ExcelProperty(value = "字典标签")
            String label,

            @Schema(description = "扩展信息")
            Map<String, Object> ext,

            @Schema(description = "排序", example = "1")
            @ExcelProperty(value = "排序")
            Integer sort,

            @Schema(description = "是否启用", example = "true")
            @ExcelProperty(value = "是否启用")
            Boolean enabled,

            @Schema(description = "描述")
            @ExcelProperty(value = "描述")
            String description
    ) {}
}
