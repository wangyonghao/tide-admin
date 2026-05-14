
package top.wyhao.admin.system.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import top.wyhao.admin.system.entity.ConfigDO;
import top.wyhao.admin.system.model.vo.ConfigResult;
import top.wyhao.cmn.db.model.BaseMapper;

/**
 * 系统配置 Mapper
 *
 * @author wyhao
 * @since 2024/04/26
 */
@Mapper
public interface ConfigMapper extends BaseMapper<ConfigDO> {

    /**
     * 分页查询
     *
     * @param page         分页对象
     * @param queryWrapper 查询条件
     * @return 分页结果
     */
    IPage<ConfigResult> selectConfigPage(IPage<ConfigResult> page, @Param(Constants.WRAPPER) Wrapper<ConfigDO> queryWrapper);
}
