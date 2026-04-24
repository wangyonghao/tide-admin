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

package top.wyhao.admin.auth.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import top.wyhao.common.security.util.LoginUtil;
import top.wyhao.starter.core.model.LoginUser;
import top.wyhao.starter.core.util.validation.BizAssert;
import top.wyhao.starter.web.core.model.PageQuery;
import top.wyhao.starter.web.core.model.PageResult;

import java.util.List;

/**
 * 在线用户 API
 *
 * @author Charles7c
 * @since 2023/1/20 21:51
 */
@Tag(name = "在线用户 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/monitor/online")
public class OnlineUserController {
    @Operation(summary = "分页查询列表", description = "分页查询列表")
    @SaCheckPermission("monitor:online:list")
    @GetMapping
    public PageResult<LoginUser> page(@Valid String keyword, @Valid PageQuery pageQuery) {
        List<LoginUser> loginUsers = LoginUtil.pageUser(keyword, pageQuery.getPage(), pageQuery.getSize(), false);
        return PageResult.build(pageQuery.getPage(), pageQuery.getSize(), loginUsers);
    }

    @Operation(summary = "强退在线用户", description = "强退在线用户")
    @Parameter(name = "token", description = "令牌", example = "ey****J9.ey****fQ.7q****vE", in = ParameterIn.PATH)
    @SaCheckPermission("monitor:online:kickout")
    @DeleteMapping("/{token}")
    public void kickout(@PathVariable String token) {
        String currentToken = LoginUtil.getTokenValue();
        BizAssert.throwIfEqual(token, currentToken, "不能强退自己");
        LoginUtil.kickout(token);
    }
}
