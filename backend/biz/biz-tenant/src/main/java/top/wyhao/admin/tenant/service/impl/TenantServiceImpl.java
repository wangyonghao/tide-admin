
package top.wyhao.admin.tenant.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.extra.spring.SpringUtil;
import com.alicp.jetcache.anno.Cached;
import lombok.RequiredArgsConstructor;
import me.ahoo.cosid.provider.IdGeneratorProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.wyhao.admin.tenant.constant.TenantConstants;
import top.wyhao.admin.tenant.mapper.SysTenantMapper;
import top.wyhao.admin.tenant.model.entity.Tenant;
import top.wyhao.admin.tenant.model.query.TenantQuery;
import top.wyhao.admin.tenant.model.req.TenantRequest;
import top.wyhao.admin.tenant.model.resp.TenantDetailResp;
import top.wyhao.admin.tenant.model.resp.TenantResp;
import top.wyhao.admin.tenant.service.PackageService;
import top.wyhao.admin.tenant.service.TenantService;
import top.wyhao.starter.cache.redisson.util.RedisUtils;
import top.wyhao.starter.core.constant.CacheConstants;
import top.wyhao.starter.core.constant.StringConstants;
import top.wyhao.starter.core.enums.RoleCodeEnum;
import top.wyhao.starter.core.enums.StatusEnum;
import top.wyhao.starter.core.model.TenantBO;
import top.wyhao.starter.core.spi.RoleApi;
import top.wyhao.starter.core.spi.RoleMenuApi;
import top.wyhao.starter.core.spi.TenantDataApi;
import top.wyhao.starter.core.util.validation.BizAssert;
import top.wyhao.starter.tenant.config.TenantProperties;
import top.wyhao.starter.tenant.util.TenantUtils;
import top.wyhao.starter.web.core.model.LabelValueResult;
import top.wyhao.starter.web.core.model.PageQuery;
import top.wyhao.starter.web.core.model.PageResult;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 租户业务实现
 *


 * @since 2024/11/26 17:20
 */
@Service
@RequiredArgsConstructor
public class TenantServiceImpl implements TenantService {

    private final Map<String, TenantDataApi> tenantDataApiMap = SpringUtil.getBeansOfType(TenantDataApi.class);
    private final PackageService packageService;
    private final IdGeneratorProvider idGeneratorProvider;
    private final RoleMenuApi roleMenuApi;
    private final RoleApi roleApi;
    private final TenantProperties tenantProperties;
    private final SysTenantMapper baseMapper;

    @Override
    public Long create(TenantRequest req) {
        this.checkNameRepeat(req.getName(), null);
        this.checkDomainRepeat(req.getDomain(), null);
        // 检查套餐状态
        packageService.checkStatus(req.getPackageId());
        // 生成租户编码
        req.setCode(this.generateCode());
        // 新增信息
        Tenant entity = BeanUtil.copyProperties(req, Tenant.class);
        baseMapper.insert(entity);
        // 初始化租户数据
        req.setId(entity.getId());
        tenantDataApiMap.forEach((key, value) -> value.init(BeanUtil.copyProperties(req, TenantBO.class)));
        return entity.getId();
    }



    public void beforeDelete(List<Long> ids) {
        // 在租户中执行数据清除
        for (Long id : ids) {
            TenantUtils.execute(id, () -> tenantDataApiMap.forEach((key, value) -> value.clear()));
        }
    }


    public void afterDelete(List<Long> ids) {
        RedisUtils.deleteByPattern(TenantConstants.TENANT_KEY_PREFIX + StringConstants.ASTERISK);
    }

    @Override
    @Cached(name = TenantConstants.TENANT_KEY_PREFIX, key = "#domain")
    public Long getIdByDomain(String domain) {
        return baseMapper.lambdaQuery()
            .select(Tenant::getId)
            .eq(Tenant::getDomain, domain)
            .oneOpt()
            .map(Tenant::getId)
            .orElse(null);
    }

    @Override
    @Cached(name = TenantConstants.TENANT_KEY_PREFIX, key = "#code")
    public Long getIdByCode(String code) {
        return baseMapper.lambdaQuery()
            .select(Tenant::getId)
            .eq(Tenant::getDisplayID, code)
            .oneOpt()
            .map(Tenant::getId)
            .orElse(null);
    }

    @Override
    public void checkStatus(Long id) {
        // 默认租户
        if (tenantProperties.getDefaultTenantId().equals(id)) {
            return;
        }
        Tenant tenant = baseMapper.selectById(id);
        BizAssert.throwIfEqual(StatusEnum.DISABLE, tenant.getStatus(), "租户已被禁用");
        BizAssert.isTrue(tenant.getExpireTime() != null && tenant.getExpireTime()
            .isBefore(LocalDateTime.now()), "租户已过期");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTenantMenu(List<Long> newMenuIds, Long packageId) {
        List<Long> tenantIdList = this.listIdByPackageId(packageId);
        if (CollUtil.isEmpty(tenantIdList)) {
            return;
        }
        // 所有租户角色：删除旧菜单
        tenantIdList.forEach(tenantId -> TenantUtils.execute(tenantId, () -> {
            // 删除旧菜单
            roleMenuApi.deleteByNotInMenuIds(newMenuIds);
            // 更新在线用户上下文
            Set<Long> roleIdSet = roleMenuApi.listRoleIdByNotInMenuIds(newMenuIds);
            roleIdSet.forEach(roleApi::updateUserContext);
        }));
        // 租户管理员：新增菜单
        tenantIdList.forEach(tenantId -> TenantUtils.execute(tenantId, () -> {
            Long roleId = roleApi.getIdByCode(RoleCodeEnum.TENANT_ADMIN.getCode());
            roleMenuApi.add(newMenuIds, roleId);
            // 更新在线用户上下文
            roleApi.updateUserContext(roleId);
        }));
        // 删除缓存
        RedisUtils.deleteByPattern(CacheConstants.ROLE_MENU_KEY_PREFIX + StringConstants.ASTERISK);
    }

    /**
     * 检查名称是否重复
     *
     * @param name 名称
     * @param id   ID
     */
    private void checkNameRepeat(String name, Long id) {
        BizAssert.isTrue(baseMapper.lambdaQuery()
            .eq(Tenant::getName, name)
            .ne(id != null, Tenant::getId, id)
            .exists(), "名称为 [{}] 的租户已存在", name);
    }

    /**
     * 检查域名是否重复
     *
     * @param domain 域名
     * @param id     ID
     */
    private void checkDomainRepeat(String domain, Long id) {
        BizAssert.isTrue(baseMapper.lambdaQuery()
            .eq(Tenant::getDomain, domain)
            .ne(id != null, Tenant::getId, id)
            .exists(), "域名为 [{}] 的租户已存在", domain);
    }

    /**
     * 生成租户编码
     *
     * @return 租户编码
     */
    private String generateCode() {
        String code;
        do {
            code = idGeneratorProvider.getRequired(TenantConstants.CODE_GENERATOR_KEY).generateAsString();
        } while (baseMapper.lambdaQuery().eq(Tenant::getDisplayID, code).exists());
        return code;
    }

    /**
     * 根据套餐 ID 查询租户 ID 列表
     *
     * @param id 套餐 ID
     * @return 租户 ID 列表
     */
    private List<Long> listIdByPackageId(Long id) {
//        return baseMapper.lambdaQuery()
//            .select(Tenant::getId)
//            .eq(Tenant::getPackageId, id)
//            .list()
//            .stream()
//            .map(Tenant::getId)
//            .toList();
        return null; // todo 待实现
    }

    @Override
    public PageResult<TenantResp> findPage(TenantQuery query, PageQuery pageQuery) {
        return null;
    }

    @Override
    public void update(TenantRequest req, Long id) {
        this.checkNameRepeat(req.getName(), id);
        this.checkDomainRepeat(req.getDomain(), id);
        Tenant tenant = baseMapper.selectById(id);

        RedisUtils.deleteByPattern(TenantConstants.TENANT_KEY_PREFIX + StringConstants.ASTERISK);
    }

    @Override
    public List<LabelValueResult> dict(TenantQuery query) {
        return List.of();
    }

    @Override
    public TenantDetailResp get(Long id) {
        return null;
    }

    @Override
    public void delete(List<Long> ids) {

    }

    @Override
    public List<TenantResp> list(TenantQuery query) {
        return List.of();
    }

    @Override
    public List<Tree<Long>> tree(TenantQuery query, boolean b) {
        return List.of();
    }
}
