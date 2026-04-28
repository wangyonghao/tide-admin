
package top.wyhao.starter.core.model;

import cn.hutool.core.collection.CollUtil;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.wyhao.starter.core.enums.RoleCodeEnum;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * 登录用户
 */
@Data
@NoArgsConstructor
public class LoginUser{
    /**
     * ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 租户 ID
     */
    private Long tenantId;

    /**
     * 部门 ID
     */
    private Long deptId;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 最后一次修改密码时间
     */
    private LocalDateTime pwdResetTime;

    /**
     * 登录时系统设置的密码过期天数
     */
    private Integer passwordExpirationDays;


//    /**
//     * 是否为超级管理员
//     *
//     * @return true：是；false：否
//     */
//    public boolean isSuperAdmin() {
//        if (CollUtil.isEmpty(roleCodes)) {
//            return false;
//        }
//        return roleCodes.contains(RoleCodeEnum.SUPER_ADMIN.getCode());
//    }
//
//    /**
//     * 是否为租户管理员
//     *
//     * @return true：是；false：否
//     */
//    public boolean isTenantAdmin() {
//        if (CollUtil.isEmpty(roleCodes)) {
//            return false;
//        }
//        return roleCodes.contains(RoleCodeEnum.TENANT_ADMIN.getCode());
//    }

    /**
     * 登录设备类型
     */
    private String deviceType;

}
