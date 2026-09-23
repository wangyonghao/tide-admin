package top.wyhao.admin.system.assembler;

import org.mapstruct.Mapper;
import top.wyhao.admin.system.entity.SysDept;
import top.wyhao.starter.web.convert.MapStructConfig;

import java.util.List;
import top.wyhao.admin.system.model.vo.DeptResult;

/**
 * 部门对象转换
 */
@Mapper(config = MapStructConfig.class)
public interface DeptAssembler {

    DeptResult toResult(SysDept dept);

    List<DeptResult> toResultList(List<SysDept> depts);
}
