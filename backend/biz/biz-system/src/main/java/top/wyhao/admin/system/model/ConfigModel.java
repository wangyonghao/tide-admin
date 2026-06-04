
package top.wyhao.admin.system.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * 系统配置请求信息 Record
 */
public class ConfigModel {

    @Schema(description = "系统配置查询条件")
    public record Query(
            @Schema(description = "配置键", example = "site")
            String configKey,

            @Schema(description = "关键词", example = "站点")
            String searchWords,

            @Schema(description = "排序条件", example = "createTime,desc")
            String[] sort
    ) {}

    @Schema(description = "系统配置请求信息")
    public record Request(
            @Schema(description = "配置键", example = "site")
            @NotBlank(message = "配置键不能为空", groups = {Create.class})
            @Size(max = 100, message = "配置键长度不能超过 100 个字符", groups = {Create.class, Update.class})
            String configKey,

            @Schema(description = "配置值（JSON格式）", example = "{\"siteName\":\"WYH Admin\"}")
            String configValue,

            @Schema(description = "配置说明", example = "站点配置")
            @Size(max = 255, message = "配置说明长度不能超过 255 个字符", groups = {Create.class, Update.class})
            String description,

            @Schema(description = "版本号", example = "1")
            Integer version
    ) {}

    /**
     * 创建校验组
     */
    public interface Create {}

    /**
     * 更新校验组
     */
    public interface Update {
    }


    public record Result(
            @Schema(description = "ID", example = "1")
            Long id,

            @Schema(description = "配置键", example = "site")
            String configKey,

            @Schema(description = "配置值（JSON格式）", example = "{\"siteName\":\"WYH Admin\"}")
            String configValue,

            @Schema(description = "配置说明", example = "站点配置")
            String description,

            @Schema(description = "版本号", example = "1")
            @JsonIgnore
            Integer version,

            @Schema(description = "创建时间", example = "2024-04-26 10:00:00")
            LocalDateTime createdAt,

            @Schema(description = "更新时间", example = "2024-04-26 10:00:00")
            LocalDateTime updatedAt
    ) {}
}
