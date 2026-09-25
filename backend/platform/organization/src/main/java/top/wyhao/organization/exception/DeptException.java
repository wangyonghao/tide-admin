package top.wyhao.organization.exception;

import cn.hutool.core.util.StrUtil;
import top.wyhao.starter.core.exception.BizException;

/**
 * 部门业务异常
 */
public class DeptException extends BizException {

    public DeptException(String message) {
        super(message);
    }

    public DeptException(String code, String message) {
        super(code, message);
    }

    public static DeptException of(String message) {
        return new DeptException(message);
    }

    public static DeptException of(String code, String message) {
        return new DeptException(code, message);
    }

    public static DeptException notFound(Long id) {
        return of("DEPT_NOT_FOUND", StrUtil.format("ID为 [{}] 的部门未找到", id));
    }

    public static DeptException nameExist(String name) {
        return of("DEPT_NAME_EXIST", StrUtil.format("名称为 [{}] 的部门已存在", name));
    }

    public static DeptException parentNotFound() {
        return of("DEPT_PARENT_NOT_FOUND", "上级部门不存在");
    }

    public static DeptException builtinDisableNotAllowed(String name) {
        return of("DEPT_BUILTIN_DISABLE_NOT_ALLOWED", StrUtil.format("[{}] 是系统内置部门，不允许禁用", name));
    }

    public static DeptException builtinParentUpdateNotAllowed(String name) {
        return of("DEPT_BUILTIN_PARENT_UPDATE_NOT_ALLOWED", StrUtil.format("[{}] 是系统内置部门，不允许变更上级部门", name));
    }

    public static DeptException hasEnabledChildren(String name) {
        return of("DEPT_HAS_ENABLED_CHILDREN", StrUtil.format("禁用 [{}] 前，请先禁用其所有下级部门", name));
    }

    public static DeptException parentDisabled(String name) {
        return of("DEPT_PARENT_DISABLED", StrUtil.format("启用 [{}] 前，请先启用其所有上级部门", name));
    }

    public static DeptException builtinDeleteNotAllowed(String name) {
        return of("DEPT_BUILTIN_DELETE_NOT_ALLOWED", StrUtil.format("所选部门 [{}] 是系统内置部门，不允许删除", name));
    }

    public static DeptException hasChildren() {
        return of("DEPT_HAS_CHILDREN", "所选部门存在下级部门，不允许删除");
    }

    public static DeptException hasUsers() {
        return of("DEPT_HAS_USERS", "所选部门存在用户关联，请解除关联后重试");
    }

    public static DeptException pathBlank() {
        return of("DEPT_PATH_BLANK", "部门路径不能为空");
    }

    public static DeptException pathFormatInvalid(String deptPath) {
        return of("DEPT_PATH_FORMAT_INVALID", StrUtil.format("部门路径格式无效：{}", deptPath));
    }

    public static DeptException pathContainsBlank(String deptPath) {
        return of("DEPT_PATH_CONTAINS_BLANK", StrUtil.format("部门路径包含空段：{}", deptPath));
    }

    public static DeptException notFoundInPath(String name, String deptPath) {
        return of("DEPT_NOT_FOUND_IN_PATH", StrUtil.format("路径 [{}] 中未找到部门 [{}]", deptPath, name));
    }

    public static DeptException notFoundByName(String deptName) {
        return of("DEPT_NOT_FOUND_BY_NAME", StrUtil.format("部门 [{}] 不存在", deptName));
    }

    public static DeptException nameDuplicate(String deptName) {
        return of("DEPT_NAME_DUPLICATE", StrUtil.format("存在多个同名部门 [{}]，请使用完整层级路径", deptName));
    }
}
