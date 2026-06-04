
package top.wyhao.admin.system.model;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import cn.sticki.spel.validator.constrain.SpelFuture;
import cn.sticki.spel.validator.constrain.SpelNotEmpty;
import cn.sticki.spel.validator.constrain.SpelNotNull;
import cn.sticki.spel.validator.jakarta.SpelValid;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import top.wyhao.admin.system.model.enums.NoticeScopes;
import top.wyhao.admin.system.model.enums.NoticeStatus;
import top.wyhao.starter.excel.converter.ExcelBaseEnumConverter;
import top.wyhao.starter.web.excel.DictExcelProperty;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 公告响应参数
 *
 * @since 2023/8/20 10:55
 */
public class NotificationModel {

    @Schema(description = "公告响应参数")
    public record Result(
            /**
             * ID
             */
            @Schema(description = "ID", example = "1")
            @ExcelProperty(value = "ID", order = 1)
            Long id,

            /**
             * 创建人
             */
            @JsonIgnore
            Long createUser,

            /**
             * 创建人
             */
            @Schema(description = "创建人", example = "超级管理员")
            @ExcelProperty(value = "创建人", order = Integer.MAX_VALUE - 4)
            String createUserString,

            /**
             * 创建时间
             */
            @Schema(description = "创建时间", example = "2023-08-08 08:08:08", type = "string")
            @ExcelProperty(value = "创建时间", order = Integer.MAX_VALUE - 3)
            LocalDateTime createTime,

            /**
             * 是否禁用修改
             */
            @Schema(description = "是否禁用修改", example = "true")
            @JsonInclude(JsonInclude.Include.NON_NULL)
            Boolean disabled,

            /**
             * 标题
             */
            @Schema(description = "标题", example = "这是公告标题")
            String title,

            /**
             * 分类（取值于字典 notice_type）
             */
            @Schema(description = "分类（取值于字典 notice_type）", example = "1")
            String type,

            /**
             * 通知范围
             */
            @Schema(description = "通知范围(1.所有人 2.指定用户)", example = "1")
            NoticeScopes noticeScope,

            /**
             * 通知方式
             */
            @Schema(description = "通知方式", example = "[1,2]")
            List<Integer> noticeMethods,

            /**
             * 是否定时
             */
            @Schema(description = "是否定时", example = "false")
            Boolean isTiming,

            /**
             * 发布时间
             */
            @Schema(description = "发布时间", example = "2023-08-08 00:00:00", type = "string")
            LocalDateTime publishTime,

            /**
             * 是否置顶
             */
            @Schema(description = "是否置顶", example = "false")
            Boolean isTop,

            /**
             * 状态
             */
            @Schema(description = "状态", example = "3")
            NoticeStatus status,

            /**
             * 是否已读
             */
            @Schema(description = "是否已读", example = "false")
            Boolean isRead
    ) implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;
    }
    @ExcelIgnoreUnannotated
    @Schema(description = "公告详情响应参数")
    public record Detail(
            /**
             * ID
             */
            @Schema(description = "ID", example = "1")
            @ExcelProperty(value = "ID", order = 1)
            Long id,

            /**
             * 创建人
             */
            @JsonIgnore
            Long createUser,

            /**
             * 创建人
             */
            @Schema(description = "创建人", example = "超级管理员")
            @ExcelProperty(value = "创建人", order = Integer.MAX_VALUE - 4)
            String createUserString,

            /**
             * 创建时间
             */
            @Schema(description = "创建时间", example = "2023-08-08 08:08:08", type = "string")
            @ExcelProperty(value = "创建时间", order = Integer.MAX_VALUE - 3)
            LocalDateTime createTime,

            /**
             * 是否禁用修改
             */
            @Schema(description = "是否禁用修改", example = "true")
            @JsonInclude(JsonInclude.Include.NON_NULL)
            Boolean disabled,

            /**
             * 修改人
             */
            @JsonIgnore
            Long updateUser,

            /**
             * 修改人
             */
            @Schema(description = "修改人", example = "李四")
            @ExcelProperty(value = "修改人", order = Integer.MAX_VALUE - 2)
            String updateUserString,

            /**
             * 修改时间
             */
            @Schema(description = "修改时间", example = "2023-08-08 08:08:08", type = "string")
            @ExcelProperty(value = "修改时间", order = Integer.MAX_VALUE - 1)
            LocalDateTime updateTime,

            /**
             * 标题
             */
            @Schema(description = "标题", example = "这是公告标题")
            @ExcelProperty(value = "标题", order = 2)
            String title,

            /**
             * 分类（取值于字典 notice_type）
             */
            @Schema(description = "分类（取值于字典 notice_type）", example = "1")
            @ExcelProperty(value = "分类", converter = top.wyhao.starter.web.excel.ExcelDictConverter.class, order = 3)
            @DictExcelProperty("notice_type")
            String type,

            /**
             * 内容
             */
            @Schema(description = "内容", example = "这是公告内容")
            String content,

            /**
             * 通知范围
             */
            @Schema(description = "通知范围", example = "2")
            @ExcelProperty(value = "通知范围", converter = ExcelBaseEnumConverter.class, order = 4)
            NoticeScopes noticeScope,

            /**
             * 通知用户
             */
            @Schema(description = "通知用户", example = "[1,2,3]")
            List<String> noticeUsers,

            /**
             * 通知方式
             */
            @Schema(description = "通知方式", example = "[1,2]")
            List<Integer> noticeMethods,

            /**
             * 是否定时
             */
            @Schema(description = "是否定时", example = "false")
            @ExcelProperty(value = "是否定时", order = 5)
            Boolean isTiming,

            /**
             * 发布时间
             */
            @Schema(description = "发布时间", example = "2023-08-08 00:00:00", type = "string")
            @ExcelProperty(value = "发布时间", order = 6)
            LocalDateTime publishTime,

            /**
             * 是否置顶
             */
            @Schema(description = "是否置顶", example = "false")
            @ExcelProperty(value = "是否置顶", order = 7)
            Boolean isTop,

            /**
             * 状态
             */
            @Schema(description = "状态", example = "3")
            @ExcelProperty(value = "状态", converter = ExcelBaseEnumConverter.class, order = 8)
            NoticeStatus status
    ) {
    }

    @Schema(description = "未读公告数量响应参数")
    public record UnreadCount(
            /**
             * 未读公告数量
             */
            @Schema(description = "未读公告数量", example = "1")
            Integer total
    ) {}

    /**
     * 公告查询条件
     *
     * @since 2023/8/20 10:55
     */
    @Schema(description = "公告查询条件")
    public static record NoticeQuery(
            /**
             * 标题
             */
            @Schema(description = "标题", example = "这是公告标题")
            String title,

            /**
             * 分类（取值于字典 notice_type）
             */
            @Schema(description = "分类（取值于字典 notice_type）", example = "1")
            String type,

            /**
             * 用户 ID
             */
            @Schema(hidden = true)
            Long userId,

            /**
             * 排序条件
             */
            @Schema(description = "排序条件", example = "createTime,desc")
            String[] sort
    ) {}

    @SpelValid
    @Schema(description = "公告创建或修改请求参数")
    public record Request(
            /**
             * 标题
             */
            @Schema(description = "标题", example = "这是公告标题")
            @NotBlank(message = "标题不能为空")
            @Length(max = 150, message = "标题长度不能超过 {max} 个字符")
            String title,

            /**
             * 内容
             */
            @Schema(description = "内容", example = "这是公告内容")
            @NotBlank(message = "内容不能为空")
            String content,

            /**
             * 分类（取值于字典 notice_type）
             */
            @Schema(description = "分类（取值于字典 notice_type）", example = "1")
            @NotBlank(message = "分类不能为空")
            @Length(max = 30, message = "分类长度不能超过 {max} 个字符")
            String type,

            /**
             * 通知范围
             */
            @Schema(description = "通知范围", example = "2")
            @NotNull(message = "通知范围不能为空")
            NoticeScopes noticeScope,

            /**
             * 通知用户
             */
            @Schema(description = "通知用户", example = "[1,2,3]")
            @SpelNotEmpty(condition = "#this.noticeScope == T(top.wyhao.admin.system.model.enums.NoticeScopes).USER", message = "通知用户不能为空")
            List<String> noticeUsers,

            /**
             * 通知方式
             */
            @Schema(description = "通知方式", example = "[1,2]")
            List<Integer> noticeMethods,

            /**
             * 是否定时
             */
            @Schema(description = "是否定时", example = "true")
            @NotNull(message = "是否定时不能为空")
            Boolean isTiming,

            /**
             * 发布时间
             */
            @Schema(description = "发布时间", example = "2023-08-08 00:00:00", type = "string")
            @SpelNotNull(condition = "#this.isTiming == true", message = "定时发布时间不能为空")
            @SpelFuture(condition = "#this.isTiming == true", message = "定时发布时间不能早于当前时间")
            LocalDateTime publishTime,

            /**
             * 是否置顶
             */
            @Schema(description = "是否置顶", example = "false")
            Boolean isTop,

            /**
             * 状态
             */
            @Schema(description = "状态", example = "3")
            NoticeStatus status
    ) implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;
    }
}