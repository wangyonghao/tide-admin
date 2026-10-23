package top.wyhao.settings.adapter.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import top.wyhao.settings.client.MailConfigVO;
import top.wyhao.settings.client.ConfigKeys;
import top.wyhao.settings.client.ConfigStoreApi;
import top.wyhao.settings.client.MailConfigApi;

@Component
@RequiredArgsConstructor
public class MailConfigProviderImpl implements MailConfigApi {

    private final ConfigStoreApi configStoreApi;

    @Override
    public MailConfigVO getMailConfig() {
        return configStoreApi.get(ConfigKeys.MAIL, MailConfigVO.class);
    }
}
