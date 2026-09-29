package top.wyhao.common.satoken.config;

import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.dao.SaTokenDaoForRedisson;
import cn.dev33.satoken.jwt.StpLogicJwtForSimple;
import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpLogic;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import top.wyhao.common.satoken.handler.SaTokenExceptionHandler;
import top.wyhao.security.client.PermissionApi;

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
    public StpInterface stpInterface(PermissionApi permissionProvider) {
        return new StpInterfaceImpl(permissionProvider);
    }

    @Autowired
    public void configSaToken(cn.dev33.satoken.config.SaTokenConfig config) {
        config.setTokenPrefix("Bearer");
        config.setIsReadBody(true);
        config.setIsReadHeader(true);
        config.setIsReadCookie(false);
        config.setTimeout(30 * 24 * 60 * 60);
        config.setActiveTimeout(-1);
        config.setIsConcurrent(false);
        config.setIsShare(false);
        config.setTokenStyle("uuid");
        config.setIsLog(false);
        log.debug("[cmn-security] - 'SaToken' configured.");
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
}
