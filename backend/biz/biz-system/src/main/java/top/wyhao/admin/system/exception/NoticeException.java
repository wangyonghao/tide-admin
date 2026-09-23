package top.wyhao.admin.system.exception;

import top.wyhao.starter.core.exception.BizException;

/**
 * 公告业务异常
 */
public class NoticeException extends BizException {

    public NoticeException(String message) {
        super(message);
    }

    public NoticeException(String code, String message) {
        super(code, message);
    }

    public static NoticeException of(String message) {
        return new NoticeException(message);
    }

    public static NoticeException of(String code, String message) {
        return new NoticeException(code, message);
    }

    public static NoticeException notFound() {
        return of("NOTICE_NOT_FOUND", "公告不存在");
    }

    public static NoticeException createFailed() {
        return of("CREATE_FAILED", "创建失败");
    }

    public static NoticeException updateFailed() {
        return of("UPDATE_FAILED", "更新失败");
    }

    public static NoticeException deleteFailed() {
        return of("DELETE_FAILED", "删除失败");
    }

    public static NoticeException idRequired() {
        return of("REQUIRE_NONE_NULL", "ID 不能为空");
    }
}
