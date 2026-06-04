
package top.wyhao.admin.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.wyhao.admin.system.entity.SysDept;
import top.wyhao.admin.system.exception.DeptException;
import top.wyhao.admin.system.mapper.SysDeptMapper;
import top.wyhao.admin.system.model.DeptModel;
import top.wyhao.admin.system.service.DeptService;
import top.wyhao.admin.system.service.RoleDeptService;
import top.wyhao.admin.system.service.UserService;
import top.wyhao.cmn.db.dialect.DatabaseType;
import top.wyhao.cmn.db.util.DBMetaUtils;
import top.wyhao.cmn.db.util.WrapperUtil;
import top.wyhao.starter.core.enums.StatusEnum;
import top.wyhao.starter.core.util.TreeUtils;
import top.wyhao.starter.core.util.validation.Check;
import top.wyhao.starter.excel.util.ExcelUtils;
import top.wyhao.starter.web.core.model.PageQuery;
import top.wyhao.starter.web.core.model.PageResult;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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

    @Override
    public PageResult<DeptModel.Result> page(DeptModel.Query query, PageQuery pageQuery) {
        QueryWrapper<SysDept> queryWrapper = WrapperUtil.build(query);
        WrapperUtil.applySort(queryWrapper, WrapperUtil.parseSort(query.sort()), SysDept.class);
        IPage<SysDept> page = baseMapper.selectPage(new Page<>(pageQuery.getPage(), pageQuery.getSize()), queryWrapper);
        return PageResult.build(page, DeptModel.Result.class);
    }

    @Override
    public List<DeptModel.Result> list(DeptModel.Query query) {
        return this.list(query, DeptModel.Result.class);
    }

    @Override
    public List<DeptModel.Result> tree(DeptModel.Query query) {
        List<DeptModel.Result> list = this.list(query, DeptModel.Result.class);
        return TreeUtils.flatToTree(list,
                DeptModel.Result::id,
                DeptModel.Result::parentId,
                DeptModel.Result::children,
                (item, children) -> new DeptModel.Result(
                        item.id(),
                        item.name(),
                        item.code(),
                        item.type(),
                        item.parentId(),
                        item.sort(),
                        item.isBuiltin(),
                        item.description(),
                        item.status(),
                        item.disabled(),
                        children
                ));
    }

    @Override
    public DeptModel.Result get(Long id) {
        SysDept entity = baseMapper.selectById(id);
        return BeanUtil.toBean(entity, DeptModel.Result.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(DeptModel.Request req) {
        // 验证部门名称是否已存在
        this.checkNameExist(req.name(), req.parentId(), null);

        SysDept entity = new SysDept();
        entity.setCode(req.code());
        entity.setName(req.name());
        entity.setType(req.type());
        entity.setParentId(req.parentId());
        entity.setAncestors(this.calcDeptPath(entity.getParentId()));
        entity.setDescription(req.description());
        entity.setSort(req.sort());
        entity.setStatus(req.status());
        baseMapper.insert(entity);
        return entity.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(DeptModel.Request req, Long id) {
        this.checkCanUpdate(req, id);

        // type、isBuiltin 不可变更
        SysDept updateEntity = new SysDept();
        updateEntity.setId(id);
        updateEntity.setCode(req.code());
        updateEntity.setName(req.name());
        updateEntity.setParentId(req.parentId());
        updateEntity.setAncestors(this.calcDeptPath(req.parentId()));
        updateEntity.setDescription(req.description());
        updateEntity.setSort(req.sort());
        updateEntity.setStatus(req.status());
        baseMapper.updateById(updateEntity);

        // 变更上级部门时，更新所有下级的 ancestors
        SysDept oldEntity = baseMapper.selectById(id);
        if (ObjectUtil.notEqual(req.parentId(), oldEntity.getParentId())) {
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

    public void checkCanUpdate(DeptModel.Request req, Long id) {
        // 检查名称是否重复
        if (Objects.nonNull(req.parentId())){
            this.checkNameExist(req.name(), req.parentId(), id);
        }

        SysDept oldDept = this.require(id);

        if (Boolean.TRUE.equals(oldDept.getIsBuiltin())) {
            Check.throwIfEqual(StatusEnum.DISABLE.name(), req.status(), "[{}] 是系统内置部门，不允许禁用", oldDept.getName());
            Check.throwIfNotEqual(req.parentId(), oldDept.getParentId(), "[{}] 是系统内置部门，不允许变更上级部门", oldDept.getName());
        }
        if (ObjectUtil.notEqual(req.status(), oldDept.getStatus())) {
            List<SysDept> children = this.listChildren(id);
            long enabledChildrenCount = children.stream().filter(d -> StatusEnum.ENABLE.getValue().equals(d.getStatus())).count();
            Check.when(StatusEnum.DISABLE.getValue().equals(req.status()) && enabledChildrenCount > 0, "禁用 [{}] 前，请先禁用其所有下级部门", oldDept.getName());
            SysDept oldParentDept = this.getByParentId(oldDept.getParentId());
            Check.when(StatusEnum.ENABLE.getValue().equals(req.status()) && StatusEnum.DISABLE.getValue()
                    .equals(oldParentDept.getStatus()), "启用 [{}] 前，请先启用其所有上级部门", oldDept.getName());
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
        Check.when(builtinData::isEmpty, "所选部门 [{}] 是系统内置部门，不允许删除", builtinData.orElseGet(SysDept::new)
                .getName());
        Check.when(this.countChildren(ids) <= 0, "所选部门存在下级部门，不允许删除");
        Check.when(userService.countByDeptIds(ids) <=0, "所选部门存在用户关联，请解除关联后重试");
        // 删除角色和部门关联
        roleDeptService.deleteByDeptIds(ids);
        baseMapper.deleteByIds(ids);
    }

    @Override
    public void export(DeptModel.Query query, HttpServletResponse response) {
        List<DeptModel.Result> list = this.list(query, DeptModel.Result.class);
        ExcelUtils.export(list, "导出数据", DeptModel.Result.class, response);
    }

    /**
     * 查询列表
     *
     * @param query       查询条件
     * @param targetClass 指定类型
     * @return 列表信息
     */
    protected <E> List<E> list(DeptModel.Query query, Class<E> targetClass) {
        QueryWrapper<SysDept> queryWrapper = WrapperUtil.build(query);
        // 设置排序
        WrapperUtil.applySort(queryWrapper, WrapperUtil.parseSort(query.sort()), SysDept.class);
        List<SysDept> entityList = baseMapper.selectList(queryWrapper);
        if (SysDept.class == targetClass) {
            return (List<E>) entityList;
        }
        return BeanUtil.copyToList(entityList, targetClass);
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
        Check.isNull(parentDept, "上级部门不存在");
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
