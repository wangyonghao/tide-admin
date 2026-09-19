package top.wyhao.file.core.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.wyhao.file.core.domain.File;

/**
 * 文件 Mapper 接口
 *
 * @author wyh
 * @since 2026/09/16
 */
@Mapper
public interface FileRepository extends BaseMapper<File> {

}
