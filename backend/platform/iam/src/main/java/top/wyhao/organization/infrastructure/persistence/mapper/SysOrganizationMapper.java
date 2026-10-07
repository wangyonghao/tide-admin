package top.wyhao.organization.infrastructure.persistence.mapper;

import org.apache.ibatis.annotations.Mapper;
import top.wyhao.organization.domain.model.SysDept;
import top.wyhao.cmn.db.model.BaseMapper;

import java.util.List;

/**
 * 部门 Mapper
 *
 * @since 2023/1/22 17:56
 */
@Mapper
public interface SysDeptMapper extends BaseMapper<SysDept> {

    /**
     * 查询指定部门的所有后代（ancestors 逗号分隔路径，PostgreSQL）。
     */
    default List<SysDept> listChildren(Long id) {
        return this.lambdaQuery()
                .apply("(select position(',{0},' in ','||ancestors||',')) <> 0", id)
                .list();
    }

}
