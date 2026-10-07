package top.wyhao.identity.domain.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 成员关系（用户-部门 / 用户-角色）。
 */
@Data
@NoArgsConstructor
@TableName("sys_membership")
public class SysMembership {

    @TableId
    private Long id;

    private Long userId;

    private MembershipScopeType scopeType;

    private Long scopeId;

    private Boolean isPrimary;

    private Integer status;

    private LocalDateTime joinedAt;

    private LocalDateTime expiredAt;

    @TableField(fill = FieldFill.INSERT)
    private Long createUser;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.UPDATE)
    private Long updateUser;

    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;

    private Integer deleted;

    public static SysMembership primaryDepartment(Long userId, Long departmentId) {
        SysMembership membership = of(userId, MembershipScopeType.DEPARTMENT, departmentId);
        membership.setIsPrimary(true);
        return membership;
    }

    public static SysMembership role(Long userId, Long roleId) {
        return of(userId, MembershipScopeType.ROLE, roleId);
    }

    private static SysMembership of(Long userId, MembershipScopeType scopeType, Long scopeId) {
        SysMembership membership = new SysMembership();
        membership.setUserId(userId);
        membership.setScopeType(scopeType);
        membership.setScopeId(scopeId);
        membership.setIsPrimary(false);
        membership.setStatus(MembershipStatus.NORMAL.getValue());
        membership.setJoinedAt(LocalDateTime.now());
        membership.setDeleted(0);
        return membership;
    }
}
