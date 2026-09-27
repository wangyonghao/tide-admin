
package top.wyhao.audit.service;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import top.wyhao.starter.web.core.model.PageQuery;
import top.wyhao.cmn.db.query.PageResult;
import top.wyhao.starter.web.log.OperationLog;
import top.wyhao.audit.model.vo.OperationLogDetailResult;
import top.wyhao.audit.model.dto.OperationLogQuery;
import top.wyhao.audit.model.vo.OperationLogResult;

/**
 * 操作日志 Service
 */
public interface OperationLogService {

    @Async
    @EventListener
    void create(OperationLog operationLog);

    /**
     * 分页查询列表
     *
     * @param query     查询条件
     * @param pageQuery 分页查询条件
     * @return 分页列表信息
     */
    PageResult<OperationLogResult> page(OperationLogQuery query, PageQuery pageQuery);

    /**
     * 查询详情
     *
     * @param id ID
     * @return 详情信息
     */
    OperationLogDetailResult detail(Long id);

    /**
     * 导出操作日志
     *
     * @param query     查询条件
     * @param response  响应对象
     */
    void export(OperationLogQuery query, HttpServletResponse response);
}
