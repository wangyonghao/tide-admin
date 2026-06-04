
package top.wyhao.admin.system.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.wyhao.admin.system.entity.SysMessage;
import top.wyhao.admin.system.entity.SysMessageLog;
import top.wyhao.admin.system.mapper.SysMessageLogMapper;
import top.wyhao.admin.system.mapper.SysMessageMapper;
import top.wyhao.admin.system.model.enums.MessageType;
import top.wyhao.admin.system.model.enums.NoticeScopes;
import top.wyhao.admin.system.model.MessageModel;
import top.wyhao.admin.system.service.MessageService;
import top.wyhao.starter.core.util.CollUtils;
import top.wyhao.starter.messaging.websocket.util.WebSocketUtils;
import top.wyhao.starter.web.core.model.PageQuery;
import top.wyhao.starter.web.core.model.PageResult;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 消息业务实现
 *


 * @since 2023/10/15 19:05
 */
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final SysMessageMapper baseMapper;
    private final SysMessageLogMapper messageLogMapper;

    @Override
    public PageResult<MessageModel> page(MessageModel.MessageQuery query, PageQuery pageQuery) {
        IPage<MessageModel> page = baseMapper.selectMessagePage(new Page<>(pageQuery.getPage(), pageQuery
            .getSize()), query);
        return PageResult.build(page);
    }

    @Override
    public MessageModel.Result get(Long id) {
        return baseMapper.selectMessageById(id);
    }

    @Override
    public void readMessage(List<Long> ids, Long userId) {
        // 查询当前用户的未读消息
        List<SysMessage> list = baseMapper.selectUnreadListByUserId(userId);
        List<Long> unreadIds = CollUtils.mapToList(list, SysMessage::getId);
        this.addWithUserId(CollUtil.isNotEmpty(ids)
            ? CollUtil.intersection(unreadIds, ids).stream().toList()
            : unreadIds, userId);
        WebSocketUtils.sendMessage(StpUtil.getTokenValueByLoginId(userId), String.valueOf(baseMapper
            .selectUnreadListByUserId(userId)
            .size()));
    }

    private void addWithUserId(List<Long> messageIds, Long userId) {
        if (CollUtil.isEmpty(messageIds)) {
            return;
        }
        List<SysMessageLog> list = CollUtils
                .mapToList(messageIds, messageId -> new SysMessageLog(messageId, userId, LocalDateTime.now()));
        messageLogMapper.insert(list);
    }

    @Override
    public MessageModel.UnreadResult countUnreadByUserId(Long userId, Boolean isDetail) {
        Long total = 0L;
        List<MessageModel.UnreadCount> detailList = null;
        if (Boolean.TRUE.equals(isDetail)) {
            detailList = new ArrayList<>();
            for (MessageType messageType : MessageType.values()) {
                Long count = baseMapper.selectUnreadCountByUserIdAndType(userId, messageType.getValue());
                detailList.add(new MessageModel.UnreadCount(messageType, count));
                total += count;
            }
        } else {
            total = baseMapper.selectUnreadCountByUserIdAndType(userId, null);
        }
        return new MessageModel.UnreadResult(total, detailList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(MessageModel.Request req, List<String> userIdList) {
        SysMessage message = new SysMessage();
        message.setTitle(req.title());
        message.setContent(req.content());
        message.setType(req.type());
        message.setPath(req.path());
        message.setScope(CollUtil.isEmpty(userIdList) ? NoticeScopes.ALL : NoticeScopes.USER);
        message.setUsers(userIdList);
        baseMapper.insert(message);
        // 发送消息给指定在线用户
        if (CollUtil.isNotEmpty(userIdList)) {
            userIdList.parallelStream().forEach(userId -> {
                List<String> tokenList = StpUtil.getTokenValueListByLoginId(userId);
                tokenList.parallelStream().forEach(token -> WebSocketUtils.sendMessage(token, "1"));
            });
            return;
        }
        // 发送消息给所有在线用户
        WebSocketUtils.sendMessage("1");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(List<Long> ids) {
        baseMapper.deleteByIds(ids);
        this.deleteByMessageIds(ids);
    }

    private void deleteByMessageIds(List<Long> messageIds) {
        if (CollUtil.isEmpty(messageIds)) {
            return;
        }
        messageLogMapper.lambdaUpdate().in(SysMessageLog::getMessageId, messageIds).remove();
    }
}