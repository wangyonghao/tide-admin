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

import lombok.RequiredArgsConstructor;
import top.wyhao.admin.tenant.service.TenantService;
import top.wyhao.starter.tenant.config.TenantProperties;
import top.wyhao.starter.tenant.config.TenantProvider;
import top.wyhao.starter.tenant.context.TenantContext;

/**
 * 默认租户提供者
 */
@RequiredArgsConstructor
public class DefaultTenantProvider implements TenantProvider {

    private final TenantProperties tenantProperties;
    private final TenantService tenantService;

    @Override
    public TenantContext getByTenantId(String tenantIdAsString, boolean checkStatus) {
        TenantContext context = new TenantContext();
        Long defaultTenantId = tenantProperties.getDefaultTenantId();
        context.setTenantId(defaultTenantId);
        // 默认租户
        if (defaultTenantId.toString().equals(tenantIdAsString)) {
            return context;
        }
        Long tenantId = Long.parseLong(tenantIdAsString);
        // 检查租户状态
        if (checkStatus) {
            tenantService.checkStatus(tenantId);
        }
        context.setTenantId(tenantId);
        return context;
    }
}
