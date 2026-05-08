
package top.wyhao.admin.system.service.impl;

import cn.crane4j.annotation.AutoOperate;
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
import top.wyhao.admin.system.mapper.OperationLogMapper;
import top.wyhao.admin.system.model.entity.SysOperationLog;
import top.wyhao.admin.system.model.query.LogQuery;
import top.wyhao.admin.system.model.vo.log.OperationLogDetailResult;
import top.wyhao.admin.system.model.vo.log.OperationLogResult;
import top.wyhao.admin.system.model.vo.log.LoginLogExportResult;
import top.wyhao.admin.system.model.vo.log.OperationLogExportResp;
import top.wyhao.admin.system.service.OperationLogService;
import top.wyhao.starter.core.model.LoginUser;
import top.wyhao.starter.core.util.validation.BizAssert;
import top.wyhao.starter.data.util.QueryWrapperUtil;
import top.wyhao.starter.excel.util.ExcelUtils;
import top.wyhao.starter.web.ServletUtils;
import top.wyhao.starter.web.core.model.PageQuery;
import top.wyhao.starter.web.core.model.PageResult;
import top.wyhao.starter.web.log.OperationLog;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 操作日志服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OperationLogServiceImpl implements OperationLogService {

    private final OperationLogMapper operationLogMapper;

    /**
     * 记录操作日志
     *
     * @param operationLog 操作日志事件
     */
    @Async
    @EventListener
    @Override
    public void log(OperationLog operationLog) {
        SysOperationLog operLog = BeanUtil.toBean(operationLog, SysOperationLog.class);
        operationLogMapper.insert(operLog);
    }

    @Override
    public void recordLoginLog(LoginUser loginUser) {
        ServletUtils.RequestMeta requestMeta = ServletUtils.getRequestMeta();
        // 记录登录日志
        SysOperationLog operationLog = new SysOperationLog();
        operationLog.setOperation("login");
        operationLog.setObjectType("user");
        operationLog.setObjectId(loginUser.getUserId());
        operationLog.setOperatorId(loginUser.getUserId());
        operationLog.setOperatorName(loginUser.getUsername());
        operationLog.setOperatorIp(requestMeta.ip());
        operationLog.setOperateTime(LocalDateTime.now());
        operationLog.setRemark(requestMeta.address() + ", " + requestMeta.browser() + ", " + requestMeta.os()); // 登录设备信息
        operationLogMapper.insert(operationLog);
    }

    @Override
    public PageResult<OperationLogResult> page(LogQuery query, PageQuery pageQuery) {
        QueryWrapper<SysOperationLog> queryWrapper = QueryWrapperUtil.build(query);
        QueryWrapperUtil.applySort(queryWrapper, query.getSort(), SysOperationLog.class);
        IPage<SysOperationLog> page = operationLogMapper.selectPage(new Page<>(pageQuery.getPage(), pageQuery.getSize()),queryWrapper);
        return PageResult.build(page, OperationLogResult.class);
    }

    @Override
    @AutoOperate(type = OperationLogDetailResult.class)
    public OperationLogDetailResult get(Long id) {
        SysOperationLog sysOperationLog = operationLogMapper.selectById(id);
        BizAssert.throwIfNotExists(sysOperationLog, "LogDO", "ID", id);
        return BeanUtil.copyProperties(sysOperationLog, OperationLogDetailResult.class);
    }

    @Override
    public void exportLoginLog(LogQuery query, HttpServletResponse response) {
        List<LoginLogExportResult> list = BeanUtil.copyToList(this.list(query), LoginLogExportResult.class);
        ExcelUtils.export(list, "导出登录日志数据", LoginLogExportResult.class, response);
    }

    @Override
    public void exportOperationLog(LogQuery query, HttpServletResponse response) {
        List<OperationLogExportResp> list = BeanUtil.copyToList(this.list(query), OperationLogExportResp.class);
        ExcelUtils.export(list, "导出操作日志数据", OperationLogExportResp.class, response);
    }

    /**
     * 查询列表
     *
     * @param query     查询条件
     * @return 列表信息
     */
    private List<OperationLogResult> list(LogQuery query) {
        QueryWrapper<SysOperationLog> queryWrapper = QueryWrapperUtil.build(query);
        QueryWrapperUtil.applySort(queryWrapper, query.getSort(), SysOperationLog.class);
        return operationLogMapper.selectLogList(queryWrapper);
    }
}
