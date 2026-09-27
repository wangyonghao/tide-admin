
package top.wyhao.security.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import top.wyhao.security.domain.model.SysUserRole;
import top.wyhao.cmn.db.model.BaseMapper;
import top.wyhao.security.adapter.web.vo.RoleMemberResult;

import java.util.List;

/**
 * 用户和角色 Mapper
 *

 * @since 2023/2/13 23:13
 */
@Mapper
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {

    /**
     * 分页查询列表
     *
     * @param page         分页条件
     * @param queryWrapper 查询条件
     * @return 分页列表信息
     */
    IPage<RoleMemberResult> selectUserPage(@Param("page") IPage<SysUserRole> page,
                                        @Param(Constants.WRAPPER) QueryWrapper<SysUserRole> queryWrapper);

    /**
     * 根据用户 ID 获取角色编码
     */
    @Select("SELECT r.code FROM sys_role r " +
            "INNER JOIN sys_user_role ur ON r.id = ur.role_id " +
            "WHERE ur.user_id = #{userId}")
    List<String> selectRoleCodesByUserId(@Param("userId") Long userId);
}
