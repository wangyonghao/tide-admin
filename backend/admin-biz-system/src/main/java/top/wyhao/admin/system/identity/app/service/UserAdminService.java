package top.wyhao.admin.system.identity.app.service;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;
import top.wyhao.mybatis.query.PageParam;
import top.wyhao.mybatis.query.PageResult;
import top.wyhao.admin.system.identity.adapter.web.dto.UserImportRequest;
import top.wyhao.admin.system.identity.adapter.web.dto.UserQuery;
import top.wyhao.admin.system.identity.adapter.web.dto.UserRequest;
import top.wyhao.admin.system.identity.adapter.web.dto.UserRoleUpdateReq;
import top.wyhao.admin.system.identity.adapter.web.vo.UserDetail;
import top.wyhao.admin.system.identity.adapter.web.vo.UserImportParseResp;
import top.wyhao.admin.system.identity.adapter.web.vo.UserImportResp;
import top.wyhao.admin.system.identity.adapter.web.vo.UserResult;
import top.wyhao.admin.system.identity.domain.model.SysUser;
import top.wyhao.web.core.model.LabelValueResult;

import java.util.List;

/**
 * 管理员对用户的管理操作。
 */
public interface UserAdminService {

    UserImportParseResp parseImport(MultipartFile file);

    UserImportResp importUser(UserImportRequest req);

    void updateRole(UserRoleUpdateReq updateReq, Long id);

    SysUser getByUsername(String username);

    SysUser getByPhone(String phone);

    SysUser getByEmail(String email);

    Long countByDepartmentIds(List<Long> departmentIds);

    UserDetail detail(Long id);

    PageResult<UserResult> page(UserQuery query, PageParam pageParam);

    Long save(SysUser user);

    Long create(@Valid UserRequest req);

    void export(@Valid UserQuery query, HttpServletResponse response);

    void update(Long id, @Valid UserRequest req);

    void delete(List<Long> ids);

    List<LabelValueResult<Long>> dict(UserQuery query);
}
