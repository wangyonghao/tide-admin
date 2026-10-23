package top.wyhao.admin.system.file.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.wyhao.admin.system.file.domain.model.File;

/**
 * 文件 Mapper
 *
 * @author wyh
 * @since 2026/09/16
 */
@Mapper
public interface FileMapper extends BaseMapper<File> {

}
