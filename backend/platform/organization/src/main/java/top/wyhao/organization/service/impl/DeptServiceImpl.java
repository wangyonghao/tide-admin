
package top.wyhao.admin.system.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.wyhao.admin.system.assembler.DeptAssembler;
import top.wyhao.admin.system.entity.SysDept;
import top.wyhao.admin.system.exception.DeptException;
import top.wyhao.admin.system.mapper.SysDeptMapper;
import top.wyhao.admin.system.service.DeptService;
import top.wyhao.admin.system.service.RoleDeptService;
import top.wyhao.admin.system.service.UserService;
import top.wyhao.cmn.db.dialect.DatabaseType;
import top.wyhao.cmn.db.query.QueryWrapperBuilder;
import top.wyhao.cmn.db.util.DBMetaUtils;
import top.wyhao.starter.core.enums.StatusEnum;
import top.wyhao.starter.core.util.TreeUtils;
import top.wyhao.starter.excel.util.ExcelUtils;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import top.wyhao.admin.system.model.dto.DeptQuery;
import top.wyhao.admin.system.model.dto.DeptRequest;
import top.wyhao.admin.system.model.vo.DeptResult;

/**
 * 部门业务实现
 *
 * @since 2023/1/22 17:55
 */
@Service
@RequiredArgsConstructor
public class DeptServiceImpl implements DeptService {

    private final RoleDeptService roleDeptService;
    private final DataSource dataSource;
    private final UserService userService;

    private final SysDeptMapper baseMapper;
    private final DeptAssembler deptAssembler;

    @Override
    public List<DeptResult> list(DeptQuery query) {
        List<SysDept> entityList = baseMapper.selectList(QueryWrapperBuilder.build(query,SysDept.class));
        return deptAssembler.toResultList(entityList);
    }

    @Override
    public List<DeptResult> tree(DeptQuery query) {
        List<DeptResult> list = this.list(query);
        return TreeUtils.flatToTree(list,
                DeptResult::getId,
                DeptResult::getParentId,
                DeptResult::getChildren,
                (item, children) -> new DeptResult(
                        item.getId(),
                        item.getName(),
                        item.getCode(),
                        item.getType(),
                        item.getParentId(),
                        item.getSort(),
                        item.getIsBuiltin(),
                        item.getDescription(),
                        item.getStatus(),
                        item.getDisabled(),
                        children
                ));
    }

    @Override
    public DeptResult get(Long id) {
        SysDept entity = baseMapper.selectById(id);
        return deptAssembler.toResult(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(DeptRequest req) {
        // 验证部门名称是否已存在
        this.checkNameExist(req.getName(), req.getParentId(), null);

        SysDept entity = new SysDept();
        entity.setCode(req.getCode());
        entity.setName(req.getName());
        entity.setType(req.getType());
        entity.setParentId(req.getParentId());
        entity.setAncestors(this.calcDeptPath(entity.getParentId()));
        entity.setDescription(req.getDescription());
        entity.setSort(req.getSort());
        entity.setStatus(req.getStatus());
        baseMapper.insert(entity);
        return entity.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(DeptRequest req, Long id) {
        this.checkCanUpdate(req, id);

        // type、isBuiltin 不可变更
        SysDept updateEntity = new SysDept();
        updateEntity.setId(id);
        updateEntity.setCode(req.getCode());
        updateEntity.setName(req.getName());
        updateEntity.setParentId(req.getParentId());
        updateEntity.setAncestors(this.calcDeptPath(req.getParentId()));
        updateEntity.setDescription(req.getDescription());
        updateEntity.setSort(req.getSort());
        updateEntity.setStatus(req.getStatus());
        baseMapper.updateById(updateEntity);

        // 变更上级部门时，更新所有下级的 ancestors
        SysDept oldEntity = baseMapper.selectById(id);
        if (ObjectUtil.notEqual(req.getParentId(), oldEntity.getParentId())) {
            baseMapper.lambdaUpdate()
                    .set(SysDept::getAncestors, updateEntity.getAncestors())
                    .likeLeft(SysDept::getAncestors, oldEntity.getAncestors()).update();
        }
    }


    private SysDept require(Long id) {
        SysDept dept = baseMapper.selectById(id);
        if (dept == null) {
            throw DeptException.notFound(id);
        }
        return dept;
    }

    public void checkCanUpdate(DeptRequest req, Long id) {
        // 检查名称是否重复
        if (Objects.nonNull(req.getParentId())){
            this.checkNameExist(req.getName(), req.getParentId(), id);
        }

        SysDept oldDept = this.require(id);

        if (Boolean.TRUE.equals(oldDept.getIsBuiltin())) {
            if (ObjectUtil.equal(StatusEnum.DISABLE.getValue(), req.getStatus())) {
                throw DeptException.builtinDisableNotAllowed(oldDept.getName());
            }
            if (ObjectUtil.notEqual(req.getParentId(), oldDept.getParentId())) {
                throw DeptException.builtinParentUpdateNotAllowed(oldDept.getName());
            }
        }
        if (ObjectUtil.notEqual(req.getStatus(), oldDept.getStatus())) {
            List<SysDept> children = this.listChildren(id);
            long enabledChildrenCount = children.stream().filter(d -> StatusEnum.ENABLE.getValue().equals(d.getStatus())).count();
            if (StatusEnum.DISABLE.getValue().equals(req.getStatus()) && enabledChildrenCount > 0) {
                throw DeptException.hasEnabledChildren(oldDept.getName());
            }
            SysDept oldParentDept = this.getByParentId(oldDept.getParentId());
            if (StatusEnum.ENABLE.getValue().equals(req.getStatus()) && StatusEnum.DISABLE.getValue()
                    .equals(oldParentDept.getStatus())) {
                throw DeptException.parentDisabled(oldDept.getName());
            }
        }
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(List<Long> ids) {
        List<SysDept> list = baseMapper.lambdaQuery()
                .select(SysDept::getName, SysDept::getIsBuiltin)
                .in(SysDept::getId, ids)
                .list();
        Optional<SysDept> builtinData = list.stream().filter(SysDept::getIsBuiltin).findFirst();
        if (builtinData.isPresent()) {
            throw DeptException.builtinDeleteNotAllowed(builtinData.get().getName());
        }
        if (this.countChildren(ids) > 0) {
            throw DeptException.hasChildren();
        }
        if (userService.countByDeptIds(ids) > 0) {
            throw DeptException.hasUsers();
        }
        // 删除角色和部门关联
        roleDeptService.deleteByDeptIds(ids);
        baseMapper.deleteByIds(ids);
    }

    @Override
    public void export(DeptQuery query, HttpServletResponse response) {
        List<DeptResult> list = this.list(query);
        ExcelUtils.export(list, "导出数据", DeptResult.class, response);
    }

    @Override
    public List<SysDept> listChildren(Long id) {
        DatabaseType databaseType = DBMetaUtils.getDatabaseTypeOrDefault(dataSource, DatabaseType.MYSQL);
        return baseMapper.lambdaQuery().apply(databaseType.findInSet(id, "ancestors")).list();
    }

    @Override
    public SysDept getById(Long deptId) {
        return baseMapper.selectById(deptId);
    }

    /**
     * 检查名称是否重复
     *
     * @param name     名称
     * @param parentId 上级 ID
     * @param selfId   ID
     */
    private void checkNameExist(String name, Long parentId, Long selfId) {
        boolean exists = baseMapper.lambdaQuery()
                .eq(SysDept::getName, name)
                .eq(SysDept::getParentId, parentId)
                .ne(selfId != null, SysDept::getId, selfId)
                .exists();

        if (exists) {
            throw DeptException.nameExist(name);
        }
    }

    /**
     * 获取祖级列表
     *
     * @param parentId 上级部门
     * @return 祖级列表
     */
    private String calcDeptPath(Long parentId) {
        SysDept parentDept = this.getByParentId(parentId);
        return "%s,%s".formatted(parentDept.getAncestors(), parentId);
    }

    /**
     * 根据上级部门 ID 查询
     *
     * @param parentId 上级部门 ID
     * @return 上级部门信息
     */
    private SysDept getByParentId(Long parentId) {
        SysDept parentDept = baseMapper.selectById(parentId);
        if (parentDept == null) {
            throw DeptException.parentNotFound();
        }
        return parentDept;
    }

    /**
     * 查询子部门数量
     *
     * @param ids ID 列表
     * @return 子部门数量
     */
    private Long countChildren(List<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return 0L;
        }
        DatabaseType databaseType = DBMetaUtils.getDatabaseTypeOrDefault(dataSource, DatabaseType.MYSQL);
        return ids.stream()
                .mapToLong(id -> baseMapper.lambdaQuery().apply(databaseType.findInSet(id, "ancestors")).count())
                .sum();
    }

    /**
     * 更新子部门祖级列表
     *
     * @param newAncestors 新祖级列表
     * @param oldAncestors 原祖级列表
     * @param id           ID
     */
    private void updateChildrenAncestors(String newAncestors, String oldAncestors, Long id) {
        List<SysDept> children = this.listChildren(id);
        if (CollUtil.isEmpty(children)) {
            return;
        }
        List<SysDept> list = new ArrayList<>(children.size());
        for (SysDept child : children) {
            SysDept dept = new SysDept();
            dept.setId(child.getId());
            dept.setAncestors(child.getAncestors().replaceFirst(oldAncestors, newAncestors));
            list.add(dept);
        }
        baseMapper.updateById(list);
    }
}
