package top.wyhao.identity.domain.gateway;

import top.wyhao.identity.adapter.web.result.config.LoginConfigVO;
import top.wyhao.identity.adapter.web.result.config.SecurityConfigVO;
import top.wyhao.identity.adapter.web.result.config.SiteConfigVO;

/**
 * 登录/站点/安全配置 API（由系统管理模块实现）
 */
public interface SystemConfigApi {

    LoginConfigVO getLoginConfig();

    SecurityConfigVO getSecurityConfig();

    /**
     * 密码过期天数。领域策略只需要这个数值，不必依赖安全配置视图。
     */
    default Integer passwordExpireDays() {
        return getSecurityConfig().getPasswordExpireDays();
    }

    SiteConfigVO getSiteConfig();
}
