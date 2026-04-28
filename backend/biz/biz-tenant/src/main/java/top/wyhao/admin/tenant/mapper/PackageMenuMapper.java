
package top.wyhao.admin.tenant.mapper;

import org.apache.ibatis.annotations.Mapper;
import top.wyhao.admin.tenant.model.entity.PackageMenuDO;
import top.wyhao.starter.data.mapper.BaseMapper;

/**
 * 套餐和菜单关联 Mapper
 *
 * @author Charles7c
 * @since 2025/7/13 20:24
 */
@Mapper
public interface PackageMenuMapper extends BaseMapper<PackageMenuDO> {
}
