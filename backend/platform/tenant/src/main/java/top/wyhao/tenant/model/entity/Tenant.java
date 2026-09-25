
package top.wyhao.admin.tenant.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import top.wyhao.cmn.db.model.BaseEntity;
import top.wyhao.starter.core.enums.StatusEnum;

import java.io.Serial;
import java.time.LocalDateTime;

/**
 * 租户实体
 *


 * @since 2024/11/26 17:20
 */
@Data
@TableName("sys_tenant")
public class Tenant extends BaseEntity {
    /**
     * 名称
     */
    private String name;
    /** 简称 */
    private String shortName;
    /** 域名 */
    private String domain;
    /** 租户编号（由用户自定义，未定义时系统自动生成）*/
    private String displayID;
    /** 联系人名称 */
    private String contactName;
    /** 联系人邮箱 */
    private String contactEmail;
    /** 联系人手机 */
    private String contactMobile;
    /** 联系人邮编 */
    private String postcode;
    /** 联系地址 */
    private String address;
    /** 过期时间 */
    private LocalDateTime expireTime;
    /** 备注 */
    private String remark;
    /** 状态 */
    private Integer status;
}