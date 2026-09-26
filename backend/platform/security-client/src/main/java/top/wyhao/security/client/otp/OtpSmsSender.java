package top.wyhao.identity.domain.otp;

import top.wyhao.identity.domain.otp.OtpScene;

/**
 * OTP 短信发送（由系统短信模块实现）
 */
public interface OtpSmsSender {

    void sendOtp(String phone, OtpScene scene);
}
