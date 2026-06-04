
package top.wyhao.admin.system.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import top.wyhao.admin.system.model.enums.MessageType;
import top.wyhao.admin.system.model.enums.NoticeScopes;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 消息响应参数 Record
 *
 * @since 2023/10/15 19:05
 */

public class MessageModel {
    @Schema(description = "消息详情响应参数")
    public record Detail(
            @Schema(description = "ID", example = "1")
            Long id,

            @Schema(description = "标题", example = "欢迎注册 xxx")
            String title,

            @Schema(description = "类型", example = "1")
            MessageType type,

            @Schema(description = "跳转路径", example = "/user/profile")
            String path,

            @Schema(description = "是否已读", example = "true")
            Boolean isRead,

            @Schema(description = "读取时间", example = "2023-08-08 23:59:59", type = "string")
            LocalDateTime readTime,

            @Schema(description = "创建时间", example = "2023-08-08 08:08:08", type = "string")
            LocalDateTime createTime
    ) {}

    @Schema(description = "消息响应参数")
    public record Result(
            @Schema(description = "ID", example = "1")
            Long id,

            @Schema(description = "标题", example = "欢迎注册 xxx")
            String title,

            @Schema(description = "内容", example = "尊敬的 xx，欢迎注册使用，请及时配置您的密码。")
            String content,

            @Schema(description = "类型", example = "1")
            MessageType type,

            @Schema(description = "跳转路径", example = "/user/profile")
            String path,

            @Schema(description = "通知范围", example = "2")
            NoticeScopes scope,

            @Schema(description = "通知用户", example = "[1,2]")
            List<String> users,

            @Schema(description = "创建时间", example = "2023-08-08 08:08:08", type = "string")
            LocalDateTime createTime,

            @Schema(description = "是否已读", example = "true")
            Boolean isRead
    ) {
    }

    public record UnreadCount(
            @Schema(description = "类型", example = "1")
            MessageType type,

            @Schema(description = "数量", example = "10")
            Long count
    ) {
    }

    /**
     * 消息查询条件
     *
     * @since 2023/10/15 19:05
     */
    @Schema(description = "消息查询条件")
    public static record MessageQuery(
            /**
             * ID
             */
            @Schema(description = "ID", example = "1")
            Long id,

            /**
             * 标题
             */
            @Schema(description = "标题", example = "欢迎注册 xxx")
            String title,

            /**
             * 类型
             */
            @Schema(description = "类型", example = "1")
            Integer type,

            /**
             * 是否已读
             */
            @Schema(description = "是否已读", example = "true")
            Boolean isRead,

            /**
             * 用户 ID
             */
            @Schema(hidden = true)
            Long userId
    ) {}

    @Schema(description = "消息创建请求参数")
    public record Request(
            /**
             * 标题
             */
            @Schema(description = "标题", example = "欢迎注册 xxx")
            @NotBlank(message = "标题不能为空")
            @Length(max = 50, message = "标题长度不能超过 {max} 个字符")
            String title,

            /**
             * 内容
             */
            @Schema(description = "内容", example = "尊敬的 xx，欢迎注册使用，请及时配置您的密码。")
            @NotBlank(message = "内容不能为空")
            @Length(max = 255, message = "内容长度不能超过 {max} 个字符")
            String content,

            /**
             * 类型
             */
            @Schema(description = "类型", example = "1")
            @NotNull(message = "类型无效")
            MessageType type,

            /**
             * 跳转路径
             */
            @Schema(description = "跳转路径", example = "/user/profile")
            String path
    ) implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;
    }

    /**
     * 未读消息响应参数
     */
    @Schema(description = "未读消息响应参数")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public record UnreadResult(
            /**
             * 未读消息数量
             */
            @Schema(description = "未读消息数量", example = "20")
            Long total,

            /**
             * 各类型未读消息数量
             */
            @Schema(description = "各类型未读消息数量")
            List<UnreadCount> details
    ) {
    }
}