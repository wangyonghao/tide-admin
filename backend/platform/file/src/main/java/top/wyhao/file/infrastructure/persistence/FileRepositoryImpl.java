package top.wyhao.file.infrastructure.persistence;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import top.wyhao.file.domain.enums.FileCategory;
import top.wyhao.file.domain.enums.FileStatus;
import top.wyhao.file.domain.gateway.FileRepository;
import top.wyhao.file.domain.model.File;
import top.wyhao.file.infrastructure.persistence.mapper.FileMapper;

/**
 * 文件仓储实现
 */
@Repository
@RequiredArgsConstructor
public class FileRepositoryImpl implements FileRepository {

    private final FileMapper fileMapper;

    @Override
    public void insert(File file) {
        fileMapper.insert(file);
    }

    @Override
    public File findById(Long id) {
        return fileMapper.selectById(id);
    }

    @Override
    public void updateById(File file) {
        fileMapper.updateById(file);
    }

    @Override
    public IPage<File> page(String fileName, FileCategory category, boolean asc, long page, long size) {
        var query = new LambdaQueryWrapper<File>()
                .eq(File::getStatus, FileStatus.ACTIVE)
                .like(StrUtil.isNotBlank(fileName), File::getFileName, fileName);

        if (category != FileCategory.ALL && !category.getExtensions().isEmpty()) {
            query.and(wrapper -> {
                boolean first = true;
                for (String ext : category.getExtensions()) {
                    if (first) {
                        wrapper.likeLeft(File::getFileName, "." + ext);
                        first = false;
                    } else {
                        wrapper.or().likeLeft(File::getFileName, "." + ext);
                    }
                }
            });
        }

        if (asc) {
            query.orderByAsc(File::getCreateTime);
        } else {
            query.orderByDesc(File::getCreateTime);
        }
        return fileMapper.selectPage(new Page<>(page, size), query);
    }
}
