package top.wyhao.security.domain.gateway;

import top.wyhao.security.domain.model.SysRole;

import java.util.Collection;
import java.util.List;

/**
 * 角色仓储。
 */
public interface RoleRepository {

    SysRole findById(Long id);

    int insert(SysRole role);

    int updateById(SysRole role);

    void deleteById(Long id);

    boolean nameExists(String name, Long selfId);

    void updateMenuCheckStrictly(Long roleId, Boolean menuCheckStrictly);

    Long findIdByCode(String code);

    List<SysRole> listByNames(List<String> names);

    int countByNames(List<String> names);

    void deleteAll();

    long countExcluding(Collection<Long> keepIds);

    boolean deleteExcluding(Collection<Long> keepIds);
}
