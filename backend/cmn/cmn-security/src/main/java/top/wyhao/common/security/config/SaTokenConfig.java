
package top.wyhao.common.security.config;

import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.dao.SaTokenDaoForRedisson;
import cn.dev33.satoken.jwt.StpLogicJwtForSimple;
import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpLogic;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import top.wyhao.common.security.context.SaUserContext;
import top.wyhao.starter.core.UserContextHolder;
import top.wyhao.starter.core.spi.PermissionProvider;
import top.wyhao.common.security.handler.SaTokenExceptionHandler;
import top.wyhao.starter.core.spi.UserContext;

/**
 * Sa-Token 自动配置
 */
@AutoConfiguration
public class SaTokenConfig {

    private static final Logger log = LoggerFactory.getLogger(SaTokenConfig.class);

    /**
     * 权限获取实现类
     */
    @Bean
    @ConditionalOnMissingBean
    public StpInterface stpInterface(PermissionProvider permissionProvider) {
        return new StpInterfaceImpl(permissionProvider);
    }

    @Autowired
    public void configSaToken(cn.dev33.satoken.config.SaTokenConfig config) {
        config.setTokenPrefix("Bearer");   // token 前缀（例如填写 Bearer，实际传参 token 键: Bearer xxxx-xxxx-xxxx-xxxx）
        config.setIsReadBody(true);        // 是否尝试从 请求体 里读取 Token
        config.setIsReadHeader(true); // 是否尝试从 请求头 里读取 Token
        // 是否尝试从 cookie 里读取 Token（此值为 false 后，StpUtil.login(id) 登录时也不会再往前端注入 Cookie，适合前后端分离模式）
        config.setIsReadCookie(false);
        config.setTimeout(30 * 24 * 60 * 60); // token 有效期（单位：秒），默认30天，-1代表永不过期
        config.setActiveTimeout(-1); // token 最低活跃频率（单位：秒），如果 token 超过此时间没有访问系统就会被冻结，默认-1 代表不限制，永不冻结
        config.setIsConcurrent(false); // 是否允许同一账号多地同时登录（为 true 时允许一起登录，为 false 时新登录挤掉旧登录）
        config.setIsShare(false); // 在多端登录同一账号时，是否共用一个 token （为 true 时所有登录共用一个 token，为 false 时每次登录新建一个 token）
        config.setTokenStyle("uuid"); // token 风格
        config.setIsLog(false); // 是否输出日志
    }

    /**
     * 整合 JWT（简单模式）
     */
    @Bean
    @ConditionalOnMissingBean
    public StpLogic stpLogic() {
        return new StpLogicJwtForSimple();
    }

    /**
     * Token 持久层配置
     */
    @Bean
    @ConditionalOnMissingBean
    public SaTokenDao saTokenDao(RedissonClient redissonClient) {
        return new SaTokenDaoForRedisson(redissonClient);
    }

    /**
     * 异常处理器
     */
    @Bean
    public SaTokenExceptionHandler saTokenExceptionHandler() {
        return new SaTokenExceptionHandler();
    }


    @Bean
    public SaUserContext loginUserContext() {
        return new SaUserContext();
    }


    @Resource
    UserContext loginUserContext;

    @PostConstruct
    public void postConstruct() {
        // 为 LoginUserHolder 注入用户上下文， 以供业务模块获取登录用户信息
        UserContextHolder.setLoginUserContext(loginUserContext);

        log.debug("[cmn-security] - 'SaToken' configured.");
    }
}
