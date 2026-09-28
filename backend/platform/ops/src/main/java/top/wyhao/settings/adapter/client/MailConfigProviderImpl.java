package top.wyhao.settings.adapter.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import top.wyhao.settings.client.MailConfig;
import top.wyhao.settings.client.ConfigKeys;
import top.wyhao.settings.client.ConfigStoreApi;
import top.wyhao.settings.client.MailConfigProvider;

@Component
@RequiredArgsConstructor
public class MailConfigProviderImpl implements MailConfigProvider {

    private final ConfigStoreApi configStoreApi;

    @Override
    public MailConfig getMailConfig() {
        return configStoreApi.get(ConfigKeys.MAIL, MailConfig.class);
    }
}
