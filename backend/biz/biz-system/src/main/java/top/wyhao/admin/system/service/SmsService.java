
package top.wyhao.admin.system.service;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import top.wyhao.admin.system.model.SmsLogModel;
import top.wyhao.admin.system.otp.enums.OtpScene;
import top.wyhao.starter.web.core.model.PageQuery;
import top.wyhao.starter.web.core.model.PageResult;

import java.util.List;

/**
 * 短信 Service
 *

 * @since 2025/03/15 22:15
 */
public interface SmsService {
    void export(@Valid SmsLogModel.SmsLogQuery query, HttpServletResponse response);

    SmsLogModel.Result get(Long id);

    PageResult<SmsLogModel.Result> page(@Valid SmsLogModel.SmsLogQuery query, @Valid PageQuery pageQuery);

    List<SmsLogModel.Result> list(@Valid SmsLogModel.SmsLogQuery query);

    void logAsync(SmsLogModel.Request req);

    void sendOtp(String phone, OtpScene scene);
}