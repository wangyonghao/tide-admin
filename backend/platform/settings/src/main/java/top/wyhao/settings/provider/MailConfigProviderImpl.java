package top.wyhao.settings.provider;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import top.wyhao.settings.mapper.SysConfigMapper;
import top.wyhao.cmn.core.model.MailConfig;
import top.wyhao.settings.client.MailConfigProvider;

@Component
@RequiredArgsConstructor
public class MailConfigProviderImpl implements MailConfigProvider {
    private final SysConfigMapper configMapper;

    @Override
    public MailConfig getMailConfig() {
        return configMapper.getConfig("mail", MailConfig.class);
    }
}
