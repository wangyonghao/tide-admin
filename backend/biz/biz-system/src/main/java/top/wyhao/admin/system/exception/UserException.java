package top.wyhao.admin.system.exception;

import cn.hutool.core.util.StrUtil;
import top.wyhao.starter.core.exception.BizException;

/**
 * 用户业务异常
 */
public class UserException extends BizException {

    public UserException(String message) {
        super(message);
    }

    public UserException(String code, String message) {
        super(code, message);
    }

    public static UserException of(String message) {
        return new UserException(message);
    }

    public static UserException of(String code, String message) {
        return new UserException(code, message);
    }

    public static UserException notFound() {
        return of("USER_NOT_FOUND", "用户不存在");
    }

    public static UserException updateNotAllowed(String reason) {
        return of("USER_UPDATE_NOT_ALLOWED", reason);
    }

    public static UserException disableNotAllowed() {
        return updateNotAllowed("系统内置用户不允许禁用");
    }

    public static UserException roleChangeNotAllowed() {
        return updateNotAllowed("系统内置用户不允许变更角色");
    }

    public static UserException usernameExists() {
        return of("USERNAME_EXISTS", "用户名已被占用");
    }

    public static UserException emailExists() {
        return of("EMAIL_EXISTS", "邮箱已被占用");
    }

    public static UserException phoneExists() {
        return of("PHONE_EXISTS", "手机号已被占用");
    }

    public static UserException importFormatError() {
        return of("IMPORT_FORMAT_ERROR", "数据文件解析异常");
    }

    public static UserException importExpired() {
        return of("IMPORTATION_EXPIRED", "导入已过期，请重新上传");
    }

    public static UserException avatarSizeExceeded(long maxSizeMb) {
        return of("AVATAR_SIZE_EXCEEDED", StrUtil.format("头像大小不能超过 {} MB", maxSizeMb));
    }

    public static UserException downloadTemplateFailed(String detail) {
        return of("USER_IMPORT_TEMPLATE_DOWNLOAD_FAILED", "下载用户导入模板失败：" + detail);
    }
}
