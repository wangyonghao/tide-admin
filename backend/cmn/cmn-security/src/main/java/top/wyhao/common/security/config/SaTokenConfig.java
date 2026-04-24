/*
 * Copyright (c) 2022-present wangyonghao Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package top.wyhao.common.security.config;

import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.dao.SaTokenDaoForRedisson;
import cn.dev33.satoken.jwt.StpLogicJwtForSimple;
import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpLogic;
import jakarta.annotation.PostConstruct;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import top.wyhao.starter.core.auth.PermissionProvider;
import top.wyhao.common.security.handler.SaTokenExceptionHandler;

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
        config.setIsShare(false); // 在多人登录同一账号时，是否共用一个 token （为 true 时所有登录共用一个 token，为 false 时每次登录新建一个 token）
        config.setTokenStyle("uuid"); // token 风格
        config.setIsLog(false); // 是否输出操作日志
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

    @PostConstruct
    public void postConstruct() {
        log.debug("[wyhao Starter] - 'SaToken' configured.");
    }
}
