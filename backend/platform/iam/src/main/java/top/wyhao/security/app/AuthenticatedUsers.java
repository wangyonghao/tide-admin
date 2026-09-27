package top.wyhao.security.app;

import top.wyhao.cmn.core.model.LoginUser;
import top.wyhao.identity.client.CredentialUser;

/**
 * 把身份凭证整理成登录会话里的用户。
 */
public final class AuthenticatedUsers {

    private AuthenticatedUsers() {
    }

    public static LoginUser from(CredentialUser user) {
        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(user.id());
        loginUser.setUsername(user.username());
        loginUser.setDeptId(user.deptId());
        loginUser.setPwdResetTime(user.pwdUpdateTime());
        return loginUser;
    }
}
