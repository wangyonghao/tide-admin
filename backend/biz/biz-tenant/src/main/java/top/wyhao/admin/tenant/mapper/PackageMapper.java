
package top.wyhao.admin.tenant.mapper;

import org.apache.ibatis.annotations.Mapper;
import top.wyhao.starter.data.mapper.BaseMapper;
import top.wyhao.admin.tenant.model.entity.PackageDO;

/**
 * 套餐 Mapper
 *
 * @author 小熊
 * @since 2024/11/26 11:25
 */
@Mapper
public interface PackageMapper extends BaseMapper<PackageDO> {
}