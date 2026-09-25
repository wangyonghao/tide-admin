package top.wyhao.identity.app.query;

import top.wyhao.cmn.db.query.PageParam;
import top.wyhao.cmn.db.query.PageResult;
import top.wyhao.identity.adapter.web.dto.UserQuery;
import top.wyhao.identity.adapter.web.vo.UserDetail;
import top.wyhao.identity.adapter.web.vo.UserResult;

import java.util.List;

/**
 * 用户列表读模型。分页和导出 SQL 直接映射到接口视图，因此端口放在应用层。
 */
public interface UserViewQuery {

    PageResult<UserResult> page(UserQuery query, PageParam pageParam);

    List<UserDetail> list(UserQuery query);
}
