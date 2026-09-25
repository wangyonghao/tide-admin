
package top.wyhao.notification.service;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import top.wyhao.identity.domain.otp.OtpScene;
import top.wyhao.starter.web.core.model.PageQuery;
import top.wyhao.cmn.db.query.PageResult;

import java.util.List;
import top.wyhao.notification.model.dto.SmsLogQuery;
import top.wyhao.notification.model.dto.SmsLogRequest;
import top.wyhao.notification.model.vo.SmsLogResult;

/**
 * 短信 Service
 *

 * @since 2025/03/15 22:15
 */
public interface SmsService {
    void export(@Valid SmsLogQuery query, HttpServletResponse response);

    SmsLogResult get(Long id);

    PageResult<SmsLogResult> page(@Valid SmsLogQuery query, @Valid PageQuery pageQuery);

    List<SmsLogResult> list(@Valid SmsLogQuery query);

    void logAsync(SmsLogRequest req);

    void sendOtp(String phone, OtpScene scene);
}