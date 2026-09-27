package top.wyhao.organization.infrastructure.persistence;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import top.wyhao.cmn.db.dialect.DatabaseType;
import top.wyhao.cmn.db.query.QueryWrapperBuilder;
import top.wyhao.cmn.db.util.DBMetaUtils;
import top.wyhao.organization.domain.gateway.DeptRepository;
import top.wyhao.organization.domain.model.SysDept;
import top.wyhao.organization.infrastructure.persistence.mapper.SysDeptMapper;

import javax.sql.DataSource;
import java.util.List;

/**
 * 部门仓储实现
 */
@Repository
@RequiredArgsConstructor
public class DeptRepositoryImpl implements DeptRepository {

    private final SysDeptMapper deptMapper;
    private final DataSource dataSource;

    @Override
    public SysDept findById(Long id) {
        return deptMapper.selectById(id);
    }

    @Override
    public void insert(SysDept dept) {
        deptMapper.insert(dept);
    }

    @Override
    public void updateById(SysDept dept) {
        deptMapper.updateById(dept);
    }

    @Override
    public void deleteByIds(List<Long> ids) {
        deptMapper.deleteByIds(ids);
    }

    @Override
    public List<SysDept> listByQuery(Object query) {
        return deptMapper.selectList(QueryWrapperBuilder.build(query, SysDept.class));
    }

    @Override
    public List<SysDept> listChildren(Long id) {
        return deptMapper.listChildren(id);
    }

    @Override
    public boolean existsName(String name, Long parentId, Long excludeId) {
        return deptMapper.lambdaQuery()
                .eq(SysDept::getName, name)
                .eq(SysDept::getParentId, parentId)
                .ne(excludeId != null, SysDept::getId, excludeId)
                .exists();
    }

    @Override
    public List<SysDept> listByName(String name) {
        return deptMapper.lambdaQuery()
                .eq(SysDept::getName, name)
                .list();
    }

    @Override
    public SysDept findByNameAndParentId(String name, Long parentId) {
        return deptMapper.lambdaQuery()
                .eq(SysDept::getName, name)
                .eq(SysDept::getParentId, parentId)
                .one();
    }

    @Override
    public long countChildren(List<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return 0L;
        }
        DatabaseType databaseType = DBMetaUtils.getDatabaseTypeOrDefault(dataSource, DatabaseType.MYSQL);
        return ids.stream()
                .mapToLong(id -> deptMapper.lambdaQuery().apply(databaseType.findInSet(id, "ancestors")).count())
                .sum();
    }

    @Override
    public void updateChildrenAncestors(String newAncestors, String oldAncestors, Long parentId) {
        List<SysDept> children = listChildren(parentId);
        if (CollUtil.isEmpty(children)) {
            return;
        }
        for (SysDept child : children) {
            SysDept update = new SysDept();
            update.setId(child.getId());
            update.setAncestors(child.getAncestors().replaceFirst(oldAncestors, newAncestors));
            deptMapper.updateById(update);
        }
    }

    @Override
    public long countIdGreaterThan(Long id) {
        return deptMapper.lambdaQuery().gt(SysDept::getId, id).count();
    }

    @Override
    public boolean deleteIdGreaterThan(Long id) {
        return deptMapper.lambdaUpdate().gt(SysDept::getId, id).remove();
    }

    @Override
    public void deleteAll() {
        deptMapper.delete(Wrappers.<SysDept>query().eq("1", 1));
    }
}
