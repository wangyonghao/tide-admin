package top.wyhao.tenant.assembler;

import org.mapstruct.Mapper;
import top.wyhao.tenant.model.entity.Tenant;
import top.wyhao.tenant.model.req.TenantRequest;
import top.wyhao.cmn.core.model.TenantBO;
import top.wyhao.starter.web.convert.BaseEnumConverters;
import top.wyhao.starter.web.convert.MapStructConfig;

/**
 * 租户对象转换
 */
@Mapper(config = MapStructConfig.class, uses = BaseEnumConverters.class)
public interface TenantAssembler {

    Tenant toEntity(TenantRequest request);

    TenantBO toBO(TenantRequest request);
}
