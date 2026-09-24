package top.wyhao.identity.config;

import top.wyhao.identity.model.result.config.LoginConfigVO;
import top.wyhao.identity.model.result.config.SecurityConfigVO;
import top.wyhao.identity.model.result.config.SiteConfigVO;

/**
 * 登录/站点/安全配置 API（由系统管理模块实现）
 */
public interface SystemConfigApi {

    LoginConfigVO getLoginConfig();

    SecurityConfigVO getSecurityConfig();

    SiteConfigVO getSiteConfig();
}
