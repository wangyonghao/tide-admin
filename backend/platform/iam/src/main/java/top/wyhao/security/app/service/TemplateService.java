package top.wyhao.security.app.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import top.wyhao.security.client.otp.OtpScene;
import top.wyhao.security.domain.model.OtpChannel;
import top.wyhao.security.infrastructure.config.OtpProperties;
import top.wyhao.security.infrastructure.otp.TargetMasker;

import java.util.HashMap;
import java.util.Map;

/**
 * OTP 模板服务（纯文本占位符渲染，不依赖 Freemarker 等模板引擎）。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TemplateService {

    private final OtpProperties otpProperties;

    /**
     * 渲染模板
     *
     * @param channel   渠道
     * @param scene     场景
     * @param locale    语言
     * @param code      验证码
     * @param expiresIn 有效期（秒）
     * @param target    目标地址
     * @param ip        请求 IP
     * @return 渲染后的内容
     */
    public String render(OtpChannel channel, OtpScene scene, String locale,
                        String code, int expiresIn, String target, String ip) {
        String templateContent = loadTemplate(channel, scene, locale);

        Map<String, Object> model = new HashMap<>();
        model.put("code", code);
        model.put("expires_in", expiresIn / 60);
        model.put("target", TargetMasker.mask(target));
        model.put("timestamp", DateUtil.now());
        model.put("ip", ip);

        return StrUtil.format(templateContent, model);
    }

    private String loadTemplate(OtpChannel channel, OtpScene scene, String locale) {
        if (StrUtil.isBlank(locale)) {
            locale = otpProperties.getTemplate().getDefaultLocale();
        }

        String templatePath = String.format("%s/%s/%s_%s.txt",
            otpProperties.getTemplate().getBasePath(),
            channel.name().toLowerCase(),
            scene.name().toLowerCase(),
            locale);

        try {
            return ResourceUtil.readUtf8Str(templatePath);
        } catch (Exception e) {
            log.warn("模板文件不存在: {}, 尝试使用默认语言", templatePath);

            String defaultPath = String.format("%s/%s/%s_%s.txt",
                otpProperties.getTemplate().getBasePath(),
                channel.name().toLowerCase(),
                scene.name().toLowerCase(),
                otpProperties.getTemplate().getDefaultLocale());

            try {
                return ResourceUtil.readUtf8Str(defaultPath);
            } catch (Exception ex) {
                log.error("默认模板文件也不存在: {}", defaultPath);
                return getDefaultTemplate(channel);
            }
        }
    }

    private String getDefaultTemplate(OtpChannel channel) {
        if (channel == OtpChannel.EMAIL) {
            return "【WYH Admin】验证码\n\n您的验证码为：{code}\n\n验证码有效期为 {expires_in} 分钟，请勿泄露给他人。\n\n如非本人操作，请忽略此邮件。";
        }
        return "【WYH Admin】您的验证码为：{code}，{expires_in}分钟内有效，请勿泄露。";
    }
}
