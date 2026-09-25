
package top.wyhao.admin.tenant.mapper;

import org.apache.ibatis.annotations.Mapper;
import top.wyhao.admin.tenant.model.entity.Tenant;
import top.wyhao.cmn.db.model.BaseMapper;

/**
 * 租户 Mapper
 *

 * @since 2024/11/26 17:20
 */
@Mapper
public interface SysTenantMapper extends BaseMapper<Tenant> {

}