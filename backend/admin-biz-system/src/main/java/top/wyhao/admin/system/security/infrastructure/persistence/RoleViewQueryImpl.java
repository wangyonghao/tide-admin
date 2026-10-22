package top.wyhao.admin.system.security.infrastructure.persistence;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import top.wyhao.mybatis.query.PageResult;
import top.wyhao.mybatis.query.QueryWrapperBuilder;
import top.wyhao.admin.system.security.adapter.web.dto.RoleMemberQuery;
import top.wyhao.admin.system.security.adapter.web.dto.RoleQuery;
import top.wyhao.admin.system.security.adapter.web.vo.RoleMemberResult;
import top.wyhao.admin.system.security.app.query.RoleViewQuery;
import top.wyhao.admin.system.security.domain.model.SysRole;
import top.wyhao.admin.system.security.domain.model.SysUserRole;
import top.wyhao.admin.system.security.infrastructure.persistence.mapper.SysRoleMapper;
import top.wyhao.admin.system.security.infrastructure.persistence.mapper.SysUserRoleMapper;

import java.util.List;

/**
 * 角色列表读模型实现。
 */
@Repository
@RequiredArgsConstructor
public class RoleViewQueryImpl implements RoleViewQuery {

    private final SysRoleMapper roleMapper;
    private final SysUserRoleMapper userRoleMapper;

    @Override
    public PageResult<SysRole> page(RoleQuery query, long page, long pageSize) {
        IPage<SysRole> result = roleMapper.selectPage(new Page<>(page, pageSize), QueryWrapperBuilder.build(query, SysRole.class));
        return PageResult.of(result);
    }

    @Override
    public List<SysRole> list(RoleQuery query) {
        return roleMapper.selectList(QueryWrapperBuilder.build(query, SysRole.class));
    }

    @Override
    public List<RoleMemberResult> pageMembers(Long roleId, RoleMemberQuery query, long page, long pageSize) {
        QueryWrapper<SysUserRole> wrapper = Wrappers.query();
        wrapper.eq("role_id", roleId)
                .and(StrUtil.isNotBlank(query.getKeyword()),
                        w -> w.like("su.username", query.getKeyword())
                                .or().like("su.display_name", query.getKeyword()));
        IPage<SysUserRole> memberPage = new Page<>(page, pageSize);
        return userRoleMapper.selectUserPage(memberPage, wrapper).getRecords();
    }
}
