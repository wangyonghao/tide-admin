package top.wyhao.identity.app.service.impl;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import top.wyhao.common.security.util.LoginUtil;
import top.wyhao.identity.app.assembler.LoginLogAssembler;
import top.wyhao.identity.app.service.LoginLogService;
import top.wyhao.identity.client.UserContextHolder;
import top.wyhao.identity.domain.gateway.LoginLogRepository;
import top.wyhao.identity.domain.model.LoginDeviceEnum;
import top.wyhao.identity.domain.model.LoginLogCriteria;
import top.wyhao.identity.domain.model.SysLoginLog;
import top.wyhao.cmn.core.util.IpUtils;
import top.wyhao.starter.excel.util.ExcelUtils;
import top.wyhao.starter.web.core.model.PageQuery;
import top.wyhao.cmn.db.query.PageResult;

import java.time.LocalDateTime;
import java.util.List;
import top.wyhao.identity.adapter.web.vo.LoginLogExcelResult;
import top.wyhao.identity.adapter.web.dto.LoginLogQuery;
import top.wyhao.identity.adapter.web.vo.LoginLogResult;

/**
 * 登录日志 Service 实现
 *
 * @since 2026/05/08
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LoginLogServiceImpl implements LoginLogService {

    private final LoginLogRepository loginLogRepository;
    private final LoginLogAssembler loginLogAssembler;

    @Async
    @Override
    public void asyncLog(String username, String ipAddress, String userAgent, String loginStatus, String failureReason) {
        try {
            SysLoginLog loginLog = new SysLoginLog();
            loginLog.setUsername(username);
            loginLog.setIpAddress(ipAddress);
            loginLog.setUserAgent(userAgent);
            loginLog.setLoginStatus(loginStatus);
            loginLog.setFailureReason(failureReason);
            loginLog.setLoginTime(LocalDateTime.now());
            loginLog.setDeviceType(LoginDeviceEnum.WEB.getValue());

            // 解析地理位置
            loginLog.setLocation(parseLocation(ipAddress));

            // 解析User-Agent
            if (CharSequenceUtil.isNotBlank(userAgent)) {
                UserAgent ua = UserAgentUtil.parse(userAgent);
                if (ua != null) {
                    // 解析浏览器
                    String browser = parseBrowser(ua);
                    loginLog.setBrowser(browser);

                    // 解析操作系统
                    String os = parseOs(ua);
                    loginLog.setOs(os);
                } else {
                    setDefaultDeviceInfo(loginLog);
                }
            } else {
                setDefaultDeviceInfo(loginLog);
            }

            loginLogRepository.insert(loginLog);
            log.info("登录日志记录成功: username={}, ip={}, status={}", username, ipAddress, loginStatus);
        } catch (Exception e) {
            log.error("登录日志记录失败: username={}, ip={}, error={}", username, ipAddress, e.getMessage(), e);
        }
    }

    @Override
    public PageResult<LoginLogResult> page(LoginLogQuery query, PageQuery pageQuery) {
        return loginLogRepository.page(toCriteria(query), pageQuery.getPage(), pageQuery.getPageSize())
                .map(loginLogAssembler::toResult);
    }

    @Override
    public void export(LoginLogQuery query, HttpServletResponse response) {
        List<SysLoginLog> list = loginLogRepository.list(toCriteria(query));
        List<LoginLogExcelResult> exportList = loginLogAssembler.toExcelList(list);

        ExcelUtils.export(exportList, "登录日志数据", LoginLogExcelResult.class, response);
    }

    @Override
    public int cleanExpiredLogs(int retentionDays) {
        LocalDateTime expireTime = LocalDateTime.now().minusDays(retentionDays);
        int count = loginLogRepository.deleteBefore(expireTime);
        log.info("清理过期登录日志完成，清理数量: {}, 留存天数: {}", count, retentionDays);
        return count;
    }

    /**
     * 构建查询条件
     */
    private LoginLogCriteria toCriteria(LoginLogQuery query) {
        LoginLogCriteria criteria = new LoginLogCriteria();
        criteria.setUsername(query.getUsername());
        criteria.setIpAddress(query.getIpAddress());
        if (query.getLoginStatus() != null) {
            criteria.setLoginStatus(query.getLoginStatus().getValue());
        }
        criteria.setLoginTimeStart(query.getLoginTimeStart());
        criteria.setLoginTimeEnd(query.getLoginTimeEnd());
        if (!UserContextHolder.isSuperadmin()) {
            criteria.setTenantRestricted(true);
            criteria.setTenantId(LoginUtil.getTenantId());
        } else if (query.getTenantId() != null) {
            criteria.setTenantRestricted(true);
            criteria.setTenantId(query.getTenantId());
        }
        return criteria;
    }

    /**
     * 解析地理位置
     */
    private String parseLocation(String ipAddress) {
        if (CharSequenceUtil.isBlank(ipAddress)) {
            return "未知地址";
        }

        try {
            String location = IpUtils.getRegion(ipAddress);
            return CharSequenceUtil.isNotBlank(location) ? location : "未知地址";
        } catch (Exception e) {
            log.warn("解析IP地址失败: ip={}, error={}", ipAddress, e.getMessage());
            return "未知地址";
        }
    }

    /**
     * 解析浏览器
     */
    private String parseBrowser(UserAgent ua) {
        try {
            if (ua.getBrowser() != null) {
                String browserName = ua.getBrowser().getName();
                String version = ua.getVersion();
                if (CharSequenceUtil.isNotBlank(version)) {
                    return browserName + " " + version;
                }
                return browserName;
            }
        } catch (Exception e) {
            log.warn("解析浏览器信息失败: error={}", e.getMessage());
        }
        return "其他";
    }

    /**
     * 解析操作系统
     */
    private String parseOs(UserAgent ua) {
        try {
            if (ua.getOs() != null) {
                return ua.getOs().getName();
            }
        } catch (Exception e) {
            log.warn("解析操作系统信息失败: error={}", e.getMessage());
        }
        return "其他";
    }


    /**
     * 设置默认设备信息
     */
    private void setDefaultDeviceInfo(SysLoginLog loginLog) {
        loginLog.setBrowser("其他");
        loginLog.setOs("其他");
    }
}
