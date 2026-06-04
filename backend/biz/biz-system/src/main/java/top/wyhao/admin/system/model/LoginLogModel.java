package top.wyhao.admin.system.model;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import top.wyhao.admin.system.model.enums.LoginStatusEnum;

import java.time.LocalDateTime;

/**
 * 登录日志响应信息 Record
 *
 * @since 2026/5/8
 */
@Schema(description = "登录日志响应信息")
public class LoginLogModel {
    public record Result(
            /**
             * 主键ID
             */
            @Schema(description = "主键ID", example = "1")
            Long id,

            /**
             * 用户名
             */
            @Schema(description = "用户名", example = "admin")
            String username,

            /**
             * 设备类型
             */
            @Schema(description = "设备类型", example = "WEB")
            String deviceType,

            /**
             * IP地址
             */
            @Schema(description = "IP地址", example = "192.168.1.1")
            String ipAddress,

            /**
             * 地理位置
             */
            @Schema(description = "地理位置", example = "中国 北京 北京")
            String location,

            /**
             * 浏览器
             */
            @Schema(description = "浏览器", example = "Chrome")
            String browser,

            /**
             * 操作系统
             */
            @Schema(description = "操作系统", example = "Windows 10")
            String os,

            /**
             * 登录状态
             */
            @Schema(description = "登录状态", example = "SUCCESS")
            LoginStatusEnum loginStatus,

            /**
             * 登录时间
             */
            @Schema(description = "登录时间", example = "2026-05-08 10:00:00")
            @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
            LocalDateTime loginTime,

            /**
             * 失败原因
             */
            @Schema(description = "失败原因", example = "用户名或密码错误")
            String failureReason,

            /**
             * 租户ID
             */
            @Schema(description = "租户ID", example = "1")
            Long tenantId
    ) {
    }

    @ExcelIgnoreUnannotated
    @Schema(description = "登录日志导出响应信息")
    public record Excel(
            @ExcelProperty(value = "用户名")
            String username,

            @ExcelProperty(value = "登录 IP")
            String ipAddress,

            @ExcelProperty(value = "登录地点")
            String location,

            @ExcelProperty(value = "浏览器")
            String browser,

            @ExcelProperty(value = "终端系统")
            String os,

            @ExcelProperty(value = "登录状态")
            String loginStatus,

            @ExcelProperty(value = "登录时间")
            @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
            LocalDateTime loginTime,

            @ExcelProperty(value = "失败原因")
            String failureReason
    ) {
    }

    /**
     * 登录日志查询条件
     *

     * @since 2026/05/08
     */
    @Schema(description = "登录日志查询条件")
    public static record LoginLogQuery(
            /**
             * 用户名（模糊查询）
             */
            @Schema(description = "用户名", example = "admin")
            String username,

            /**
             * IP地址（模糊查询）
             */
            @Schema(description = "IP地址", example = "192.168.1.1")
            String ipAddress,

            /**
             * 登录状态
             */
            @Schema(description = "登录状态", example = "SUCCESS")
            LoginStatusEnum loginStatus,

            /**
             * 登录开始时间
             */
            @Schema(description = "登录开始时间", example = "2026-01-01T00:00:00")
            LocalDateTime loginTimeStart,

            /**
             * 登录结束时间
             */
            @Schema(description = "登录结束时间", example = "2026-12-31T23:59:59")
            LocalDateTime loginTimeEnd,

            /**
             * 租户ID
             */
            @Schema(description = "租户ID", example = "1")
            Long tenantId
    ) {}
}
