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

package top.wyhao.starter.tenant.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import top.wyhao.starter.core.constant.PropertiesConstants;

/**
 * 租户配置属性
 *
 * @author Charles7c
 * @since 2.7.0
 */
@ConfigurationProperties(PropertiesConstants.TENANT)
public class TenantProperties {

    /**
     * 是否启用
     */
    private boolean enabled = true;

    /**
     * 默认租户 ID（默认：0）
     */
    private Long defaultTenantId = 0L;
    /**
     * 请求头中租户 ID 键名
     */
    private String tenantIdHeader = "X-Tenant-Id";

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Long getDefaultTenantId() {
        return defaultTenantId;
    }

    public void setDefaultTenantId(Long defaultTenantId) {
        this.defaultTenantId = defaultTenantId;
    }

    public String getTenantIdHeader() {
        return tenantIdHeader;
    }

    public void setTenantIdHeader(String tenantIdHeader) {
        this.tenantIdHeader = tenantIdHeader;
    }
}
