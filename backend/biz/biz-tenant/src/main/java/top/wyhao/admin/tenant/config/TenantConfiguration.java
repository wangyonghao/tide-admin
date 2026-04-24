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

package top.wyhao.admin.tenant.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.wyhao.admin.tenant.service.TenantService;
import top.wyhao.starter.tenant.annotation.ConditionalOnEnabledTenant;
import top.wyhao.starter.tenant.config.TenantProperties;
import top.wyhao.starter.tenant.config.TenantProvider;

/**
 * 租户配置
 *
 * @author Charles7c
 * @since 2025/7/12 13:30
 */
@Configuration
public class TenantConfiguration {

    /**
     * 租户扩展配置属性
     */
    @Bean
    public TenantProperties tenantProperties() {
        return new TenantProperties();
    }

    /**
     * 租户提供者
     */
    @Bean
    @ConditionalOnEnabledTenant
    public TenantProvider tenantProvider(TenantProperties tenantProperties,
                                         TenantService tenantService) {
        return new DefaultTenantProvider(tenantProperties, tenantService);
    }

    /**
     * API 文档分组配置
     */
    @Bean
    public GroupedOpenApi tenantApi() {
        return GroupedOpenApi.builder().group("tenant").displayName("租户管理").pathsToMatch("/tenant/**").build();
    }
}
