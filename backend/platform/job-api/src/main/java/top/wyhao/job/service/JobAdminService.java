package top.wyhao.job.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.StringUtils;
import top.wyhao.job.api.model.*;
import top.wyhao.job.domain.SysJob;
import top.wyhao.job.domain.SysJobLog;
import top.wyhao.job.mapper.SysJobLogMapper;
import top.wyhao.job.mapper.SysJobMapper;
import top.wyhao.starter.core.exception.BizException;
import top.wyhao.starter.quartz.JobHandlerRegistry;
import top.wyhao.starter.quartz.QuartzJobScheduler;
import top.wyhao.starter.quartz.spi.JobHandlerDescriptor;
import top.wyhao.starter.web.core.model.PageResult;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobAdminService {

    public static final int STATUS_DISABLED = 0;
    public static final int STATUS_ENABLED = 1;

    private final SysJobMapper sysJobMapper;
    private final SysJobLogMapper sysJobLogMapper;
    private final JobHandlerRegistry jobHandlerRegistry;
    private final QuartzJobScheduler quartzJobScheduler;

    public List<JobHandlerResponse> listHandlers() {
        return jobHandlerRegistry.list().stream().map(descriptor -> {
            JobHandlerResponse response = new JobHandlerResponse();
            response.setCode(descriptor.getCode());
            response.setName(descriptor.getName());
            response.setDescription(descriptor.getDescription());
            response.setAllowConcurrent(descriptor.isAllowConcurrent());
            return response;
        }).toList();
    }

    public PageResult<JobResponse> page(JobQuery query) {
        Page<SysJob> page = sysJobMapper.selectPage(
                new Page<>(query.getPage(), query.getPageSize()),
                new LambdaQueryWrapper<SysJob>()
                        .like(StringUtils.hasText(query.getName()), SysJob::getName, query.getName())
                        .eq(StringUtils.hasText(query.getHandlerCode()), SysJob::getHandlerCode, query.getHandlerCode())
                        .eq(query.getStatus() != null, SysJob::getStatus, query.getStatus())
                        .orderByDesc(SysJob::getCreateTime));
        return PageResult.build(page, records -> records.stream().map(this::toResponse).toList());
    }

    public JobResponse detail(Long id) {
        return toResponse(requireJob(id));
    }

    @Transactional(rollbackFor = Exception.class)
    public Long create(JobSaveRequest request) {
        JobHandlerDescriptor handler = jobHandlerRegistry.requireDescriptor(request.getHandlerCode());
        CronComposer.Compiled compiled = CronComposer.compile(request.getSchedule());
        SysJob job = createJob(request, handler, compiled);
        sysJobMapper.insert(job);
        Long jobId = job.getId();
        boolean enabled = job.getStatus() == STATUS_ENABLED;
        afterCommit(() -> quartzJobScheduler.schedule(jobId, handler.getCode(), compiled.cron(), request.getParams(), enabled));
        return jobId;
    }

    private static @NonNull SysJob createJob(JobSaveRequest request, JobHandlerDescriptor handler, CronComposer.Compiled compiled) {
        SysJob job = new SysJob();
        job.setName(request.getName().trim());
        job.setHandlerCode(handler.getCode());
        job.setCron(compiled.cron());
        job.setScheduleMode(request.getSchedule().getMode());
        job.setSchedulePayload(compiled.payloadJson());
        job.setScheduleLabel(compiled.label());
        job.setParams(request.getParams());
        job.setRemark(request.getRemark());
        job.setStatus(Boolean.TRUE.equals(request.getEnabled()) ? STATUS_ENABLED : STATUS_DISABLED);
        return job;
    }

    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, JobSaveRequest request) {
        SysJob job = requireJob(id);
        JobHandlerDescriptor handler = jobHandlerRegistry.requireDescriptor(request.getHandlerCode());
        CronComposer.Compiled compiled = CronComposer.compile(request.getSchedule());
        job.setName(request.getName().trim());
        job.setHandlerCode(handler.getCode());
        job.setCron(compiled.cron());
        job.setScheduleMode(request.getSchedule().getMode());
        job.setSchedulePayload(compiled.payloadJson());
        job.setScheduleLabel(compiled.label());
        job.setParams(request.getParams());
        job.setRemark(request.getRemark());
        sysJobMapper.updateById(job);
        boolean enabled = job.getStatus() != null && job.getStatus() == STATUS_ENABLED;
        afterCommit(() -> quartzJobScheduler.reschedule(id, handler.getCode(), compiled.cron(), request.getParams(), enabled));
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, Integer status) {
        if (status == null || (status != STATUS_DISABLED && status != STATUS_ENABLED)) {
            throw new BizException("状态无效");
        }
        SysJob job = requireJob(id);
        job.setStatus(status);
        sysJobMapper.updateById(job);
        afterCommit(() -> {
            if (status == STATUS_ENABLED) {
                if (!quartzJobScheduler.exists(id)) {
                    quartzJobScheduler.schedule(id, job.getHandlerCode(), job.getCron(), job.getParams(), true);
                } else {
                    quartzJobScheduler.resume(id);
                }
            } else {
                quartzJobScheduler.pause(id);
            }
        });
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        requireJob(id);
        sysJobMapper.deleteById(id);
        afterCommit(() -> quartzJobScheduler.delete(id));
    }

    public void trigger(Long id) {
        SysJob job = requireJob(id);
        if (!quartzJobScheduler.exists(id)) {
            quartzJobScheduler.schedule(id, job.getHandlerCode(), job.getCron(), job.getParams(), job.getStatus() == STATUS_ENABLED);
        }
        quartzJobScheduler.triggerOnce(id);
    }

    public PageResult<JobLogResponse> pageLogs(JobLogQuery query) {
        Long jobId = null;
        if (StringUtils.hasText(query.getJobId())) {
            jobId = Long.valueOf(query.getJobId());
        }
        Page<SysJobLog> page = sysJobLogMapper.selectPage(
                new Page<>(query.getPage(), query.getPageSize()),
                new LambdaQueryWrapper<SysJobLog>()
                        .eq(jobId != null, SysJobLog::getJobId, jobId)
                        .eq(StringUtils.hasText(query.getHandlerCode()), SysJobLog::getHandlerCode, query.getHandlerCode())
                        .eq(query.getStatus() != null, SysJobLog::getStatus, query.getStatus())
                        .orderByDesc(SysJobLog::getStartTime));
        return PageResult.build(page, records -> records.stream().map(this::toLogResponse).toList());
    }

    public void reconcile() {
        List<SysJob> jobs = sysJobMapper.selectList(null);
        int synced = 0;
        for (SysJob job : jobs) {
            if (!jobHandlerRegistry.contains(job.getHandlerCode())) {
                log.warn("跳过未注册任务: id={}, handler={}", job.getId(), job.getHandlerCode());
                continue;
            }
            boolean enabled = job.getStatus() != null && job.getStatus() == STATUS_ENABLED;
            if (quartzJobScheduler.exists(job.getId())) {
                quartzJobScheduler.reschedule(job.getId(), job.getHandlerCode(), job.getCron(), job.getParams(), enabled);
            } else {
                quartzJobScheduler.schedule(job.getId(), job.getHandlerCode(), job.getCron(), job.getParams(), enabled);
            }
            synced++;
        }
        log.info("定时任务已与 JobStore 对齐，共 {} 条", synced);
    }

    private SysJob requireJob(Long id) {
        SysJob job = sysJobMapper.selectById(id);
        if (job == null) {
            throw new BizException("任务不存在");
        }
        return job;
    }

    private JobResponse toResponse(SysJob job) {
        JobResponse response = new JobResponse();
        response.setId(String.valueOf(job.getId()));
        response.setName(job.getName());
        response.setHandlerCode(job.getHandlerCode());
        if (jobHandlerRegistry.contains(job.getHandlerCode())) {
            JobHandlerDescriptor descriptor = jobHandlerRegistry.requireDescriptor(job.getHandlerCode());
            response.setHandlerName(descriptor.getName());
            response.setHandlerDescription(descriptor.getDescription());
        } else {
            response.setHandlerName(job.getHandlerCode());
        }
        response.setCron(job.getCron());
        response.setScheduleMode(job.getScheduleMode());
        ScheduleSpec schedule = CronComposer.parse(job.getSchedulePayload());
        if (schedule == null) {
            schedule = new ScheduleSpec();
            schedule.setMode(job.getScheduleMode());
            schedule.setCron(job.getCron());
        }
        response.setSchedule(schedule);
        response.setScheduleLabel(job.getScheduleLabel());
        response.setParams(job.getParams());
        response.setStatus(job.getStatus());
        response.setRemark(job.getRemark());
        Instant next = quartzJobScheduler.nextFireTime(job.getId());
        if (next != null) {
            response.setNextFireTime(LocalDateTime.ofInstant(next, ZoneId.systemDefault()));
        }
        response.setCreateTime(job.getCreateTime());
        response.setUpdateTime(job.getUpdateTime());
        response.setUpcomingTimes(CronComposer.upcoming(job.getCron(), 5));
        return response;
    }

    private JobLogResponse toLogResponse(SysJobLog log) {
        JobLogResponse response = new JobLogResponse();
        response.setId(String.valueOf(log.getId()));
        response.setJobId(log.getJobId() == null ? null : String.valueOf(log.getJobId()));
        response.setJobName(log.getJobName());
        response.setHandlerCode(log.getHandlerCode());
        response.setTriggerType(log.getTriggerType());
        response.setStartTime(log.getStartTime());
        response.setEndTime(log.getEndTime());
        response.setDurationMs(log.getDurationMs());
        response.setStatus(log.getStatus());
        response.setErrorMessage(log.getErrorMessage());
        return response;
    }

    private static void afterCommit(Runnable action) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            action.run();
            return;
        }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                action.run();
            }
        });
    }
}
