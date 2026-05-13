
package top.wyhao.admin.system.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import top.wyhao.admin.system.entity.SysFile;
import top.wyhao.starter.data.mapper.BaseMapper;

import java.util.List;

/**
 * 文件 Mapper
 *
 * @author Charles7c
 * @since 2023/12/23 10:38
 */
@Mapper
public interface FileMapper extends BaseMapper<SysFile> {
}