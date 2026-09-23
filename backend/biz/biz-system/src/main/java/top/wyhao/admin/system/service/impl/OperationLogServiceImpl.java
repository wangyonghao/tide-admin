
package top.wyhao.admin.system.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import top.wyhao.admin.system.assembler.OperationLogAssembler;
import top.wyhao.admin.system.entity.SysOperationLog;
import top.wyhao.admin.system.mapper.SysOperationLogMapper;
import top.wyhao.admin.system.service.OperationLogService;
import top.wyhao.cmn.db.query.QueryWrapperBuilder;
import top.wyhao.admin.system.exception.OperationLogException;
import top.wyhao.starter.core.util.validation.Check;
import top.wyhao.starter.excel.util.ExcelUtils;
import top.wyhao.starter.web.core.model.PageQuery;
import top.wyhao.starter.web.core.model.PageResult;
import top.wyhao.starter.web.log.OperationLog;

import java.util.List;
import top.wyhao.admin.system.model.vo.OperationLogDetailResult;
import top.wyhao.admin.system.model.vo.OperationLogExcelResult;
import top.wyhao.admin.system.model.dto.OperationLogQuery;
import top.wyhao.admin.system.model.vo.OperationLogResult;

/**
 * 操作日志服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OperationLogServiceImpl implements OperationLogService {

    private final SysOperationLogMapper operationLogMapper;
    private final OperationLogAssembler operationLogAssembler;

    /**
     * 异步记录操作日志
     *
     * @param operationLog 操作日志事件
     */
    @Async
    @EventListener
    @Override
    public void create(OperationLog operationLog) {
        SysOperationLog operLog = operationLogAssembler.toEntity(operationLog);
        operationLogMapper.insert(operLog);
    }


    @Override
    public PageResult<OperationLogResult> page(OperationLogQuery query, PageQuery pageQuery) {
        IPage<SysOperationLog> page = operationLogMapper.selectPage(new Page<>(pageQuery.getPage(), pageQuery.getPageSize()),
                QueryWrapperBuilder.build(query,SysOperationLog.class));
        return PageResult.build(page, operationLogAssembler::toResultList);
    }

    @Override
    public OperationLogDetailResult detail(Long id) {
        SysOperationLog sysOperationLog = this.require(id);
        Check.throwIfNotExists(sysOperationLog, "LogDO", "ID", id);
        return operationLogAssembler.toDetail(sysOperationLog);
    }

    @Override
    public void export(OperationLogQuery query, HttpServletResponse response) {
        List<OperationLogExcelResult> list = operationLogAssembler.toExcelList(this.list(query));
        ExcelUtils.export(list, "导出操作日志数据", OperationLogExcelResult.class, response);
    }

    /**
     * 查询列表
     *
     * @param query 查询条件
     * @return 列表信息
     */
    private List<OperationLogExcelResult> list(OperationLogQuery query) {
        return operationLogMapper.selectLogList(QueryWrapperBuilder.build(query, SysOperationLog.class));
    }

    private SysOperationLog require(Long id) {
        SysOperationLog operationLog = operationLogMapper.selectById(id);
        if (operationLog == null) {
            throw OperationLogException.notFound();
        }
        return operationLog;
    }
}
