package top.wyhao.security.app.assembler;

import cn.hutool.core.collection.CollUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import top.wyhao.cmn.core.enums.DataScopeEnum;
import top.wyhao.cmn.core.model.RoleDataScope;
import top.wyhao.identity.client.CredentialUser;
import top.wyhao.identity.client.LoginUser;
import top.wyhao.organization.client.DeptApi;
import top.wyhao.security.domain.gateway.RoleDeptRepository;
import top.wyhao.security.domain.gateway.RoleRepository;
import top.wyhao.security.domain.model.SysRole;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 将身份凭证组装为登录会话用户（含角色数据权限范围）。
 */
@Component
@RequiredArgsConstructor
public class LoginUserAssembler {

    private final RoleRepository roleRepository;
    private final RoleDeptRepository roleDeptRepository;
    private final DeptApi deptApi;

    public LoginUser assemble(CredentialUser user) {
        return assemble(user, null);
    }

    public LoginUser assemble(CredentialUser user, String deviceType) {
        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(user.id());
        loginUser.setUsername(user.username());
        loginUser.setDeptId(user.deptId());
        loginUser.setPwdResetTime(user.pwdUpdateTime());
        loginUser.setDeviceType(deviceType);
        loginUser.setRoleDataScopes(buildRoleDataScopes(user));
        return loginUser;
    }

    private List<RoleDataScope> buildRoleDataScopes(CredentialUser user) {
        List<SysRole> roles = roleRepository.selectRolesByUserId(user.id());
        if (CollUtil.isEmpty(roles)) {
            return Collections.emptyList();
        }
        Long deptId = user.deptId();
        List<Long> deptAndDescendants = null;
        List<RoleDataScope> scopes = new ArrayList<>(roles.size());
        for (SysRole role : roles) {
            DataScopeEnum dataScope = role.getDataScope();
            if (DataScopeEnum.DEPT_AND_CHILD.equals(dataScope) && deptAndDescendants == null) {
                deptAndDescendants = deptId == null
                        ? Collections.emptyList()
                        : deptApi.listSelfAndDescendantIds(deptId);
            }
            RoleDataScope scope = new RoleDataScope();
            scope.setId(role.getId());
            scope.setCode(role.getCode());
            scope.setDataScope(dataScope);
            scope.setVisibleDeptIds(resolveVisibleDeptIds(dataScope, role.getId(), deptId, deptAndDescendants));
            scopes.add(scope);
        }
        return scopes;
    }

    private List<Long> resolveVisibleDeptIds(DataScopeEnum dataScope,
                                             Long roleId,
                                             Long userDeptId,
                                             List<Long> deptAndDescendants) {
        if (dataScope == null) {
            return null;
        }
        return switch (dataScope) {
            case DEPT_AND_CHILD -> CollUtil.emptyIfNull(deptAndDescendants);
            case DEPT -> userDeptId == null ? Collections.emptyList() : List.of(userDeptId);
            case CUSTOM_DEPT -> roleDeptRepository.listDeptIdsByRoleId(roleId);
            case ALL, SELF -> null;
        };
    }
}
