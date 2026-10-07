
package top.wyhao.department.app.service;

import jakarta.servlet.http.HttpServletResponse;
import top.wyhao.department.domain.model.SysDepartment;

import java.util.List;
import top.wyhao.department.adapter.web.dto.DepartmentQuery;
import top.wyhao.department.adapter.web.dto.DepartmentRequest;
import top.wyhao.department.adapter.web.vo.DepartmentResult;

/**
 * 部门业务接口
 */
public interface DepartmentService{

    /**
     * 查询列表
     *
     * @param query     查询条件
     * @return 列表信息
     */
    List<DepartmentResult> list(DepartmentQuery query);

    /**
     * 查询部门树
     *
     * @param query     查询条件
     * @return 树列表信息
     */
    List<DepartmentResult> tree(DepartmentQuery query);

    /**
     * 查询详情
     *
     * @param id ID
     * @return 详情信息
     */
    DepartmentResult get(Long id);

    /**
     * 创建
     *
     * @param req 创建请求参数
     * @return 自增 ID
     */
    Long create(DepartmentRequest req);

    /**
     * 修改
     *
     * @param req 修改请求参数
     * @param id  ID
     */
    void update(DepartmentRequest req, Long id);

    /**
     * 删除
     *
     * @param ids ID 列表
     */
    void delete(List<Long> ids);

    /**
     * 导出
     *
     * @param query     查询条件
     * @param response  响应对象
     */
    void export(DepartmentQuery query, HttpServletResponse response);

    /**
     * 查询子部门列表
     *
     * @param id ID
     * @return 子部门列表
     */
    List<SysDepartment> listChildren(Long id);

    SysDepartment getById(Long departmentId);
}
