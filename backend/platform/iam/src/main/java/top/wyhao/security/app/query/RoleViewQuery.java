package top.wyhao.security.app.query;

import top.wyhao.cmn.db.query.PageResult;
import top.wyhao.security.adapter.web.dto.RoleMemberQuery;
import top.wyhao.security.adapter.web.dto.RoleQuery;
import top.wyhao.security.adapter.web.vo.RoleMemberResult;
import top.wyhao.security.domain.model.SysRole;

import java.util.List;

/**
 * 角色列表读模型。分页 SQL 直接映射到接口视图，因此端口放在应用层。
 */
public interface RoleViewQuery {

    PageResult<SysRole> page(RoleQuery query, long page, long pageSize);

    List<SysRole> list(RoleQuery query);

    List<RoleMemberResult> pageMembers(Long roleId, RoleMemberQuery query, long page, long pageSize);
}
