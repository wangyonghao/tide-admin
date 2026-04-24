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

package top.wyhao.admin.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * API 文档
 */
@Configuration
public class DocConfiguration {

    /**
     * 认证 API 文档分组配置
     */
    @Bean
    public GroupedOpenApi authApi() {
        return GroupedOpenApi.builder()
            .group("auth")
            .displayName("系统认证")
            .pathsToMatch("/auth/**", "/monitor/online/**")
            .build();
    }

    /**
     * 开放 API 文档分组配置
     */
    @Bean
    public GroupedOpenApi openApi() {
        return GroupedOpenApi.builder().group("open").displayName("能力开放").pathsToMatch("/open/**").build();
    }
}
