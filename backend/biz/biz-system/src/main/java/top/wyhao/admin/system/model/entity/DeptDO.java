
package top.wyhao.admin.system.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import top.wyhao.starter.core.enums.StatusEnum;
import top.wyhao.starter.data.entity.BaseDO;

/**
 * 部门实体
 *
 * @author Charles7c
 * @since 2023/1/22 13:50
 */
@Data
@TableName("sys_dept")
public class DeptDO extends BaseDO {

    /**
     * 名称
     */
    private String name;

    /**
     * 上级部门 ID
     */
    private Long parentId;

    /**
     * 祖级列表
     */
    private String ancestors;

    /**
     * 描述
     */
    private String description;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 状态
     */
    private StatusEnum status;

    /**
     * 是否为系统内置数据
     */
    private Boolean isBuiltin;
}
