
package top.wyhao.admin.system.mapper;

import org.apache.ibatis.annotations.Mapper;
import top.wyhao.admin.system.model.entity.DeptDO;
import top.wyhao.starter.data.enums.DatabaseType;
import top.wyhao.starter.data.mapper.BaseMapper;

import java.util.List;

/**
 * 部门 Mapper
 *
 * @author Charles7c
 * @since 2023/1/22 17:56
 */
@Mapper
public interface DeptMapper extends BaseMapper<DeptDO> {

    default List<DeptDO> listChildren(Long id) {
        return this.lambdaQuery().apply(DatabaseType.POSTGRE_SQL.findInSet(id, "ancestors")).list();
    }

}
