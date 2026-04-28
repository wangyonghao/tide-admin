
package top.wyhao.admin.schedule.model.exception;

import top.wyhao.starter.core.exception.BusinessException;
import top.wyhao.starter.core.exception.SystemException;

/**
 * 调度服务异常
 */
public class ScheduleServerException extends SystemException {

    public ScheduleServerException(String message) {
        super(message);
    }

    public ScheduleServerException(String message, Throwable cause) {
        super(message, cause);
    }
}
