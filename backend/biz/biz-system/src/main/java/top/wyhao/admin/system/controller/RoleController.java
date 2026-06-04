package top.wyhao.admin.system.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import top.wyhao.admin.system.model.bo.RolePermissionUpdateRequest;
import top.wyhao.admin.system.model.RoleModel;
import top.wyhao.admin.system.model.result.MenuTreeVO;
import top.wyhao.admin.system.model.RoleUserModel;
import top.wyhao.admin.system.service.MenuService;
import top.wyhao.admin.system.service.RoleService;
import top.wyhao.starter.core.model.Result;
import top.wyhao.starter.web.core.model.PageQuery;
import top.wyhao.starter.web.core.model.PageResult;
import top.wyhao.starter.web.core.model.IdResult;

import java.util.List;

/**
 * 角色管理 API
 *

 * @since 2023/2/18 14:29
 */
@Tag(name = "角色管理 API")
@RestController
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;
    private final MenuService menuService;

    /**
     * 分页查询列表
     *
     * @param query     查询条件
     * @param pageQuery 分页查询条件
     * @return 分页信息
     */
    @Operation(summary = "分页查询列表", description = "分页查询列表")
    @GetMapping("/system/role")
    public PageResult<RoleModel.Result> page(@Valid RoleModel.Query query, @Valid PageQuery pageQuery) {
        return roleService.page(query, pageQuery);
    }

    /**
     * 查询列表
     *
     * @param query     查询条件
     * @return 列表信息
     */
    @Operation(summary = "查询列表", description = "查询列表")
    @GetMapping("/system/role/list")
    public List<RoleModel.Result> list(@Valid RoleModel.Query query) {
        return roleService.list(query);
    }
    /**
     * 查询详情
     *
     * @param id ID
     * @return 详情信息
     */
    @Operation(summary = "查询详情", description = "查询详情")
    @Parameter(name = "id", description = "ID", example = "1", in = ParameterIn.PATH)
    @GetMapping("/system/role/{id}")
    public RoleModel.Detail detail(@PathVariable Long id) {
        return roleService.detail(id);
    }

    /**
     * 创建
     *
     * @param req 创建请求参数
     * @return ID
     */
    @Operation(summary = "创建数据", description = "创建数据")
    @PostMapping("/system/role")
    public Result<IdResult<Long>> create(@RequestBody @Valid RoleModel.Request req) {
        return Result.ok(new IdResult<>(roleService.create(req)));
    }

    /**
     * 修改
     *
     * @param req 修改请求参数
     * @param id  ID
     */
    @Operation(summary = "修改数据", description = "修改数据")
    @Parameter(name = "id", description = "ID", example = "1", in = ParameterIn.PATH)
    @PutMapping("/system/role/{id}")
    public void update(@RequestBody @Valid RoleModel.Request req, @PathVariable Long id) {
        roleService.update(req, id);
    }

    /**
     * 删除
     *
     * @param id ID
     */
    @Operation(summary = "删除数据", description = "删除数据")
    @Parameter(name = "id", description = "ID", example = "1", in = ParameterIn.PATH)
    @DeleteMapping("/system/role/{id}")
    public void delete(@PathVariable Long id) {
        roleService.delete(id);
    }

    /**
     * 导出
     *
     * @param query    查询条件
     * @param response 响应对象
     */
    @Operation(summary = "导出数据", description = "导出数据")
    @GetMapping("/system/role/export")
    public void export(@Valid RoleModel.Query query, HttpServletResponse response) {
        roleService.export(query, response);
    }

    /**
     * 查询角色下的所有用户
     *
     * @param id 角色 ID
     * @return 用户列表
     */
    @Operation(summary = "查询角色成员", description = "查询角色成员")
    @Parameter(name = "id", description = "角色 ID", example = "1", in = ParameterIn.PATH)
    @GetMapping("/system/role/{id}/user")
    public List<RoleUserModel> pageMember(@PathVariable Long id, RoleUserModel.Query query, PageQuery pageQuery) {
        return roleService.pageMember(id, query, pageQuery);
    }

    @Operation(summary = "删除角色成员", description = "删除角色成员")
    @Parameter(name = "id", description = "角色 ID", example = "1", in = ParameterIn.PATH)
    @DeleteMapping("/system/role/{id}/user")
    public void deleteMember(@PathVariable Long id, @Validated @RequestBody RoleUserModel.Remove request) {
        roleService.deleteMember(id, request.userIds());
    }



    /**
     * 查询权限树
     *
     * @return 权限树
     */
    @Operation(summary = "查询权限树", description = "查询权限树")
    @GetMapping("/system/permission/tree")
    public List<MenuTreeVO> treePermission() {
        return menuService.tree(null);
    }

    /**
     * 更新角色权限
     *
     * @return
     */
    @Operation(summary = "查询权限树", description = "查询权限树")
    @Parameter(name = "id", description = "角色 ID", example = "1", in = ParameterIn.PATH)
    @PutMapping("/system/role/{id}/permission")
    public void updatePermission(@PathVariable Long id, @RequestBody @Valid RolePermissionUpdateRequest req) {
        roleService.updatePermission(id, req);
    }


}