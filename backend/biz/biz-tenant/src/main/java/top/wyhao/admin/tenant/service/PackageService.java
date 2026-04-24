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

package top.wyhao.admin.tenant.service;

import cn.hutool.core.lang.tree.Tree;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import top.wyhao.admin.tenant.model.query.PackageQuery;
import top.wyhao.admin.tenant.model.req.PackageReq;
import top.wyhao.admin.tenant.model.resp.PackageDetailResp;
import top.wyhao.admin.tenant.model.resp.PackageResp;
import top.wyhao.starter.web.core.model.PageQuery;
import top.wyhao.starter.web.core.model.PageResult;
import top.wyhao.starter.web.core.model.SortQuery;

import java.util.List;

/**
 * 套餐业务接口
 *
 * @author 小熊
 * @since 2024/11/26 11:25
 */
public interface PackageService {

    /**
     * 检查套餐状态
     *
     * @param id ID
     */
    void checkStatus(Long id);

    PageResult<PackageResp> findPage(@Valid PackageQuery query, @Valid PageQuery pageQuery);

    List<PackageResp> list(@Valid PackageQuery query, @Valid SortQuery sortQuery);

    List<Tree<Long>> tree(@Valid PackageQuery query, @Valid SortQuery sortQuery, boolean b);

    void delete(List<Long> id);

    void export(@Valid PackageQuery query, @Valid SortQuery sortQuery, HttpServletResponse response);

    PackageDetailResp get(Long id);

    Long create(@Valid PackageReq req);

    void update(@Valid PackageReq req, Long id);
}