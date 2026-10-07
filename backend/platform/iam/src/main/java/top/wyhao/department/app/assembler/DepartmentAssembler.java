package top.wyhao.department.app.assembler;

import org.mapstruct.Mapper;
import top.wyhao.department.domain.model.SysDepartment;
import top.wyhao.web.convert.MapStructConfig;

import java.util.List;
import top.wyhao.department.adapter.web.vo.DepartmentResult;

/**
 * 部门对象转换
 */
@Mapper(config = MapStructConfig.class)
public interface DepartmentAssembler {

    DepartmentResult toResult(SysDepartment department);

    List<DepartmentResult> toResultList(List<SysDepartment> departments);
}
