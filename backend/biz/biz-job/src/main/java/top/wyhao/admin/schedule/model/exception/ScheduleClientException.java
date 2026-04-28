
package top.wyhao.admin.schedule.model.exception;


import top.wyhao.starter.core.exception.SystemException;

/**
 * 调度客户端异常
 */
public class ScheduleClientException extends SystemException {

    public ScheduleClientException(String message) {
        super(message);
    }

    public ScheduleClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
