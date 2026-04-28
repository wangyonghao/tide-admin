
package top.wyhao.admin.tenant.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import top.wyhao.starter.data.entity.BaseDO;
import top.wyhao.starter.core.enums.StatusEnum;
import top.wyhao.starter.web.core.annotation.DictModel;

import java.io.Serial;

/**
 * 套餐实体
 *
 * @author 小熊
 * @author Charles7c
 * @since 2024/11/26 11:25
 */
@Data
@DictModel
@TableName("tenant_package")
public class PackageDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 名称
     */
    private String name;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 菜单选择是否父子节点关联
     */
    private Boolean menuCheckStrictly;

    /**
     * 描述
     */
    private String description;

    /**
     * 状态
     */
    private StatusEnum status;
}