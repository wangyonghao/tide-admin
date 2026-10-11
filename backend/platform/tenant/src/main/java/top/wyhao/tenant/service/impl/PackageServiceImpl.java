
package top.wyhao.tenant.service.impl;

import cn.hutool.core.lang.tree.Tree;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.wyhao.cmn.core.enums.StatusEnum;
import top.wyhao.tenant.exception.PackageException;
import top.wyhao.tenant.mapper.TenantPackageMapper;
import top.wyhao.tenant.model.entity.TenantPackage;
import top.wyhao.tenant.model.query.PackageQuery;
import top.wyhao.tenant.model.req.PackageRequest;
import top.wyhao.tenant.model.resp.PackageDetailResp;
import top.wyhao.tenant.model.resp.PackageResp;
import top.wyhao.tenant.service.PackageMenuService;
import top.wyhao.tenant.service.PackageService;
import top.wyhao.web.core.model.PageQuery;
import top.wyhao.cmn.db.query.PageResult;

import java.util.List;

/**
 * 套餐业务实现
 *


 * @since 2024/11/26 11:25
 */
@Service
@RequiredArgsConstructor
public class PackageServiceImpl implements PackageService {
    private final TenantPackageMapper baseMapper;
    private final PackageMenuService packageMenuService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(PackageRequest req) {
        checkNameRepeat(req.getName(), null);
        TenantPackage entity = new TenantPackage();
        entity.setName(req.getName());
        entity.setSort(req.getSort() == null ? 999 : req.getSort());
        entity.setMenuCheckStrictly(req.getMenuCheckStrictly() == null || req.getMenuCheckStrictly());
        entity.setDescription(req.getDescription());
        entity.setStatus(req.getStatus() == null ? StatusEnum.ENABLE : req.getStatus());
        baseMapper.insert(entity);
        packageMenuService.add(req.getMenuIds(), entity.getId());
        return entity.getId();
    }

    @Override
    public void update(PackageRequest req, Long id) {

    }

    @Override
    public void checkStatus(Long id) {
        TenantPackage pkg = baseMapper.selectById(id);
        if (pkg == null) {
            throw PackageException.notFound();
        }
        if (StatusEnum.DISABLE.equals(pkg.getStatus())) {
            throw PackageException.disabled();
        }
    }

    @Override
    public PageResult<PackageResp> findPage(PackageQuery query, PageQuery pageQuery) {
        return null;
    }

    @Override
    public List<PackageResp> list(PackageQuery query) {
        return List.of();
    }

    @Override
    public List<Tree<Long>> tree(PackageQuery query, boolean b) {
        return List.of();
    }

    @Override
    public void delete(List<Long> id) {
    }

    @Override
    public void export(PackageQuery query, HttpServletResponse response) {

    }

    @Override
    public PackageDetailResp get(Long id) {
        return null;
    }

    /**
     * 名称是否存在
     *
     * @param name 名称
     * @param id   ID
     */
    private void checkNameRepeat(String name, Long id) {
        if (baseMapper.lambdaQuery()
            .eq(TenantPackage::getName, name)
            .ne(id != null, TenantPackage::getId, id)
            .exists()) {
            throw PackageException.nameExists(name);
        }
    }

}