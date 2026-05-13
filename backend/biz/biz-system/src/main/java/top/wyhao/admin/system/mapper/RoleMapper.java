
package top.wyhao.admin.system.mapper;

import org.apache.ibatis.annotations.Mapper;
import top.wyhao.admin.system.entity.RoleDO;
import top.wyhao.starter.data.mapper.BaseMapper;

/**
 * 角色 Mapper
 *
 * @author Charles7c
 * @since 2023/2/8 23:17
 */
@Mapper
public interface RoleMapper extends BaseMapper<RoleDO> {

    default boolean isBuiltIn(Long roleId) {
        return this.lambdaQuery()
                .select(RoleDO::getName, RoleDO::getIsBuiltin)
                .eq(RoleDO::getId, roleId)
                .exists();
    }
}
