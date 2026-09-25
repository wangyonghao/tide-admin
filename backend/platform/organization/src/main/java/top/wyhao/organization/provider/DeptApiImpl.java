package top.wyhao.organization.provider;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import top.wyhao.organization.entity.SysDept;
import top.wyhao.organization.exception.DeptException;
import top.wyhao.organization.mapper.SysDeptMapper;
import top.wyhao.organization.service.DeptService;
import top.wyhao.cmn.core.constant.StringConstants;
import top.wyhao.cmn.core.enums.StatusEnum;
import top.wyhao.organization.client.DeptApi;
import top.wyhao.cmn.core.util.CollUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 部门业务 API 实现
 */
@Service
@RequiredArgsConstructor
public class DeptApiImpl implements DeptApi {

    private final DeptService deptService;
    private final SysDeptMapper deptMapper;

    @Override
    public List<Long> listSelfAndDescendantIds(Long deptId) {
        List<Long> deptIdList = CollUtils.mapToList(deptService.listChildren(deptId), SysDept::getId);
        deptIdList.add(deptId);
        return deptIdList;
    }

    @Override
    public int countValidDeptPaths(Collection<String> deptPaths) {
        int count = 0;
        for (String path : deptPaths) {
            try {
                resolveDeptByPath(path);
                count++;
            } catch (Exception ignored) {
                // 无效路径不计入
            }
        }
        return count;
    }

    @Override
    public Map<String, Long> resolveDeptIdsByPaths(Collection<String> deptPaths) {
        Map<String, Long> deptMap = new HashMap<>();
        for (String deptName : deptPaths) {
            SysDept dept = resolveDeptByPath(deptName);
            deptMap.put(deptName, dept.getId());
        }
        return deptMap;
    }

    @Override
    public boolean isDisabled(Long deptId) {
        SysDept dept = deptService.getById(deptId);
        return dept != null && ObjectUtil.equal(StatusEnum.DISABLE.getValue(), dept.getStatus());
    }

    private SysDept resolveDeptByPath(String deptPath) {
        if (CharSequenceUtil.isBlank(deptPath)) {
            throw DeptException.pathBlank();
        }
        return deptPath.contains(StringConstants.SLASH)
                ? findMultiLevelDept(deptPath)
                : findSingleLevelDept(deptPath.trim());
    }

    private SysDept findMultiLevelDept(String deptPath) {
        String[] pathParts = deptPath.split(StringConstants.SLASH);
        if (pathParts.length == 0) {
            throw DeptException.pathFormatInvalid(deptPath);
        }
        SysDept currentDept = null;
        Long parentId = 0L;
        for (String part : pathParts) {
            String trimmedPart = part.trim();
            if (CharSequenceUtil.isBlank(trimmedPart)) {
                throw DeptException.pathContainsBlank(deptPath);
            }
            currentDept = deptMapper.lambdaQuery()
                    .eq(SysDept::getName, trimmedPart)
                    .eq(SysDept::getParentId, parentId)
                    .one();
            if (currentDept == null) {
                throw DeptException.notFoundInPath(trimmedPart, deptPath);
            }
            parentId = currentDept.getId();
        }
        return currentDept;
    }

    private SysDept findSingleLevelDept(String deptName) {
        List<SysDept> deptList = deptMapper.lambdaQuery().eq(SysDept::getName, deptName).list();
        if (ObjectUtil.isEmpty(deptList)) {
            throw DeptException.notFoundByName(deptName);
        }
        if (deptList.size() > 1) {
            throw DeptException.nameDuplicate(deptName);
        }
        return deptList.get(0);
    }
}
