package top.wyhao.department.infrastructure.persistence;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import top.wyhao.cmn.db.query.QueryWrapperBuilder;
import top.wyhao.department.domain.gateway.DepartmentRepository;
import top.wyhao.department.domain.model.SysDepartment;
import top.wyhao.department.infrastructure.persistence.mapper.SysDepartmentMapper;

import java.util.List;

/**
 * 部门仓储实现
 */
@Repository
@RequiredArgsConstructor
public class DepartmentRepositoryImpl implements DepartmentRepository {

    private final SysDepartmentMapper departmentMapper;

    @Override
    public SysDepartment findById(Long id) {
        return departmentMapper.selectById(id);
    }

    @Override
    public void insert(SysDepartment department) {
        departmentMapper.insert(department);
    }

    @Override
    public void updateById(SysDepartment department) {
        departmentMapper.updateById(department);
    }

    @Override
    public void deleteByIds(List<Long> ids) {
        departmentMapper.deleteByIds(ids);
    }

    @Override
    public List<SysDepartment> listByQuery(Object query) {
        return departmentMapper.selectList(QueryWrapperBuilder.build(query, SysDepartment.class));
    }

    @Override
    public List<SysDepartment> listChildren(Long id) {
        return departmentMapper.listChildren(id);
    }

    @Override
    public boolean existsName(String name, Long parentId, Long excludeId) {
        return departmentMapper.lambdaQuery()
                .eq(SysDepartment::getName, name)
                .eq(SysDepartment::getParentId, parentId)
                .ne(excludeId != null, SysDepartment::getId, excludeId)
                .exists();
    }

    @Override
    public List<SysDepartment> listByName(String name) {
        return departmentMapper.lambdaQuery()
                .eq(SysDepartment::getName, name)
                .list();
    }

    @Override
    public SysDepartment findByNameAndParentId(String name, Long parentId) {
        return departmentMapper.lambdaQuery()
                .eq(SysDepartment::getName, name)
                .eq(SysDepartment::getParentId, parentId)
                .one();
    }

    @Override
    public long countChildren(List<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return 0L;
        }
        return ids.stream()
                .mapToLong(id -> departmentMapper.lambdaQuery()
                        .apply("(select position(',{0},' in ','||ancestors||',')) <> 0", id)
                        .count())
                .sum();
    }

    @Override
    public void updateChildrenAncestors(String newAncestors, String oldAncestors, Long parentId) {
        List<SysDepartment> children = listChildren(parentId);
        if (CollUtil.isEmpty(children)) {
            return;
        }
        for (SysDepartment child : children) {
            SysDepartment update = new SysDepartment();
            update.setId(child.getId());
            update.setAncestors(child.getAncestors().replaceFirst(oldAncestors, newAncestors));
            departmentMapper.updateById(update);
        }
    }

    @Override
    public long countIdGreaterThan(Long id) {
        return departmentMapper.lambdaQuery().gt(SysDepartment::getId, id).count();
    }

    @Override
    public boolean deleteIdGreaterThan(Long id) {
        return departmentMapper.lambdaUpdate().gt(SysDepartment::getId, id).remove();
    }

    @Override
    public void deleteAll() {
        departmentMapper.delete(Wrappers.<SysDepartment>query().eq("1", 1));
    }
}
