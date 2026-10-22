package top.wyhao.tenant.api;

import cn.hutool.core.collection.CollUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import top.wyhao.tenant.client.TenantApi;
import top.wyhao.tenant.client.TenantBO;
import top.wyhao.tenant.mapper.SysTenantMapper;
import top.wyhao.tenant.model.entity.Tenant;
import top.wyhao.tenant.service.TenantService;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TenantApiImpl implements TenantApi {

    private final SysTenantMapper baseMapper;
    private final TenantService tenantService;

    @Override
    public void bindAdminUser(Long tenantId, Long userId) {
        baseMapper.lambdaUpdate()
            .set(Tenant::getAdminUser, userId)
            .eq(Tenant::getId, tenantId)
            .update();
    }

    @Override
    public TenantBO get(Long id) {
        return toBo(tenantService.getEntity(id));
    }

    @Override
    public List<TenantBO> listByIds(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return tenantService.listEntitiesByIds(ids).stream().map(this::toBo).toList();
    }

    @Override
    public TenantBO findByCode(String code) {
        return toBo(tenantService.getEntityByCode(code));
    }

    @Override
    public Long createOwnedBy(String name, String description, Long ownerUserId) {
        return tenantService.createOwnedBy(name, description, ownerUserId);
    }

    private TenantBO toBo(Tenant tenant) {
        if (tenant == null) {
            return null;
        }
        TenantBO bo = new TenantBO();
        bo.setId(tenant.getId());
        bo.setName(tenant.getName());
        bo.setCode(tenant.getCode());
        bo.setPackageId(tenant.getPackageId());
        bo.setAdminUserId(tenant.getAdminUser());
        bo.setAdminUsername(tenant.getAdminUsername());
        return bo;
    }
}
