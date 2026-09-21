package top.wyhao.admin.job;

import cn.hutool.core.collection.CollUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import top.wyhao.admin.system.entity.SysNotice;
import top.wyhao.admin.system.mapper.SysNoticeMapper;
import top.wyhao.admin.system.model.enums.NoticeMethods;
import top.wyhao.admin.system.model.enums.NoticeStatus;
import top.wyhao.admin.system.service.NoticeService;
import top.wyhao.starter.core.util.CollUtils;
import top.wyhao.starter.quartz.annotation.JobHandler;
import top.wyhao.starter.quartz.spi.JobContext;
import top.wyhao.starter.quartz.spi.JobTask;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 公告发布
 */
@Slf4j
@JobHandler(code = "noticePublish", name = "公告发布", description = "把到期的待发布公告改为已发布")
@RequiredArgsConstructor
public class NoticePublishJob implements JobTask {

    private final SysNoticeMapper noticeMapper;
    private final NoticeService noticeService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void execute(JobContext context) {
        log.info("定时任务 [公告发布] 开始执行");
        List<SysNotice> list = noticeMapper.lambdaQuery()
                .eq(SysNotice::getStatus, NoticeStatus.PENDING)
                .le(SysNotice::getPublishTime, LocalDateTime.now())
                .list();
        if (CollUtil.isEmpty(list)) {
            log.info("定时任务 [公告发布] 无待发布公告");
            return;
        }
        List<SysNotice> needSendMessageList = list.stream()
                .filter(notice -> CollUtil.isNotEmpty(notice.getNoticeMethods()))
                .filter(notice -> notice.getNoticeMethods().contains(NoticeMethods.SYSTEM_MESSAGE.getValue()))
                .toList();
        if (CollUtil.isNotEmpty(needSendMessageList)) {
            needSendMessageList.forEach(noticeService::publish);
        }
        noticeMapper.lambdaUpdate()
                .set(SysNotice::getStatus, NoticeStatus.PUBLISHED)
                .in(SysNotice::getId, CollUtils.mapToList(list, SysNotice::getId))
                .update();
        log.info("定时任务 [公告发布] 执行结束，发布 {} 条", list.size());
    }
}
