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

package top.wyhao.admin.schedule.service.impl;

import com.aizuda.snailjob.client.job.core.openapi.SnailJobOpenApi;
import com.aizuda.snailjob.common.core.enums.StatusEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import top.wyhao.admin.schedule.api.JobApi;
import top.wyhao.admin.schedule.api.JobClient;
import top.wyhao.admin.schedule.api.JobGroupApi;
import top.wyhao.admin.schedule.model.enums.JobStatusEnum;
import top.wyhao.admin.schedule.model.query.JobQuery;
import top.wyhao.admin.schedule.model.req.JobReq;
import top.wyhao.admin.schedule.model.req.JobStatusReq;
import top.wyhao.admin.schedule.model.req.JobTriggerReq;
import top.wyhao.admin.schedule.model.resp.JobResp;
import top.wyhao.admin.schedule.service.JobService;
import top.wyhao.starter.web.core.model.PageResult;

import java.util.List;
import java.util.Set;

/**
 * 任务业务实现
 *
 * @author KAI
 * @author Charles7c
 * @since 2024/6/25 17:25
 */
@Service
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {

    private final JobClient jobClient;
    private final JobApi jobApi;
    private final JobGroupApi jobGroupApi;

    @Override
    public PageResult<JobResp> page(JobQuery query) {
        return jobClient.requestPage(() -> jobApi.page(query));
    }

    @Override
    public boolean create(JobReq req) {
        return Boolean.TRUE.equals(jobClient.request(() -> jobApi.create(req)));
    }

    @Override
    public boolean update(JobReq req, Long id) {
        req.setId(id);
        return Boolean.TRUE.equals(jobClient.request(() -> jobApi.update(req)));
    }

    @Override
    public boolean updateStatus(JobStatusReq req, Long id) {
        return SnailJobOpenApi.updateJobStatus(id)
            .setStatus(JobStatusEnum.DISABLED.equals(req.getJobStatus()) ? StatusEnum.NO : StatusEnum.YES)
            .execute();
    }

    @Override
    public boolean delete(Long id) {
        return SnailJobOpenApi.deleteJob(Set.of(id)).execute();
    }

    @Override
    public boolean trigger(JobTriggerReq req) {
        return Boolean.TRUE.equals(jobClient.request(() -> jobApi.trigger(req)));
    }

    @Override
    public List<String> listGroup() {
        return jobClient.request(jobGroupApi::listGroup);
    }
}