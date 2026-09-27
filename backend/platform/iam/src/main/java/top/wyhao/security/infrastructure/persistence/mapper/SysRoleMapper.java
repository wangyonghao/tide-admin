
package top.wyhao.security.infrastructure.persistence.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import top.wyhao.cmn.db.model.BaseMapper;
import top.wyhao.security.domain.model.SysRole;

import java.util.List;

/**
 * 角色 Mapper
 *

 * @since 2023/2/8 23:17
 */
@Mapper
public interface SysRoleMapper extends BaseMapper<SysRole> {

    /**
     * 根据用户 ID 查询角色列表
     */
    @Select("SELECT r.* FROM sys_role r " +
            "INNER JOIN sys_user_role ur ON r.id = ur.role_id " +
            "WHERE ur.user_id = #{userId}")
    List<SysRole> selectRolesByUserId(@Param("userId") Long userId);

    default boolean isBuiltIn(Long roleId) {
        return this.lambdaQuery()
                .select(SysRole::getName, SysRole::getIsBuiltin)
                .eq(SysRole::getId, roleId)
                .exists();
    }

    default boolean isNameExists(String name, Long selfId) {
        return this.lambdaQuery()
                .eq(SysRole::getName, name)
                .ne(selfId != null, SysRole::getId, selfId)
                .exists();
    }
}
