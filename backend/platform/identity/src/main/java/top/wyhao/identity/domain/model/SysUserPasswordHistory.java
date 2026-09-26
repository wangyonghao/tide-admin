package top.wyhao.identity.domain.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户历史密码实体
 */
@Data
@NoArgsConstructor
@TableName("sys_user_password_history")
public class SysUserPasswordHistory {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long userId;

    private String password;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    public SysUserPasswordHistory(Long userId, String password) {
        this.userId = userId;
        this.password = password;
    }
}
