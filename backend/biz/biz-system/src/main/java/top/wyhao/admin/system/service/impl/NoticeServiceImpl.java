
package top.wyhao.admin.system.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import top.wyhao.admin.system.entity.SysNotice;
import top.wyhao.admin.system.mapper.SysNoticeMapper;
import top.wyhao.admin.system.model.MessageModel;
import top.wyhao.admin.system.model.enums.*;
import top.wyhao.admin.system.model.NotificationModel;
import top.wyhao.admin.system.model.result.dashboard.DashboardNoticeResp;
import top.wyhao.admin.system.service.MessageService;
import top.wyhao.admin.system.service.NoticeLogService;
import top.wyhao.admin.system.service.NoticeService;
import top.wyhao.common.security.util.LoginUtil;
import top.wyhao.starter.core.exception.BadRequestException;
import top.wyhao.starter.core.exception.SystemException;
import top.wyhao.starter.core.util.validation.Check;
import top.wyhao.starter.web.core.model.PageQuery;
import top.wyhao.starter.web.core.model.PageResult;

import java.time.LocalDateTime;
import java.util.List;

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
    public PageResult<NotificationModel> page(NotificationModel.NoticeQuery query, PageQuery pageQuery) {
        IPage<NotificationModel> page = noticeMapper.selectNoticePage(new Page<>(pageQuery.getPage(), pageQuery
                .getSize()), query);
        return PageResult.build(page);
    }

    @Override
    public NotificationModel.Detail detail(Long id) {
        SysNotice entity = noticeMapper.selectById(id);
        if (entity == null) {
            throw new BadRequestException("NOTICE_NOT_FOUND", "公告不存在");
        }
        // 将 SysNotice 转换为 NoticeDetailResp
        return convertToNoticeDetailResp(entity);
    }

    @Override
    public Long create(NotificationModel.Request req) {
        if (!NoticeStatus.DRAFT.equals(req.status())) {
            if (Boolean.TRUE.equals(req.isTiming())) {
                // 待发布
                req = new NotificationModel.Request(req.title(), req.content(), req.type(), req.noticeScope(),
                        req.noticeUsers(), req.noticeMethods(), req.isTiming(), req.publishTime(), req.isTop(), 
                        NoticeStatus.PENDING);
            } else {
                // 已发布
                req = new NotificationModel.Request(req.title(), req.content(), req.type(), req.noticeScope(),
                        req.noticeUsers(), req.noticeMethods(), req.isTiming(), LocalDateTime.now(), req.isTop(), 
                        NoticeStatus.PUBLISHED);
            }
        }
        SysNotice entity = new SysNotice();
        // 设置实体属性
        updateEntityFromReq(entity, req);
        int result = noticeMapper.insert(entity);
        if (result <= 0) {
            throw new SystemException("创建失败");
        }
        // 发送消息
        if (NoticeStatus.PUBLISHED.equals(entity.getStatus())) {
            this.publish(entity);
        }
        return entity.getId();
    }

    @Override
    public void update(NotificationModel.Request req, Long id) {
        SysNotice oldNotice = noticeMapper.selectById(id);
        switch (oldNotice.getStatus()) {
            case PUBLISHED -> {
                Check.throwIfNotEqual(req.status(), oldNotice.getStatus(), "公告已发布，不允许修改状态");
                Check.throwIfNotEqual(req.isTiming(), oldNotice.getIsTiming(), "公告已发布，不允许修改定时发布信息");
                Check.throwIfNotEqual(req.noticeScope(), oldNotice.getNoticeScope(), "公告已发布，不允许修改通知范围");
                if (NoticeScopes.USER.equals(oldNotice.getNoticeScope())) {
                    Check.throwIfNotEmpty(CollUtil.disjunction(req.noticeUsers(), oldNotice
                            .getNoticeUsers()), "公告已发布，不允许修改通知用户");
                }
                Check.when(!CollUtil.isEqualList(req.noticeMethods(), oldNotice
                        .getNoticeMethods()), "公告已发布，不允许修改通知方式");
                // 修正定时发布信息
                if (Boolean.TRUE.equals(oldNotice.getIsTiming())) {
                    Check.throwIfNotEqual(req.publishTime(), oldNotice.getPublishTime(), "公告已发布，不允许修改定时发布信息");
                }
                req = new NotificationModel.Request(req.title(), req.content(), req.type(), req.noticeScope(),
                        req.noticeUsers(), req.noticeMethods(), req.isTiming(), oldNotice.getPublishTime(), 
                        req.isTop(), req.status());
            }
            case DRAFT, PENDING -> {
                // 已发布
                if (NoticeStatus.PUBLISHED.equals(req.status())) {
                    if (Boolean.TRUE.equals(req.isTiming())) {
                        // 待发布
                        req = new NotificationModel.Request(req.title(), req.content(), req.type(), req.noticeScope(),
                                req.noticeUsers(), req.noticeMethods(), req.isTiming(), req.publishTime(), 
                                req.isTop(), NoticeStatus.PENDING);
                    } else {
                        // 已发布
                        req = new NotificationModel.Request(req.title(), req.content(), req.type(), req.noticeScope(),
                                req.noticeUsers(), req.noticeMethods(), req.isTiming(), LocalDateTime.now(), 
                                req.isTop(), NoticeStatus.PUBLISHED);
                    }
                }
            }
            default -> throw new IllegalArgumentException("状态无效");
        }
        SysNotice entity = noticeMapper.selectById(id);
        if (entity == null) {
            throw new BadRequestException("NOTICE_NOT_FOUND", "公告不存在");
        }
        // 更新实体属性
        updateEntityFromReq(entity, req);
        int result = noticeMapper.updateById(entity);
        if (result <= 0) {
            throw new SystemException("更新失败");
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
            throw new BadRequestException("REQUIRE_NONE_NULL", "ID 不能为空");
        }
        // 调用批量删除
        int result = noticeMapper.deleteByIds(ids);
        if (result <= 0) {
            throw new BadRequestException("DELETE_FAILED", "删除失败");
        }
        // 删除公告日志
        noticeLogService.deleteByNoticeIds(ids);
    }

    @Override
    public void publish(SysNotice notice) {
        List<Integer> noticeMethods = notice.getNoticeMethods();
        if (CollUtil.isNotEmpty(noticeMethods) && noticeMethods.contains(NoticeMethods.SYSTEM_MESSAGE.getValue())) {
            MessageTemplates template = MessageTemplates.NOTICE_PUBLISH;
            MessageModel.Request req = new MessageModel.Request(
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

    private NotificationModel.Detail convertToNoticeDetailResp(SysNotice entity) {
        return new NotificationModel.Detail(
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

    private void updateEntityFromReq(SysNotice entity, NotificationModel.Request req) {
        entity.setTitle(req.title());
        entity.setContent(req.content());
        entity.setStatus(req.status());
        entity.setIsTiming(req.isTiming());
        entity.setPublishTime(req.publishTime());
        entity.setNoticeScope(req.noticeScope());
        entity.setNoticeUsers(req.noticeUsers());
        entity.setNoticeMethods(req.noticeMethods());
        // 设置其他属性...
    }
}