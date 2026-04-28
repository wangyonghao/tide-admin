
package top.wyhao.admin.system.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import top.wyhao.admin.system.model.entity.FileDO;
import top.wyhao.admin.system.model.vo.file.FileStatisticsResp;
import top.wyhao.starter.data.mapper.BaseMapper;

import java.util.List;

/**
 * 文件 Mapper
 *
 * @author Charles7c
 * @since 2023/12/23 10:38
 */
@Mapper
public interface FileMapper extends BaseMapper<FileDO> {

    /**
     * 查询文件资源统计信息
     *
     * @return 文件资源统计信息
     */
    @Select("SELECT type, COUNT(1) number, SUM(size) size FROM sys_file WHERE type != 0 GROUP BY type")
    List<FileStatisticsResp> statistics();

    default Long countByStorageIds(List<Long> fileStorageIds) {
        return this.lambdaQuery().in(FileDO::getStorageId, fileStorageIds).count();
    }
}