package top.wyhao.settings.adapter.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import top.wyhao.admin.cmn.mail.MailConfigApi;
import top.wyhao.admin.cmn.mail.MailConfigVO;
import top.wyhao.settings.client.ConfigKeys;
import top.wyhao.settings.client.ConfigStoreApi;

@Component
@RequiredArgsConstructor
public class MailConfigProviderImpl implements MailConfigApi {

    private final ConfigStoreApi configStoreApi;

    @Override
    public MailConfigVO getMailConfig() {
        return configStoreApi.get(ConfigKeys.MAIL, MailConfigVO.class);
    }
}
