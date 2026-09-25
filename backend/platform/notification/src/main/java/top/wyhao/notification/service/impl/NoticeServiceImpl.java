
package top.wyhao.notification.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import top.wyhao.notification.entity.SysNotice;
import top.wyhao.notification.mapper.SysNoticeMapper;
import top.wyhao.notification.model.enums.*;
import top.wyhao.notification.model.result.dashboard.DashboardNoticeResp;
import top.wyhao.notification.service.MessageService;
import top.wyhao.notification.service.NoticeLogService;
import top.wyhao.notification.service.NoticeService;
import top.wyhao.common.security.util.LoginUtil;
import top.wyhao.notification.exception.NoticeException;
import top.wyhao.starter.web.core.model.PageQuery;
import top.wyhao.cmn.db.query.PageResult;

import java.time.LocalDateTime;
import java.util.List;
import top.wyhao.notification.model.dto.MessageRequest;
import top.wyhao.notification.model.vo.NotificationDetailResult;
import top.wyhao.notification.model.dto.NotificationQuery;
import top.wyhao.notification.model.dto.NotificationRequest;
import top.wyhao.notification.model.vo.NotificationResult;

/**
 * 公告管理 Service 实现
 *

 * @since 2026/5/8
 */
@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {

    private final SysNoticeMapper noticeMapper;
    private final NoticeLogService noticeLogService;
    private final MessageService messageService;


    @Override
    public PageResult<NotificationResult> page(NotificationQuery query, PageQuery pageQuery) {
        IPage<NotificationResult> page = noticeMapper.selectNoticePage(new Page<>(pageQuery.getPage(), pageQuery
                .getPageSize()), query);
        return PageResult.of(page);
    }

    @Override
    public NotificationDetailResult detail(Long id) {
        SysNotice entity = noticeMapper.selectById(id);
        if (entity == null) {
            throw NoticeException.notFound();
        }
        // 将 SysNotice 转换为 NoticeDetailResp
        return convertToNoticeDetailResp(entity);
    }

    @Override
    public Long create(NotificationRequest req) {
        if (!NoticeStatus.DRAFT.equals(req.getStatus())) {
            if (Boolean.TRUE.equals(req.getIsTiming())) {
                // 待发布
                req.setStatus(NoticeStatus.PENDING);
            } else {
                // 已发布
                req.setPublishTime(LocalDateTime.now());
                req.setStatus(NoticeStatus.PUBLISHED);
            }
        }
        SysNotice entity = new SysNotice();
        // 设置实体属性
        updateEntityFromReq(entity, req);
        int result = noticeMapper.insert(entity);
        if (result <= 0) {
            throw NoticeException.createFailed();
        }
        // 发送消息
        if (NoticeStatus.PUBLISHED.equals(entity.getStatus())) {
            this.publish(entity);
        }
        return entity.getId();
    }

    @Override
    public void update(NotificationRequest req, Long id) {
        SysNotice oldNotice = noticeMapper.selectById(id);
        switch (oldNotice.getStatus()) {
            case PUBLISHED -> {
                if (ObjectUtil.notEqual(req.getStatus(), oldNotice.getStatus())) {
                    throw NoticeException.publishedStatusUpdateNotAllowed();
                }
                if (ObjectUtil.notEqual(req.getIsTiming(), oldNotice.getIsTiming())) {
                    throw NoticeException.publishedTimingUpdateNotAllowed();
                }
                if (ObjectUtil.notEqual(req.getNoticeScope(), oldNotice.getNoticeScope())) {
                    throw NoticeException.publishedScopeUpdateNotAllowed();
                }
                if (NoticeScopes.USER.equals(oldNotice.getNoticeScope())
                        && ObjectUtil.isNotEmpty(CollUtil.disjunction(req.getNoticeUsers(), oldNotice.getNoticeUsers()))) {
                    throw NoticeException.publishedUsersUpdateNotAllowed();
                }
                if (!CollUtil.isEqualList(req.getNoticeMethods(), oldNotice.getNoticeMethods())) {
                    throw NoticeException.publishedMethodsUpdateNotAllowed();
                }
                // 修正定时发布信息
                if (Boolean.TRUE.equals(oldNotice.getIsTiming())
                        && ObjectUtil.notEqual(req.getPublishTime(), oldNotice.getPublishTime())) {
                    throw NoticeException.publishedTimingUpdateNotAllowed();
                }
                req.setPublishTime(oldNotice.getPublishTime());
            }
            case DRAFT, PENDING -> {
                // 已发布
                if (NoticeStatus.PUBLISHED.equals(req.getStatus())) {
                    if (Boolean.TRUE.equals(req.getIsTiming())) {
                        // 待发布
                        req.setStatus(NoticeStatus.PENDING);
                    } else {
                        // 已发布
                        req.setPublishTime(LocalDateTime.now());
                        req.setStatus(NoticeStatus.PUBLISHED);
                    }
                }
            }
            default -> throw new IllegalArgumentException("状态无效");
        }
        SysNotice entity = noticeMapper.selectById(id);
        if (entity == null) {
            throw NoticeException.notFound();
        }
        // 更新实体属性
        updateEntityFromReq(entity, req);
        int result = noticeMapper.updateById(entity);
        if (result <= 0) {
            throw NoticeException.updateFailed();
        }
        // 重置定时发布时间
        if (!NoticeStatus.PUBLISHED.equals(entity.getStatus()) && Boolean.FALSE.equals(entity
                .getIsTiming()) && entity.getPublishTime() != null) {
            noticeMapper.lambdaUpdate().set(SysNotice::getPublishTime, null).eq(SysNotice::getId, entity.getId()).update();
        }
        // 发送消息
        if (Boolean.FALSE.equals(entity.getIsTiming()) && NoticeStatus.PUBLISHED.equals(entity.getStatus())) {
            this.publish(entity);
        }
    }

    @Override
    public void delete(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw NoticeException.idRequired();
        }
        // 调用批量删除
        int result = noticeMapper.deleteByIds(ids);
        if (result <= 0) {
            throw NoticeException.deleteFailed();
        }
        // 删除公告日志
        noticeLogService.deleteByNoticeIds(ids);
    }

    @Override
    public void publish(SysNotice notice) {
        List<Integer> noticeMethods = notice.getNoticeMethods();
        if (CollUtil.isNotEmpty(noticeMethods) && noticeMethods.contains(NoticeMethods.SYSTEM_MESSAGE.getValue())) {
            MessageTemplates template = MessageTemplates.NOTICE_PUBLISH;
            MessageRequest req = new MessageRequest(
                    template.getTitle(),
                    template.getContent().formatted(notice.getTitle()),
                    MessageType.SYSTEM,
                    template.getPath().formatted(notice.getId())
            );
            // 新增消息
            messageService.add(req, notice.getNoticeUsers());
        }
    }

    @Override
    public List<Long> listUnreadIdsByUserId(NoticeMethods method, Long userId) {
        return noticeMapper.selectUnreadIdsByUserId(method != null ? method.getValue() : null, userId);
    }

    @Override
    public void readNotice(Long id, Long userId) {
        noticeLogService.add(List.of(userId), id);
    }

    @Override
    public List<DashboardNoticeResp> listDashboard() {
        Long userId = LoginUtil.getUserId();
        return noticeMapper.selectDashboardList(userId);
    }

    private NotificationDetailResult convertToNoticeDetailResp(SysNotice entity) {
        return new NotificationDetailResult(
                entity.getId(),
                entity.getCreateUser(),
                null, // createUserString - 需要从其他地方获取
                entity.getCreateTime(),
                null, // disabled
                entity.getUpdateUser(),
                null, // updateUserString - 需要从其他地方获取
                entity.getUpdateTime(),
                entity.getTitle(),
                entity.getType(),
                entity.getContent(),
                entity.getNoticeScope(),
                entity.getNoticeUsers(),
                entity.getNoticeMethods(),
                entity.getIsTiming(),
                entity.getPublishTime(),
                entity.getIsTop(),
                entity.getStatus()
        );
    }

    private void updateEntityFromReq(SysNotice entity, NotificationRequest req) {
        entity.setTitle(req.getTitle());
        entity.setContent(req.getContent());
        entity.setStatus(req.getStatus());
        entity.setIsTiming(req.getIsTiming());
        entity.setPublishTime(req.getPublishTime());
        entity.setNoticeScope(req.getNoticeScope());
        entity.setNoticeUsers(req.getNoticeUsers());
        entity.setNoticeMethods(req.getNoticeMethods());
        // 设置其他属性...
    }
}