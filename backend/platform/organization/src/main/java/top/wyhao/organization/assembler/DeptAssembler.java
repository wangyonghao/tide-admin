package top.wyhao.organization.assembler;

import org.mapstruct.Mapper;
import top.wyhao.organization.entity.SysDept;
import top.wyhao.starter.web.convert.MapStructConfig;

import java.util.List;
import top.wyhao.organization.model.vo.DeptResult;

/**
 * 部门对象转换
 */
@Mapper(config = MapStructConfig.class)
public interface DeptAssembler {

    DeptResult toResult(SysDept dept);

    List<DeptResult> toResultList(List<SysDept> depts);
}
