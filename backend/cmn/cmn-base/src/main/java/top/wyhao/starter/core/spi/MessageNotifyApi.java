package top.wyhao.starter.core.spi;

import java.util.List;

/**
 * 站内消息通知 API（系统模块实现）
 */
public interface MessageNotifyApi {

    /**
     * 发送消息给指定用户
     */
    void notifyUsers(String title, String content, String type, List<String> userIds);
}
