
package top.wyhao.admin.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import top.wyhao.admin.system.entity.SysOperationLog;
import top.wyhao.admin.system.mapper.SysOperationLogMapper;
import top.wyhao.admin.system.model.OperationLogModel;
import top.wyhao.admin.system.service.OperationLogService;
import top.wyhao.cmn.db.util.WrapperUtil;
import top.wyhao.starter.core.exception.BizException;
import top.wyhao.starter.core.util.validation.Check;
import top.wyhao.starter.excel.util.ExcelUtils;
import top.wyhao.starter.web.core.model.PageQuery;
import top.wyhao.starter.web.core.model.PageResult;
import top.wyhao.starter.web.log.OperationLog;

import java.util.List;

/**
 * 操作日志服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OperationLogServiceImpl implements OperationLogService {

    private final SysOperationLogMapper operationLogMapper;

    /**
     * 异步记录操作日志
     *
     * @param operationLog 操作日志事件
     */
    @Async
    @EventListener
    @Override
    public void create(OperationLog operationLog) {
        SysOperationLog operLog = BeanUtil.toBean(operationLog, SysOperationLog.class);
        operationLogMapper.insert(operLog);
    }


    @Override
    public PageResult<OperationLogModel.Result> page(OperationLogModel.LogQuery query, PageQuery pageQuery) {
        QueryWrapper<SysOperationLog> queryWrapper = WrapperUtil.build(query);
        WrapperUtil.applySort(queryWrapper, WrapperUtil.parseSort(query.sort()), SysOperationLog.class);
        IPage<SysOperationLog> page = operationLogMapper.selectPage(new Page<>(pageQuery.getPage(), pageQuery.getSize()), queryWrapper);
        return PageResult.build(page, OperationLogModel.Result.class);
    }

    @Override
    public OperationLogModel.Detail detail(Long id) {
        SysOperationLog sysOperationLog = this.require(id);
        Check.throwIfNotExists(sysOperationLog, "LogDO", "ID", id);
        return BeanUtil.copyProperties(sysOperationLog, OperationLogModel.Detail.class);
    }

    @Override
    public void export(OperationLogModel.LogQuery query, HttpServletResponse response) {
        List<OperationLogModel.Excel> list = BeanUtil.copyToList(this.list(query), OperationLogModel.Excel.class);
        ExcelUtils.export(list, "导出操作日志数据", OperationLogModel.Excel.class, response);
    }

    /**
     * 查询列表
     *
     * @param query 查询条件
     * @return 列表信息
     */
    private List<OperationLogModel> list(OperationLogModel.LogQuery query) {
        QueryWrapper<SysOperationLog> queryWrapper = WrapperUtil.build(query);
        WrapperUtil.applySort(queryWrapper, WrapperUtil.parseSort(query.sort()), SysOperationLog.class);
        return operationLogMapper.selectLogList(queryWrapper);
    }

    private SysOperationLog require(Long id) {
        SysOperationLog operationLog = operationLogMapper.selectById(id);
        if (operationLog == null) {
            throw new BizException("OPERATIONLOG_NOT_FOUND", "操作日志不存在");
        }
        return operationLog;
    }
}
