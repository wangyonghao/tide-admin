package top.wyhao.security.infrastructure.persistence;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import top.wyhao.security.domain.gateway.RoleRepository;
import top.wyhao.security.domain.model.SysRole;
import top.wyhao.security.infrastructure.persistence.mapper.SysRoleMapper;

import java.util.Collection;
import java.util.List;

/**
 * 角色仓储实现。
 */
@Repository
@RequiredArgsConstructor
public class RoleRepositoryImpl implements RoleRepository {

    private final SysRoleMapper roleMapper;

    @Override
    public SysRole findById(Long id) {
        return roleMapper.selectById(id);
    }

    @Override
    public int insert(SysRole role) {
        return roleMapper.insert(role);
    }

    @Override
    public int updateById(SysRole role) {
        return roleMapper.updateById(role);
    }

    @Override
    public void deleteById(Long id) {
        roleMapper.deleteById(id);
    }

    @Override
    public boolean nameExists(String name, Long selfId) {
        return roleMapper.isNameExists(name, selfId);
    }

    @Override
    public void updateMenuCheckStrictly(Long roleId, Boolean menuCheckStrictly) {
        roleMapper.lambdaUpdate()
                .set(SysRole::getMenuCheckStrictly, menuCheckStrictly)
                .eq(SysRole::getId, roleId)
                .update();
    }

    @Override
    public Long findIdByCode(String code) {
        return roleMapper.lambdaQuery().eq(SysRole::getCode, code).oneOpt().map(SysRole::getId).orElse(null);
    }

    @Override
    public List<SysRole> listByNames(List<String> names) {
        return roleMapper.selectList(Wrappers.<SysRole>lambdaQuery().in(SysRole::getName, names));
    }

    @Override
    public int countByNames(List<String> names) {
        return roleMapper.selectCount(Wrappers.<SysRole>lambdaQuery().in(SysRole::getName, names)).intValue();
    }

    @Override
    public void deleteAll() {
        roleMapper.delete(Wrappers.<SysRole>query().eq("1", 1));
    }

    @Override
    public long countExcluding(Collection<Long> keepIds) {
        return roleMapper.lambdaQuery().notIn(SysRole::getId, keepIds).count();
    }

    @Override
    public boolean deleteExcluding(Collection<Long> keepIds) {
        return roleMapper.lambdaUpdate().notIn(SysRole::getId, keepIds).remove();
    }

    @Override
    public List<SysRole> selectRolesByUserId(Long userId) {
        return roleMapper.selectRolesByUserId(userId);
    }
}
