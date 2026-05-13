
package top.wyhao.admin.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import net.sf.jsqlparser.statement.select.Offset;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

/**
 * 系统配置实体
 *
 * @author Yonghao Wang
 * @since 2024/04/26
 */
@Data
@TableName(value = "sys_config", autoResultMap = true)
public class ConfigDO {
    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 配置键
     */
    private String configKey;

    /**
     * 配置值（JSON格式）
     */
    private String configValue;

    /**
     * 配置说明
     */
    private String description;

    @TableField(fill = FieldFill.INSERT)
    private OffsetDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private OffsetDateTime updateTime;
}
