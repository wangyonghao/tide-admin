package top.wyhao.common.security.context;


import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import top.wyhao.starter.core.enums.RoleCodeEnum;
import top.wyhao.starter.core.model.LoginUser;
import top.wyhao.starter.core.spi.UserContext;

public class SaUserContext implements UserContext {
    public static final String LOGIN_USER_KEY = "loginUser";

    @Override
    public Long getUserId() {
        return StpUtil.getLoginIdAsLong();
    }

    @Override
    public String getToken() {
        return StpUtil.getTokenValue();
    }

    @Override
    public LoginUser getUser() {
        SaSession session = StpUtil.getTokenSession();
        return (LoginUser) session.get(LOGIN_USER_KEY);
    }

    @Override
    public boolean isSuperadmin() {
        return StpUtil.hasRole(RoleCodeEnum.SUPER_ADMIN.getCode());
    }
}
