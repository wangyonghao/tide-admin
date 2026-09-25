package top.wyhao.settings.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import top.wyhao.starter.core.constant.RegexConstants;

import java.util.Map;

/**
 * 字典创建或修改请求参数
 */
@Data
@Schema(description = "字典创建或修改请求参数")
public class DictRequest {

    @Schema(description = "字典类型", example = "notice_type")
    @NotBlank(message = "字典类型不能为空")
    @Pattern(regexp = RegexConstants.GENERAL_CODE, message = "字典类型长度为 2-30 个字符，支持大小写字母、数字、下划线，以字母开头")
    private String dictType;

    @Schema(description = "字典值", example = "1")
    @NotBlank(message = "字典值不能为空")
    @Length(max = 255, message = "字典值长度不能超过 {max} 个字符")
    private String value;

    @Schema(description = "字典标签", example = "产品新闻")
    @NotBlank(message = "字典标签不能为空")
    @Length(max = 255, message = "字典标签长度不能超过 {max} 个字符")
    private String label;

    @Schema(description = "扩展信息", example = "{\"color\": \"primary\"}")
    private Map<String, Object> extra;

    @Schema(description = "排序", example = "1")
    private Integer sort;

    @Schema(description = "是否启用", example = "true")
    private Boolean enabled;

    @Schema(description = "描述", example = "公告类型描述信息")
    @Length(max = 500, message = "描述长度不能超过 {max} 个字符")
    private String description;
}
