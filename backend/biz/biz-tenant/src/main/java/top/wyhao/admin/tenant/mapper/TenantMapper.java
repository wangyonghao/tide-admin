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

package top.wyhao.admin.tenant.mapper;

import org.apache.ibatis.annotations.Mapper;
import top.wyhao.admin.tenant.model.entity.TenantDO;
import top.wyhao.starter.data.mapper.BaseMapper;

import java.util.List;

/**
 * 租户 Mapper
 *
 * @author 小熊
 * @since 2024/11/26 17:20
 */
@Mapper
public interface TenantMapper extends BaseMapper<TenantDO> {

    /**
     * 根据套餐 ID 查询数量
     *
     * @param packageIds 套餐 ID 列表
     * @return 租户数量
     */
    default Long countByPackageIds(List<Long> packageIds) {
        return this.lambdaQuery().in(TenantDO::getPackageId, packageIds).count();
    }
}