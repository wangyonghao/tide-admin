
package top.wyhao.admin.system.model.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import top.wyhao.admin.system.model.enums.FileType;
import top.wyhao.cmn.db.query.QueryCondition;
import top.wyhao.cmn.db.query.QueryType;

/**
 * 文件查询条件
 *

 * @since 2023/12/23 10:38
 */
@Data
@Schema(description = "文件查询条件")
public class FileQuery {

    /**
     * 名称
     */
    @Schema(description = "名称", example = "example")
    @QueryCondition(type = QueryType.LIKE)
    private String fileName;

    /**
     * 上级目录
     */
    @Schema(description = "上级目录", example = "/")
    @QueryCondition(type = QueryType.EQ)
    private String ossPath;

    /**
     * 类型
     */
    @Schema(description = "类型", example = "2")
    @QueryCondition(type = QueryType.EQ)
    private FileType fileType;

    @Schema(description = "关联业务类型", example = "2")
    @QueryCondition(type = QueryType.EQ)
    private String bizType;
    @Schema(description = "关联业务Id", example = "2")
    @QueryCondition(type = QueryType.EQ)
    private String bizId;

    /**
     * 排序条件
     */
    @Schema(description = "排序条件", example = "createTime,desc")
    private String[] sort;
}