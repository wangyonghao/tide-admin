package top.wyhao.admin.system.identity.app.service;

import top.wyhao.admin.system.identity.app.dto.OrgApplyRequest;
import top.wyhao.admin.system.identity.app.dto.OrgCreateRequest;
import top.wyhao.admin.system.identity.app.dto.OrgInviteRequest;
import top.wyhao.admin.system.identity.app.dto.OrgInviteResult;
import top.wyhao.admin.system.identity.app.dto.OrgResult;
import top.wyhao.admin.system.identity.app.dto.OrgSessionResult;

public interface OrgService {

    /**
     * 登录后解析应写入会话的组织 ID（主组织；平台超管无组织时回落到 defaultTenantId）。
     */
    Long resolveLoginTenantId(Long userId);

    OrgSessionResult currentSession();

    OrgResult create(OrgCreateRequest request);

    void switchOrg(Long orgId);

    void apply(OrgApplyRequest request);

    OrgInviteResult invite(OrgInviteRequest request);

    OrgInviteResult previewInvite(String rawToken);

    void acceptInvite(String rawToken);

    void rejectInvite(String rawToken);

    void reviewApplication(Long applicationId, boolean approve);
}
