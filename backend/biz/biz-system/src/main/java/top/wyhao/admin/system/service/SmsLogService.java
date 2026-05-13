
package top.wyhao.admin.system.service;

import cn.hutool.core.lang.tree.Tree;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import top.wyhao.admin.system.model.query.SmsLogQuery;
import top.wyhao.admin.system.model.bo.SmsLogReq;
import top.wyhao.admin.system.model.vo.SmsLogResult;
import top.wyhao.starter.web.core.model.PageQuery;
import top.wyhao.starter.web.core.model.PageResult;
import top.wyhao.starter.web.core.model.SortQuery;
import top.wyhao.starter.web.core.model.LabelValueResult;

import java.util.List;

/**
 * 短信日志业务接口
 *
 * @author luoqiz
 * @since 2025/03/15 22:15
 */
public interface SmsLogService {
    Long create(SmsLogReq req);

    List<Tree<Long>> tree(@Valid SmsLogQuery query, @Valid SortQuery sortQuery, boolean b);

    void update(@Valid SmsLogReq req, Long id);

    void delete(List<Long> id);

    List<LabelValueResult> dict(@Valid SmsLogQuery query, @Valid SortQuery sortQuery);

    void export(@Valid SmsLogQuery query, @Valid SortQuery sortQuery, HttpServletResponse response);

    SmsLogResult get(Long id);

    PageResult<SmsLogResult> findPage(@Valid SmsLogQuery query, @Valid PageQuery pageQuery);

    List<SmsLogResult> list(@Valid SmsLogQuery query, @Valid SortQuery sortQuery);
}