
package top.wyhao.organization.app.service.impl;

import cn.hutool.core.util.ObjectUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.wyhao.cmn.core.enums.StatusEnum;
import top.wyhao.cmn.core.util.TreeUtils;
import top.wyhao.identity.client.UserApi;
import top.wyhao.organization.adapter.web.dto.DeptQuery;
import top.wyhao.organization.adapter.web.dto.DeptRequest;
import top.wyhao.organization.adapter.web.vo.DeptResult;
import top.wyhao.organization.app.assembler.DeptAssembler;
import top.wyhao.organization.app.service.DeptService;
import top.wyhao.organization.domain.exception.DeptException;
import top.wyhao.organization.domain.gateway.DeptRepository;
import top.wyhao.organization.domain.model.SysDept;
import top.wyhao.security.client.RoleDeptApi;
import top.wyhao.starter.excel.util.ExcelUtils;

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

    private final RoleDeptApi roleDeptApi;
    private final UserApi userApi;
    private final DeptRepository deptRepository;
    private final DeptAssembler deptAssembler;

    @Override
    public List<DeptResult> list(DeptQuery query) {
        List<SysDept> entityList = deptRepository.listByQuery(query);
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
        SysDept entity = deptRepository.findById(id);
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
        deptRepository.insert(entity);
        return entity.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(DeptRequest req, Long id) {
        this.checkCanUpdate(req, id);

        SysDept oldEntity = this.require(id);
        String newAncestors = this.calcDeptPath(req.getParentId());

        // type、isBuiltin 不可变更
        SysDept updateEntity = new SysDept();
        updateEntity.setId(id);
        updateEntity.setCode(req.getCode());
        updateEntity.setName(req.getName());
        updateEntity.setParentId(req.getParentId());
        updateEntity.setAncestors(newAncestors);
        updateEntity.setDescription(req.getDescription());
        updateEntity.setSort(req.getSort());
        updateEntity.setStatus(req.getStatus());
        deptRepository.updateById(updateEntity);

        // 变更上级部门时，更新所有下级的 ancestors
        if (ObjectUtil.notEqual(req.getParentId(), oldEntity.getParentId())) {
            deptRepository.updateChildrenAncestors(newAncestors, oldEntity.getAncestors(), id);
        }
    }


    private SysDept require(Long id) {
        SysDept dept = deptRepository.findById(id);
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
        List<SysDept> list = deptRepository.listByQuery(null).stream()
                .filter(d -> ids.contains(d.getId()))
                .toList();
        Optional<SysDept> builtinData = list.stream().filter(SysDept::getIsBuiltin).findFirst();
        if (builtinData.isPresent()) {
            throw DeptException.builtinDeleteNotAllowed(builtinData.get().getName());
        }
        if (deptRepository.countChildren(ids) > 0) {
            throw DeptException.hasChildren();
        }
        if (userApi.countByDeptIds(ids) > 0) {
            throw DeptException.hasUsers();
        }
        // 删除角色和部门关联
        roleDeptApi.deleteByDeptIds(ids);
        deptRepository.deleteByIds(ids);
    }

    @Override
    public void export(DeptQuery query, HttpServletResponse response) {
        List<DeptResult> list = this.list(query);
        ExcelUtils.export(list, "导出数据", DeptResult.class, response);
    }

    @Override
    public List<SysDept> listChildren(Long id) {
        return deptRepository.listChildren(id);
    }

    @Override
    public SysDept getById(Long deptId) {
        return deptRepository.findById(deptId);
    }

    /**
     * 检查名称是否重复
     *
     * @param name     名称
     * @param parentId 上级 ID
     * @param selfId   ID
     */
    private void checkNameExist(String name, Long parentId, Long selfId) {
        boolean exists = deptRepository.existsName(name, parentId, selfId);

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
        SysDept parentDept = deptRepository.findById(parentId);
        if (parentDept == null) {
            throw DeptException.parentNotFound();
        }
        return parentDept;
    }
}
