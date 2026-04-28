
package top.wyhao.admin.system.service;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import top.wyhao.admin.system.model.query.LogQuery;
import top.wyhao.admin.system.model.vo.log.OperationLogDetailResult;
import top.wyhao.admin.system.model.vo.log.OperationLogResult;
import top.wyhao.starter.core.model.LoginUser;
import top.wyhao.starter.web.core.model.PageQuery;
import top.wyhao.starter.web.core.model.PageResult;
import top.wyhao.starter.web.log.OperationLog;

/**
 * 操作日志 Service
 */
public interface OperationLogService {

    @Async
    @EventListener
    void log(OperationLog operationLog);

    @Async
    void recordLoginLog(LoginUser loginUser);

    /**
     * 分页查询列表
     *
     * @param query     查询条件
     * @param pageQuery 分页查询条件
     * @return 分页列表信息
     */
    PageResult<OperationLogResult> page(LogQuery query, PageQuery pageQuery);

    /**
     * 查询详情
     *
     * @param id ID
     * @return 详情信息
     */
    OperationLogDetailResult get(Long id);

    /**
     * 导出登录日志
     *
     * @param query     查询条件
     * @param sortQuery 排序查询条件
     * @param response  响应对象
     */
    void exportLoginLog(LogQuery query, HttpServletResponse response);

    /**
     * 导出操作日志
     *
     * @param query     查询条件
     * @param sortQuery 排序查询条件
     * @param response  响应对象
     */
    void exportOperationLog(LogQuery query, HttpServletResponse response);
}
