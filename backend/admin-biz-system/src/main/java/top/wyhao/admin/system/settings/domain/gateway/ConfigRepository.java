package top.wyhao.settings.domain.gateway;

import com.baomidou.mybatisplus.core.metadata.IPage;
import top.wyhao.settings.domain.model.SysConfig;

import java.util.List;

/**
 * 系统配置仓储接口
 */
public interface ConfigRepository {

    /**
     * 根据 ID 查询
     *
     * @param id ID
     * @return 配置信息
     */
    SysConfig findById(Long id);

    /**
     * 根据配置键查询
     *
     * @param configKey 配置键
     * @return 配置信息
     */
    SysConfig findByKey(String configKey);

    /**
     * 插入
     *
     * @param config 配置信息
     */
    void insert(SysConfig config);

    /**
     * 按 ID 更新
     *
     * @param config 配置信息
     * @return 更新行数
     */
    int updateById(SysConfig config);

    /**
     * 批量删除
     *
     * @param ids ID 列表
     */
    void deleteByIds(List<Long> ids);

    /**
     * 按条件查询列表
     *
     * @param query 查询条件
     * @return 配置列表
     */
    List<SysConfig> listByQuery(Object query);

    /**
     * 分页查询
     *
     * @param page     页码
     * @param pageSize 每页条数
     * @param query    查询条件
     * @return 分页结果
     */
    IPage<SysConfig> page(long page, long pageSize, Object query);

    /**
     * 检查配置键是否存在
     *
     * @param configKey 配置键
     * @param excludeId 排除 ID
     * @return 是否存在
     */
    boolean existsKey(String configKey, Long excludeId);

    /**
     * 获取配置并转换为指定类型
     *
     * @param configKey 配置键
     * @param type      目标类型
     * @return 配置对象
     */
    <T> T getAs(String configKey, Class<T> type);
}
