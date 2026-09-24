package top.wyhao.identity.otp.spi;

import top.wyhao.identity.otp.enums.OtpScene;

/**
 * OTP 短信发送（由系统短信模块实现）
 */
public interface OtpSmsSender {

    void sendOtp(String phone, OtpScene scene);
}
