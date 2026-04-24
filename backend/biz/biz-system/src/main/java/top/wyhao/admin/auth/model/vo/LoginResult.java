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

package top.wyhao.admin.auth.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 登录响应参数
 *
 * @author Charles7c
 * @since 2022/12/21 20:42
 */
@Data
@Builder
@Schema(description = "登录响应参数")
public class LoginResult implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 响应代码
     */
    @Schema(description = "响应代码", example = "PASSWORD_EXPIRED")
    private String code;

    /**
     * 令牌
     */
    @Schema(description = "令牌", example = "ey****J9.ey****fQ.KU****Z8")
    private String token;

    /**
     * 租户 ID
     */
    @Schema(description = "租户 ID", example = "0")
    private Long tenantId;
}
