package top.wyhao.admin.system.assembler;

import org.mapstruct.Mapper;
import top.wyhao.admin.system.entity.SysDept;
import top.wyhao.admin.system.model.DeptModel;
import top.wyhao.starter.web.convert.MapStructConfig;

import java.util.List;

/**
 * 部门对象转换
 */
@Mapper(config = MapStructConfig.class)
public interface DeptAssembler {

    DeptModel.Result toResult(SysDept dept);

    List<DeptModel.Result> toResultList(List<SysDept> depts);
}
