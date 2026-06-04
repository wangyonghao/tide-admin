
package top.wyhao.admin.system.model;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import jakarta.validation.groups.Default;
import org.hibernate.validator.constraints.Length;
import top.wyhao.starter.core.constant.RegexConstants;
import top.wyhao.starter.core.enums.GenderEnum;
import top.wyhao.starter.core.enums.StatusEnum;
import top.wyhao.starter.core.validation.Mobile;
import top.wyhao.starter.excel.converter.ExcelBaseEnumConverter;
import top.wyhao.starter.excel.converter.ExcelListConverter;
import top.wyhao.starter.web.sensitive.annotation.Sensitive;
import top.wyhao.starter.web.sensitive.enums.SensitiveMethod;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户响应参数 Record
 *
 * @since 2023/2/20 21:08
 */
public class UserModel {
    @Schema(description = "用户响应参数")
    public record Result(
            @Schema(description = "ID", example = "1")
            Long id,

            @Schema(description = "是否禁用修改", example = "true")
            @JsonInclude(JsonInclude.Include.NON_NULL)
            Boolean disabled,

            @Schema(description = "用户名", example = "zhangsan")
            String username,

            @Schema(description = "昵称", example = "张三")
            String nickname,

            @Schema(description = "性别", example = "1")
            GenderEnum gender,

            @Schema(description = "头像地址", example = "https://himg.bdimg.com/sys/portrait/item/public.1.81ac9a9e.rf1ix17UfughLQjNo7XQ_w.jpg")
            String avatar,

            @Schema(description = "邮箱", example = "c*******@126.com")
            @Sensitive(SensitiveMethod.EMAIL)
            String email,

            @Schema(description = "手机号码", example = "188****8888")
            @Sensitive(SensitiveMethod.MOBILE_PHONE)
            String phone,

            @Schema(description = "状态", example = "1")
            String status,

            @Schema(description = "是否为系统内置数据", example = "false")
            Boolean isBuiltin,

            @Schema(description = "描述", example = "张三描述信息")
            String description,

            @Schema(description = "部门 ID", example = "5")
            Long deptId,

            @Schema(description = "所属部门", example = "测试部")
            String deptName,

            @Schema(description = "角色名称列表", example = "测试人员")
            List<String> roleNames
    ) {
    }

    @Schema(description = "用户查询条件")
    public record Query(
            @Schema(description = "关键词", example = "zhangsan")
            String keyword,

            @Schema(description = "状态", example = "1")
            StatusEnum status,

            @Schema(description = "创建时间", example = "2023-08-08 00:00:00,2023-08-08 23:59:59")
            @Size(max = 2, message = "创建时间必须是一个范围")
            List<LocalDateTime> createTime,

            @Schema(description = "部门 ID", example = "1")
            Long deptId,

            @Schema(description = "用户 ID 列表", example = "[1,2,3]")
            List<Long> userIds,

            @Schema(description = "角色 ID", example = "1")
            Long roleId,

            @Schema(description = "排序条件", example = "createTime,desc")
            String[] sort
    ) {
    }

    @ExcelIgnoreUnannotated
    @Schema(description = "用户详情响应参数")
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
            String updateUserString,

            /**
             * 修改时间
             */
            @Schema(description = "修改时间", example = "2023-08-08 08:08:08", type = "string")
            LocalDateTime updateTime,

            /**
             * 用户名
             */
            @Schema(description = "用户名", example = "zhangsan")
            @ExcelProperty(value = "用户名", order = 2)
            String username,

            /**
             * 昵称
             */
            @Schema(description = "昵称", example = "张三")
            @ExcelProperty(value = "昵称", order = 3)
            String nickname,

            /**
             * 状态
             */
            @Schema(description = "状态", example = "1")
            @ExcelProperty(value = "状态", converter = ExcelBaseEnumConverter.class, order = 4)
            StatusEnum status,

            /**
             * 性别
             */
            @Schema(description = "性别", example = "1")
            @ExcelProperty(value = "性别", converter = ExcelBaseEnumConverter.class, order = 5)
            GenderEnum gender,

            /**
             * 部门 ID
             */
            @Schema(description = "部门 ID", example = "5")
            @ExcelProperty(value = "部门 ID", order = 6)
            Long deptId,

            /**
             * 所属部门
             */
            @Schema(description = "所属部门", example = "测试部")
            @ExcelProperty(value = "所属部门", order = 7)
            String deptName,

            /**
             * 角色 ID 列表
             */
            @Schema(description = "角色 ID 列表", example = "2")
            @ExcelProperty(value = "角色 ID 列表", converter = ExcelListConverter.class, order = 8)
            List<Long> roleIds,

            /**
             * 角色名称列表
             */
            @Schema(description = "角色名称列表", example = "测试人员")
            @ExcelProperty(value = "角色", converter = ExcelListConverter.class, order = 9)
            List<String> roleNames,

            /**
             * 手机号码
             */
            @Schema(description = "手机号码", example = "13811111111")
            @ExcelProperty(value = "手机号码", order = 10)
            String phone,

            /**
             * 邮箱
             */
            @Schema(description = "邮箱", example = "123456789@qq.com")
            @ExcelProperty(value = "邮箱", order = 11)
            String email,

            /**
             * 是否为系统内置数据
             */
            @Schema(description = "系统内置", example = "false")
            @ExcelProperty(value = "系统内置", order = 12)
            Boolean isBuiltin,

            /**
             * 描述
             */
            @Schema(description = "描述", example = "张三描述信息")
            @ExcelProperty(value = "描述", order = 13)
            String description,

            /**
             * 头像地址
             */
            @Schema(description = "头像地址", example = "https://himg.bdimg.com/sys/portrait/item/public.1.81ac9a9e.rf1ix17UfughLQjNo7XQ_w.jpg")
            @ExcelProperty(value = "头像地址", order = 14)
            String avatar,

            /**
             * 最后一次修改密码时间
             */
            @Schema(description = "最后一次修改密码时间", example = "2023-08-08 08:08:08", type = "string")
            LocalDateTime pwdResetTime
    ) {
    }

    /**
     * 用户创建或修改请求参数
     */
    @Schema(description = "用户创建或修改请求参数")
    public record Request(
            /**
             * 用户名
             */
            @Schema(description = "用户名", example = "zhangsan")
            @NotBlank(message = "用户名不能为空", groups = Create.class)
            @Null(message = "不能修改用户名", groups = Update.class)
            @Pattern(regexp = RegexConstants.USERNAME, message = "用户名长度为 4-64 个字符，支持大小写字母、数字、下划线，以字母开头")
            String username,

            /**
             * 昵称
             */
            @Schema(description = "昵称", example = "张三")
            @NotBlank(message = "昵称不能为空", groups = Create.class)
            @Pattern(regexp = RegexConstants.GENERAL_NAME, message = "昵称长度为 2-30 个字符，支持中文、字母、数字、下划线，短横线")
            String nickname,

            /**
             * 密码
             */
            @Schema(description = "密码", example = "RSA 公钥加密的密码")
            @NotBlank(message = "密码不能为空", groups = Create.class)
            String password,

            /**
             * 所属部门
             */
            @Schema(description = "所属部门", example = "5")
            @NotNull(message = "所属部门不能为空", groups = Create.class)
            Long deptId,

            /**
             * 所属角色
             */
            @Schema(description = "所属角色", example = "2")
            @NotEmpty(message = "所属角色不能为空", groups = Create.class)
            List<Long> roleIds,

            /**
             * 邮箱
             */
            @Schema(description = "邮箱", example = "123456789@qq.com")
            @Length(max = 255, message = "邮箱长度不能超过 {max} 个字符")
            @Email(message = "邮箱格式不正确")
            String email,

            /**
             * 手机号
             */
            @Schema(description = "手机号", example = "13811111111")
            @Mobile
            String phone,

            /**
             * 性别
             */
            @Schema(description = "性别", example = "1")
            GenderEnum gender,

            /**
             * 描述
             */
            @Schema(description = "描述", example = "张三描述信息")
            @Length(max = 200, message = "描述长度不能超过 {max} 个字符")
            String description,

            /**
             * 状态
             */
            @Schema(description = "状态", example = "1")
            StatusEnum status
    ) {
        public interface Create extends Default {
        }

        public interface Update extends Default {
        }
    }
}