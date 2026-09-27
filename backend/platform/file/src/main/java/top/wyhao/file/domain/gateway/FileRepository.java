package top.wyhao.file.domain.gateway;

import com.baomidou.mybatisplus.core.metadata.IPage;
import top.wyhao.file.domain.enums.FileCategory;
import top.wyhao.file.domain.model.File;

/**
 * 文件仓储
 */
public interface FileRepository {

    void insert(File file);

    File findById(Long id);

    void updateById(File file);

    /**
     * 分页查询可访问文件
     *
     * @param fileName 文件名（模糊，可空）
     * @param category 分类
     * @param asc      是否按创建时间升序
     * @param page     页码
     * @param size     每页条数
     */
    IPage<File> page(String fileName, FileCategory category, boolean asc, long page, long size);
}
