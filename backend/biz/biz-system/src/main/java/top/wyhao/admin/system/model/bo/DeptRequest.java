
package top.wyhao.admin.system.model.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import top.wyhao.starter.core.validation.EnumValue;

/**
 * 部门创建或修改请求参数
 *
 * @since 2026/5/20
 */
@Data
@Schema(description = "部门创建或修改请求参数")
public class DeptRequest {

    @Schema(description = "编码", example = "A001")
    @NotBlank(message = "编码不能为空")
    @Length(max = 30, message = "编码长度不能超过 {max} 个字符")
    private String code;

    /**
     * 名称
     */
    @Schema(description = "名称", example = "测试部")
    @NotBlank(message = "名称不能为空")
    @Length(max = 50, message = "名称长度不能超过 {max} 个字符")
    private String name;

    /**
     * 部门类型
     * 例如：1-分公司 2-部门 3-工作组 参考字典表dept_type
     */
    @NotNull(message = "部门类型不能为空")
    private Integer type;

    /**
     * 上级部门 ID
     */
    @Schema(description = "上级部门 ID", example = "2")
    @NotNull(message = "上级部门不能为空")
    private Long parentId;
    /**
     * 排序
     */
    @Schema(description = "排序", example = "1")
    @Min(value = 1, message = "排序最小值为 {value}")
    private Integer sort;

    /**
     * 描述
     */
    @Schema(description = "描述", example = "测试部描述信息")
    @Length(max = 200, message = "描述长度不能超过 {max} 个字符")
    private String description;

    /**
     * 状态
     */
    @Schema(description = "状态", example = "1")
    @EnumValue(enumValues = {"1","2"}, message = "状态值必须是 1 或 2")
    private Integer status;
}
