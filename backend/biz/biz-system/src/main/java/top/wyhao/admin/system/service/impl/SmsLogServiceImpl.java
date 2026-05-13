
package top.wyhao.admin.system.service.impl;

import cn.hutool.core.lang.tree.Tree;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;
import top.wyhao.admin.system.model.query.SmsLogQuery;
import top.wyhao.admin.system.model.bo.SmsLogReq;
import top.wyhao.admin.system.model.vo.SmsLogResult;
import top.wyhao.admin.system.service.SmsLogService;
import top.wyhao.starter.web.core.model.PageQuery;
import top.wyhao.starter.web.core.model.PageResult;
import top.wyhao.starter.web.core.model.SortQuery;
import top.wyhao.starter.web.core.model.LabelValueResult;

import java.util.List;

/**
 * 短信日志业务实现
 *
 * @author luoqiz
 * @since 2025/03/15 22:15
 */
@Service
public class SmsLogServiceImpl implements SmsLogService {
    @Override
    public Long create(SmsLogReq req) {
        return 0L;
    }

    @Override
    public List<Tree<Long>> tree(SmsLogQuery query, SortQuery sortQuery, boolean b) {
        return List.of();
    }

    @Override
    public void update(SmsLogReq req, Long id) {

    }

    @Override
    public void delete(List<Long> id) {

    }

    @Override
    public List<LabelValueResult> dict(SmsLogQuery query, SortQuery sortQuery) {
        return List.of();
    }

    @Override
    public void export(SmsLogQuery query, SortQuery sortQuery, HttpServletResponse response) {

    }

    @Override
    public SmsLogResult get(Long id) {
        return null;
    }

    @Override
    public PageResult<SmsLogResult> findPage(SmsLogQuery query, PageQuery pageQuery) {
        return null;
    }

    @Override
    public List<SmsLogResult> list(SmsLogQuery query, SortQuery sortQuery) {
        return List.of();
    }
}