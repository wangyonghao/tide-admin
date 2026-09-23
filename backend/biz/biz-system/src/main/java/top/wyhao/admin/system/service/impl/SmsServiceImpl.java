
package top.wyhao.admin.system.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import top.wyhao.admin.cmn.sms.SmsClient;
import top.wyhao.admin.system.assembler.SmsLogAssembler;
import top.wyhao.admin.system.entity.SysSmsLog;
import top.wyhao.admin.system.mapper.SysSmsLogMapper;
import top.wyhao.admin.system.otp.enums.OtpScene;
import top.wyhao.admin.system.service.ConfigService;
import top.wyhao.admin.system.service.SmsService;
import top.wyhao.cmn.db.query.QueryWrapperBuilder;
import top.wyhao.admin.system.exception.SmsException;
import top.wyhao.starter.excel.util.ExcelUtils;
import top.wyhao.starter.web.core.model.PageQuery;
import top.wyhao.starter.web.core.model.PageResult;

import java.util.LinkedHashMap;
import java.util.List;
import top.wyhao.admin.system.model.dto.SmsLogQuery;
import top.wyhao.admin.system.model.dto.SmsLogRequest;
import top.wyhao.admin.system.model.vo.SmsLogResult;

/**
 * 短信日志业务实现
 *
 * @since 2026/05/18
 */
@Service
@RequiredArgsConstructor
public class SmsServiceImpl implements SmsService {
    private final ConfigService configService;
    private final SysSmsLogMapper sysSmsLogMapper;
    private final SmsClient client;
    private final SmsLogAssembler smsLogAssembler;

    @Override
    public void export(SmsLogQuery query, HttpServletResponse response) {
        List<SmsLogResult> list = sysSmsLogMapper.selectObjs(QueryWrapperBuilder.build(query, SysSmsLog.class));

        ExcelUtils.export(list, "短信日志.xlsx", SmsLogResult.class, response);
    }

    @Override
    public SmsLogResult get(Long id) {
        SysSmsLog smsLog = sysSmsLogMapper.selectById(id);
        if (smsLog == null) {
            throw SmsException.notFound();
        }
        return smsLogAssembler.toResult(smsLog);
    }

    @Override
    public PageResult<SmsLogResult> page(SmsLogQuery query, PageQuery pageQuery) {
        IPage<SysSmsLog> resultPage = sysSmsLogMapper.selectPage(new Page<>(pageQuery.getPage(), pageQuery.getPageSize()),
                QueryWrapperBuilder.build(query, SysSmsLog.class));
        return PageResult.build(resultPage, smsLogAssembler::toResultList);
    }

    @Override
    public List<SmsLogResult> list(SmsLogQuery query) {
        return List.of();
    }

    @Async
    @Override
    public void logAsync(SmsLogRequest req) {
        SysSmsLog sysSmsLog = smsLogAssembler.toEntity(req);
        sysSmsLogMapper.insert(sysSmsLog);
    }

    @Override
    public void sendOtp(String phone, OtpScene scene) {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("code", "123456");
        params.put("expire", "5");
        params.put("signature", "签名");
        client.send(phone, getTemplateId(scene), params);
    }

    private String getTemplateId(OtpScene scene) {
        return configService.getSmsTemplate(scene.name());
    }
}