
package top.wyhao.admin.auth.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 在线用户
 *
 * @since 2023/1/20 21:54
 */
@Schema(description = "在线用户响应参数")
public record OnlineUserResult(
        /**
         * ID
         */
        @Schema(description = "会话id", example = "1")
        String sessionId,

        /**
         * Token
         */
        @Schema(description = "Token", example = "ey****J9.ey****fQ.7q****vE")
        String token,

        /**
         * 用户名
         */
        @Schema(description = "用户名", example = "zhangsan")
        String loginName,

        /**
         * 登录 IP
         */
        @Schema(description = "登录 IP", example = "")
        String ip,

        /**
         * 登录地点
         */
        @Schema(description = "登录地点", example = "中国北京北京市")
        String location,

        /**
         * 浏览器
         */
        @Schema(description = "浏览器", example = "Chrome 115.0.0.0")
        String browser,

        /**
         * 操作系统
         */
        @Schema(description = "操作系统", example = "Windows 10")
        String os,

        /**
         * 登录时间
         */
        @Schema(description = "登录时间", example = "2023-08-08 08:08:08", type = "string")
        LocalDateTime loginTime,

        /**
         * 最后活跃时间
         */
        @Schema(description = "最后活跃时间", example = "2023-08-08 08:08:08", type = "string")
        LocalDateTime lastActiveTime
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
