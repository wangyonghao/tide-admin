
package top.wyhao.admin.tenant.api;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import top.wyhao.admin.modules.common.api.tenant.PackageMenuApi;
import top.wyhao.admin.tenant.service.PackageMenuService;

import java.util.List;

/**
 * 套餐和菜单关联业务 API 实现
 *
 * @author Charles7c
 * @since 2025/7/23 21:13
 */
@Service
@RequiredArgsConstructor
public class PackageMenuApiImpl implements PackageMenuApi {

    private final PackageMenuService baseService;

    @Override
    public List<Long> listMenuIdsByPackageId(Long packageId) {
        return baseService.listMenuIdsByPackageId(packageId);
    }
}
