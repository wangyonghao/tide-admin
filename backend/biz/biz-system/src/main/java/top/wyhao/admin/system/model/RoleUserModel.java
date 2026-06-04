
package top.wyhao.admin.system.model;

import cn.idev.excel.annotation.ExcelProperty;
import cn.idev.excel.annotation.write.style.ColumnWidth;
import cn.idev.excel.annotation.write.style.ContentStyle;
import cn.idev.excel.annotation.write.style.HeadRowHeight;
import cn.idev.excel.annotation.write.style.HeadStyle;
import cn.idev.excel.enums.poi.HorizontalAlignmentEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import top.wyhao.starter.core.enums.GenderEnum;
import top.wyhao.starter.core.enums.StatusEnum;

import java.util.List;

/**
 * 角色关联用户响应参数 Record
 */

public class RoleUserModel {
    @Schema(description = "角色关联用户响应参数")
    public record RoleMemberResult(
            /**
             * ID
             */
            @Schema(description = "ID", example = "1")
            Long id,

            /**
             * 角色 ID
             */
            @Schema(description = "角色 ID", example = "1")
            Long roleId,

            /**
             * 用户 ID
             */
            @Schema(description = "用户 ID", example = "1")
            Long userId,

            /**
             * 用户名
             */
            @Schema(description = "用户名", example = "zhangsan")
            String username,

            /**
             * 昵称
             */
            @Schema(description = "昵称", example = "张三")
            String nickname,

            /**
             * 性别
             */
            @Schema(description = "性别", example = "1")
            GenderEnum gender,

            /**
             * 用户头像
             */
            @Schema(description = "用户头像", example = "https://example.com/avatar.jpg")
            String avatar,

            /**
             * 状态
             */
            @Schema(description = "状态", example = "1")
            StatusEnum status,

            /**
             * 是否为系统内置数据
             */
            @Schema(description = "是否为系统内置数据", example = "false")
            Boolean isBuiltin,

            /**
             * 描述
             */
            @Schema(description = "描述", example = "测试人员描述信息")
            String description,

            /**
             * 部门 ID
             */
            @Schema(description = "部门 ID", example = "5")
            Long deptId,

            /**
             * 所属部门
             */
            @Schema(description = "所属部门", example = "测试部")
            String deptName,

            /**
             * 角色 ID 列表
             */
            @Schema(description = "角色 ID 列表", example = "2")
            List<Long> roleIds,

            /**
             * 角色名称列表
             */
            @Schema(description = "角色名称列表", example = "测试人员")
            List<String> roleNames
    ) {
    }

    @Schema(description = "角色成员查询条件")
    public record Query(
            /**
             * 角色 ID
             */
            @Schema(description = "角色 ID", example = "1")
            Long roleId,

            /**
             * 关键词(用户名/昵称）
             */
            @Schema(description = "关键词", example = "zhangsan")
            String keyword
    ) {}

    /**
     * 角色成员删除请求参数
     */
    @Schema(description = "角色成员删除请求参数")
    public record Remove(
            /**
             * ID
             */
            @Schema(description = "ID", example = "[1,2]")
            @NotEmpty(message = "ID 不能为空")
            List<Long> userIds
    ) {
    }

    /**
     * 用户导出响应参数
     */
    @HeadRowHeight(20)
    @HeadStyle(horizontalAlignment = HorizontalAlignmentEnum.CENTER)
    @ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.CENTER)
    @Schema(description = "用户导出响应参数")
    public record Excel(
            /**
             * 用户名
             */
            @ExcelProperty(value = "用户名")
            @ColumnWidth(20)
            @Schema(description = "用户名", example = "zhangsan")
            String username,

            /**
             * 昵称
             */
            @ExcelProperty(value = "昵称")
            @ColumnWidth(20)
            @Schema(description = "昵称", example = "张三")
            String nickname,

            /**
             * 性别
             */
            @ExcelProperty(value = "性别")
            @ColumnWidth(10)
            @Schema(description = "性别", example = "男")
            String gender,

            /**
             * 邮箱
             */
            @ExcelProperty(value = "邮箱")
            @ColumnWidth(30)
            @Schema(description = "邮箱", example = "zhangsan@example.com")
            String email,

            /**
             * 手机号码
             */
            @ExcelProperty(value = "手机号码")
            @ColumnWidth(20)
            @Schema(description = "手机号码", example = "18888888888")
            String phone,

            /**
             * 状态
             */
            @ExcelProperty(value = "状态")
            @ColumnWidth(10)
            @Schema(description = "状态", example = "启用")
            String status,

            /**
             * 所属部门
             */
            @ExcelProperty(value = "所属部门")
            @ColumnWidth(30)
            @Schema(description = "所属部门", example = "测试部")
            String deptName,

            /**
             * 角色
             */
            @ExcelProperty(value = "角色")
            @ColumnWidth(30)
            @Schema(description = "角色", example = "测试人员")
            String roleNames,

            /**
             * 描述
             */
            @ExcelProperty(value = "描述")
            @ColumnWidth(40)
            @Schema(description = "描述", example = "张三描述信息")
            String description
    ) {
    }
}
