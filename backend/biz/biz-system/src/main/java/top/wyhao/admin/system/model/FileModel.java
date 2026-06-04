
package top.wyhao.admin.system.model;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import top.wyhao.admin.system.model.enums.FileType;
import top.wyhao.cmn.db.query.QueryCondition;
import top.wyhao.cmn.db.query.QueryType;

import java.time.LocalDateTime;

/**
 * 文件请求参数 Record
 */
public class FileModel {
    @Schema(description = "文件请求参数")
    public record Request(
            /**
             * 业务ID
             */
            @Schema(description = "业务ID", example = "1234567890")
            Long bizId,

            /**
             * 业务类型
             */
            @Schema(description = "业务类型", example = "user_avatar")
            String bizType,

            /**
             * 上级目录
             */
            @Schema(description = "上级目录", example = "/")
            String path
    ) {}

    /**
     * 文件查询条件
     *
     * @since 2023/12/23 10:38
     */
    @Schema(description = "文件查询条件")
    public record Query(
            /**
             * 名称
             */
            @Schema(description = "名称", example = "example")
            @QueryCondition(type = QueryType.LIKE)
            String fileName,

            /**
             * 上级目录
             */
            @Schema(description = "上级目录", example = "/")
            @QueryCondition(type = QueryType.EQ)
            String ossPath,

            /**
             * 类型
             */
            @Schema(description = "类型", example = "2")
            @QueryCondition(type = QueryType.EQ)
            FileType fileType,

            /**
             * 关联业务类型
             */
            @Schema(description = "关联业务类型", example = "2")
            @QueryCondition(type = QueryType.EQ)
            String bizType,

            /**
             * 关联业务Id
             */
            @Schema(description = "关联业务Id", example = "2")
            @QueryCondition(type = QueryType.EQ)
            String bizId,

            /**
             * 排序条件
             */
            @Schema(description = "排序条件", example = "createTime,desc")
            String[] sort
    ) {}

    /**
     * 文件查询响应参数
     */
    @ExcelIgnoreUnannotated
@Schema(description = "文件响应参数")
public record Result(
        /**
         * ID
         */
        @Schema(description = "ID", example = "1")
        Long id,

        @Schema(description = "文件名称", example = "example.jpg")
        String fileName,

        @Schema(description = "OSS中的文件名称", example = "6824afe8408da079832dcfb6.jpg")
        String ossFileName,

        @Schema(description = "大小（字节）", example = "4096")
        Long fileSize,

        /**
         * URL
         */
        @Schema(description = "URL", example = "https://examplebucket.oss-cn-hangzhou.aliyuncs.com/2025/2/25/6824afe8408da079832dcfb6.jpg")
        String ossUrl,

        /**
         * 路径
         */
        @Schema(description = "路径", example = "/2025/2/25/6824afe8408da079832dcfb6.jpg")
        String ossPath,

        /**
         * 扩展名
         */
        @Schema(description = "扩展名", example = "jpg")
        String fileExtension,

        /**
         * 内容类型
         */
        @Schema(description = "内容类型", example = "image/jpeg")
        String contentType,

        /**
         * 类型
         */
        @Schema(description = "类型", example = "2")
        FileType type,

        /**
         * SHA256 值
         */
        @Schema(description = "SHA256 值", example = "722f185c48bed892d6fa12e2b8bf1e5f8200d4a70f522fb62112b6caf13cb74e")
        String sha256,

        /**
         * 元数据
         */
        @Schema(description = "元数据", example = "{width:1024,height:1024}")
        String metadata,

        /**
         * 缩略图名称
         */
        @Schema(description = "缩略图名称", example = "example.jpg.min.jpg")
        String thumbnailName,

        /**
         * 缩略图大小（字节)
         */
        @Schema(description = "缩略图大小（字节)", example = "1024")
        Long thumbnailSize,

        /**
         * 缩略图元数据
         */
        @Schema(description = "缩略图文件元数据", example = "{width:100,height:100}")
        String thumbnailMetadata,

        /**
         * 缩略图 URL
         */
        @Schema(description = "缩略图 URL", example = "https://examplebucket.oss-cn-hangzhou.aliyuncs.com/2025/2/25/example.jpg.min.jpg")
        String thumbnailUrl,

        /**
         * 存储 ID
         */
        @Schema(description = "存储 ID", example = "1")
        Long storageId,

        /**
         * 存储名称
         */
        @Schema(description = "存储名称", example = "MinIO")
        String storageName,

        @JsonIgnore
        Long createUser,

        @Schema(description = "创建人", example = "超级管理员")
        String createUserString,

        @Schema(description = "创建时间", example = "2023-08-08 08:08:08", type = "string")
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
        String updateUserString,

        /**
         * 修改时间
         */
        @Schema(description = "修改时间", example = "2023-08-08 08:08:08", type = "string")
        LocalDateTime updateTime
) {
}
}