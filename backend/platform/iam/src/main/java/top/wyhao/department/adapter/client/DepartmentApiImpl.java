package top.wyhao.department.adapter.client;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import top.wyhao.cmn.core.constant.StringConstants;
import top.wyhao.cmn.core.enums.StatusEnum;
import top.wyhao.cmn.core.util.CollUtils;
import top.wyhao.department.app.service.DepartmentService;
import top.wyhao.department.client.DepartmentApi;
import top.wyhao.department.domain.exception.DepartmentException;
import top.wyhao.department.domain.gateway.DepartmentRepository;
import top.wyhao.department.domain.model.SysDepartment;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 部门业务 API 实现
 */
@Service
@RequiredArgsConstructor
public class DepartmentApiImpl implements DepartmentApi {

    private final DepartmentService departmentService;
    private final DepartmentRepository departmentRepository;

    @Override
    public List<Long> listSelfAndDescendantIds(Long departmentId) {
        List<Long> departmentIdList = CollUtils.mapToList(departmentService.listChildren(departmentId), SysDepartment::getId);
        departmentIdList.add(departmentId);
        return departmentIdList;
    }

    @Override
    public int countValidDepartmentPaths(Collection<String> departmentPaths) {
        int count = 0;
        for (String path : departmentPaths) {
            try {
                resolveDepartmentByPath(path);
                count++;
            } catch (Exception ignored) {
                // 无效路径不计入
            }
        }
        return count;
    }

    @Override
    public Map<String, Long> resolveDepartmentIdsByPaths(Collection<String> departmentPaths) {
        Map<String, Long> departmentMap = new HashMap<>();
        for (String departmentName : departmentPaths) {
            SysDepartment department = resolveDepartmentByPath(departmentName);
            departmentMap.put(departmentName, department.getId());
        }
        return departmentMap;
    }

    @Override
    public boolean isDisabled(Long departmentId) {
        SysDepartment department = departmentService.getById(departmentId);
        return department != null && ObjectUtil.equal(StatusEnum.DISABLE.getValue(), department.getStatus());
    }

    private SysDepartment resolveDepartmentByPath(String departmentPath) {
        if (CharSequenceUtil.isBlank(departmentPath)) {
            throw DepartmentException.pathBlank();
        }
        return departmentPath.contains(StringConstants.SLASH)
            ? findMultiLevelDepartment(departmentPath)
            : findSingleLevelDepartment(departmentPath.trim());
    }

    private SysDepartment findMultiLevelDepartment(String departmentPath) {
        String[] pathParts = departmentPath.split(StringConstants.SLASH);
        if (pathParts.length == 0) {
            throw DepartmentException.pathFormatInvalid(departmentPath);
        }
        SysDepartment currentDepartment = null;
        Long parentId = 0L;
        for (String part : pathParts) {
            String trimmedPart = part.trim();
            if (CharSequenceUtil.isBlank(trimmedPart)) {
                throw DepartmentException.pathContainsBlank(departmentPath);
            }
            currentDepartment = departmentRepository.findByNameAndParentId(trimmedPart, parentId);
            if (currentDepartment == null) {
                throw DepartmentException.notFoundInPath(trimmedPart, departmentPath);
            }
            parentId = currentDepartment.getId();
        }
        return currentDepartment;
    }

    private SysDepartment findSingleLevelDepartment(String departmentName) {
        List<SysDepartment> departmentList = departmentRepository.listByName(departmentName);
        if (ObjectUtil.isEmpty(departmentList)) {
            throw DepartmentException.notFoundByName(departmentName);
        }
        if (departmentList.size() > 1) {
            throw DepartmentException.nameDuplicate(departmentName);
        }
        return departmentList.get(0);
    }
}
