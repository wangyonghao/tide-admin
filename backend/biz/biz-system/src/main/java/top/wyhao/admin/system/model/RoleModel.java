
package top.wyhao.admin.system.model;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;
import top.wyhao.cmn.db.query.QueryCondition;
import top.wyhao.cmn.db.query.QueryType;
import top.wyhao.starter.core.constant.RegexConstants;
import top.wyhao.starter.core.enums.DataScopeEnum;
import top.wyhao.starter.excel.converter.ExcelBaseEnumConverter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 角色创建或修改请求参数 Record
 */
public class RoleModel {
    @Schema(description = "角色创建或修改请求参数")
    public record Request(
            @Schema(description = "名称", example = "测试人员")
            @NotBlank(message = "名称不能为空")
            @Pattern(regexp = RegexConstants.GENERAL_NAME, message = "名称长度为 2-30 个字符，支持中文、字母、数字、下划线，短横线")
            String name,

            @Schema(description = "编码", example = "test")
            @NotBlank(message = "编码不能为空")
            @Pattern(regexp = RegexConstants.GENERAL_CODE, message = "编码长度为 2-30 个字符，支持大小写字母、数字、下划线，以字母开头")
            String code,

            @Schema(description = "排序", example = "1")
            @Min(value = 0, message = "排序最小值为 {value}")
            Integer sort,

            @Schema(description = "描述", example = "测试人员描述信息")
            @Length(max = 200, message = "描述长度不能超过 {max} 个字符")
            String description,

            @Schema(description = "数据权限", example = "5")
            DataScopeEnum dataScope,

            @Schema(description = "权限范围：部门 ID 列表", example = "5")
            List<Long> deptIds,

            @Schema(description = "部门选择是否父子节点关联", example = "false")
            Boolean deptCheckStrictly
    ) {}

    @Schema(description = "角色查询条件")
    public record Query(
            @Schema(description = "关键词", example = "测试人员")
            @QueryCondition(columns = {"name", "code", "description"}, type = QueryType.LIKE)
            String description,

            @Schema(description = "排除的编码列表", example = "[super_admin,tenant_admin]")
            @QueryCondition(columns = "code", type = QueryType.NOT_IN)
            List<String> excludeRoleCodes,

            @Schema(description = "排序条件", example = "createTime,desc")
            String[] sort
    ) {}

    @Schema(description = "角色响应参数")
    public record Result(
            @Schema(description = "ID", example = "1")
            Long id,

            @JsonIgnore
            Long createUser,

            @Schema(description = "创建人", example = "超级管理员")
            String createUserString,

            @Schema(description = "创建时间", example = "2023-08-08 08:08:08", type = "string")
            LocalDateTime createTime,

            @Schema(description = "是否禁用修改", example = "true")
            @JsonInclude(JsonInclude.Include.NON_NULL)
            Boolean disabled,

            @JsonIgnore
            Long updateUser,

            @Schema(description = "修改人", example = "李四")
            String updateUserString,

            @Schema(description = "修改时间", example = "2023-08-08 08:08:08", type = "string")
            LocalDateTime updateTime,

            @Schema(description = "名称", example = "测试人员")
            String name,

            @Schema(description = "编码", example = "test")
            String code,

            @Schema(description = "数据权限", example = "5")
            DataScopeEnum dataScope,

            @Schema(description = "排序", example = "1")
            Integer sort,

            @Schema(description = "是否为系统内置数据", example = "false")
            Boolean isBuiltin,

            @Schema(description = "描述", example = "测试人员描述信息")
            String description
    ) {}

    @ExcelIgnoreUnannotated
@Schema(description = "角色详情响应参数")
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
        @ExcelProperty(value = "修改人", order = Integer.MAX_VALUE - 2)
        String updateUserString,

        /**
         * 修改时间
         */
        @Schema(description = "修改时间", example = "2023-08-08 08:08:08", type = "string")
        @ExcelProperty(value = "修改时间", order = Integer.MAX_VALUE - 1)
        LocalDateTime updateTime,

        /**
         * 名称
         */
        @Schema(description = "名称", example = "测试人员")
        @ExcelProperty(value = "名称")
        String name,

        /**
         * 编码
         */
        @Schema(description = "编码", example = "test")
        @ExcelProperty(value = "编码")
        String code,

        /**
         * 数据权限
         */
        @Schema(description = "数据权限", example = "5")
        @ExcelProperty(value = "数据权限", converter = ExcelBaseEnumConverter.class)
        DataScopeEnum dataScope,

        /**
         * 排序
         */
        @Schema(description = "排序", example = "1")
        @ExcelProperty(value = "排序")
        Integer sort,

        /**
         * 是否为系统内置数据
         */
        @Schema(description = "是否为系统内置数据", example = "false")
        @ExcelProperty(value = "系统内置")
        Boolean isBuiltin,

        /**
         * 菜单选择是否父子节点关联
         */
        @Schema(description = "菜单选择是否父子节点关联", example = "false")
        Boolean menuCheckStrictly,

        /**
         * 部门选择是否父子节点关联
         */
        @Schema(description = "部门选择是否父子节点关联", example = "false")
        Boolean deptCheckStrictly,

        /**
         * 描述
         */
        @Schema(description = "描述", example = "测试人员描述信息")
        @ExcelProperty(value = "描述")
        String description,

        /**
         * 功能权限：菜单 ID 列表
         */
        @Schema(description = "功能权限：菜单 ID 列表", example = "1000,1010,1011,1012,1013,1014")
        List<Long> menuIds,

        /**
         * 权限范围：部门 ID 列表
         */
        @Schema(description = "权限范围：部门 ID 列表", example = "5")
        List<Long> deptIds
) {
}
}
