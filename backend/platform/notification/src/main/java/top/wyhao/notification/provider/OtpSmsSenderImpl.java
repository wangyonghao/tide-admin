package top.wyhao.notification.provider;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import top.wyhao.notification.service.SmsService;
import top.wyhao.identity.otp.enums.OtpScene;
import top.wyhao.identity.otp.spi.OtpSmsSender;

/**
 * OTP 短信发送适配
 */
@Service
@RequiredArgsConstructor
public class OtpSmsSenderImpl implements OtpSmsSender {

    private final SmsService smsService;

    @Override
    public void sendOtp(String phone, OtpScene scene) {
        smsService.sendOtp(phone, scene);
    }
}
