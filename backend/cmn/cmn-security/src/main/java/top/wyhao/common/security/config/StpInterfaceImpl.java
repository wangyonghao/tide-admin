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

import cn.dev33.satoken.stp.StpInterface;
import lombok.RequiredArgsConstructor;
import top.wyhao.starter.core.auth.PermissionProvider;

import java.util.List;

/**
 * 获取用户角色和权限
 *
 * @author wyh
 */
@RequiredArgsConstructor
public class StpInterfaceImpl implements StpInterface {

    private final PermissionProvider permissionProvider;

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        return permissionProvider.findUserPermissions(Long.parseLong(loginId.toString()));
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        return permissionProvider.findUserRoles(Long.parseLong(loginId.toString()));
    }
}
