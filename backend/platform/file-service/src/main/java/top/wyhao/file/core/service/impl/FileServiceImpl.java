package top.wyhao.file.core.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import top.wyhao.file.core.config.FileCoreProperties;
import top.wyhao.file.core.domain.File;
import top.wyhao.file.core.enums.FileCategory;
import top.wyhao.file.core.enums.FileStatus;
import top.wyhao.file.core.exception.FileException;
import top.wyhao.file.core.exception.FileNotFoundException;
import top.wyhao.file.core.repository.FileRepository;
import top.wyhao.file.core.service.FileService;
import top.wyhao.file.storage.StorageManager;
import top.wyhao.storage.api.StorageException;
import top.wyhao.storage.api.StorageObject;
import top.wyhao.storage.api.StorageType;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 文件服务实现
 *
 * @author wyh
 * @since 2026/09/16
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final FileRepository fileRepository;
    private final StorageManager storageManager;
    private final FileCoreProperties properties;

    @Override
    public File upload(MultipartFile file, Long operatorId) {
        if (file == null || file.isEmpty()) {
            throw new StorageException(StorageType.LOCAL, "上传文件不能为空");
        }

        String fileName = file.getOriginalFilename();
        String contentType = file.getContentType();
        long contentLength = file.getSize();

        try {
            return upload(fileName, contentType, contentLength, file.getInputStream(), operatorId);
        } catch (IOException e) {
            throw new StorageException(StorageType.LOCAL, "读取上传文件失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public File upload(String fileName, String contentType, long contentLength,
                       InputStream inputStream, Long operatorId) {
        // 生成存储键
        String storageKey = generateStorageKey(fileName);

        // 计算文件哈希值
        // 先读取流到内存，以便多次使用（计算哈希 + 上传）
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            IoUtil.copy(inputStream, baos);
        } catch (Exception e) {
            throw new StorageException("复制文件流失败", e);
        }
        byte[] data = baos.toByteArray();
        ByteArrayInputStream hashStream = new ByteArrayInputStream(data);
        String sha256 = calculateSha256(hashStream);

        // 上传到存储
        ByteArrayInputStream uploadStream = new ByteArrayInputStream(data);
        StorageType storageType = storageManager.getDefault().type();
        try {
            storageManager.getDefault().put(storageKey, uploadStream, contentLength, contentType);
        } catch (Exception e) {
            log.error("上传文件到存储失败: storageKey={}", storageKey, e);
            throw new StorageException(storageType, "上传文件到存储失败", e);
        }

        // 保存文件元数据
        File file = File.create(fileName, contentType, contentLength, sha256, storageType, storageKey);
        file.setCreateUser(operatorId);
        file.setUpdateUser(operatorId);

        try {
            fileRepository.insert(file);
        } catch (Exception e) {
            // 数据库保存失败，尝试删除已上传的文件
            log.error("保存文件元数据失败，尝试删除已上传的文件: storageKey={}", storageKey, e);
            try {
                storageManager.getDefault().delete(storageKey);
            } catch (Exception deleteEx) {
                log.error("删除已上传的文件失败: storageKey={}", storageKey, deleteEx);
            }
            throw new StorageException(storageType, "保存文件元数据失败", e);
        }

        log.info("文件上传成功: fileId={}, fileName={}, storageKey={}, sha256={}",
                file.getId(), fileName, storageKey, sha256);

        return file;
    }

    @Override
    public File get(Long fileId) {
        File file = fileRepository.selectById(fileId);
        if (file == null) {
            throw new FileNotFoundException(fileId);
        }
        return file;
    }

    @Override
    public InputStream download(Long fileId) {
        File file = get(fileId);

        if (!file.isAccessible()) {
            throw new FileNotFoundException(fileId);
        }

        StorageObject storageObject = storageManager.get(file.getStorageType()).get(file.getStorageKey());
        if (storageObject == null) {
            throw new FileNotFoundException(file.getStorageKey());
        }

        return storageObject.getInputStream();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long fileId, Long operatorId) {
        File file = get(fileId);

        if (FileStatus.ACTIVE.equals(file.getStatus())) {
            // 逻辑删除
            file.markDeleted();
            file.setUpdateUser(operatorId);
            fileRepository.updateById(file);

            log.info("文件逻辑删除成功: fileId={}, operatorId={}", fileId, operatorId);
        } else {
            log.warn("文件状态异常，无法删除: fileId={}, status={}", fileId, file.getStatus());
        }
    }

    @Override
    public boolean exists(Long fileId) {
        if (fileId == null) {
            return false;
        }
        File file = fileRepository.selectById(fileId);
        return file != null && file.isAccessible();
    }

    @Override
    public IPage<File> page(String fileName, String category, String sortOrder, long page, long size) {
        FileCategory fileCategory = FileCategory.of(category);
        boolean asc = "asc".equalsIgnoreCase(sortOrder);

        var query = new LambdaQueryWrapper<File>()
                .eq(File::getStatus, FileStatus.ACTIVE)
                .like(StrUtil.isNotBlank(fileName), File::getFileName, fileName);

        if (fileCategory != FileCategory.ALL && !fileCategory.getExtensions().isEmpty()) {
            query.and(wrapper -> {
                boolean first = true;
                for (String ext : fileCategory.getExtensions()) {
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
        return fileRepository.selectPage(new Page<>(page, size), query);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(List<Long> fileIds, Long operatorId) {
        if (fileIds == null || fileIds.isEmpty()) {
            return;
        }
        for (Long fileId : fileIds) {
            delete(fileId, operatorId);
        }
    }

    @Override
    public String calculateSha256(InputStream inputStream) {
        try {
            return DigestUtil.sha256Hex(inputStream);
        } catch (Exception e) {
            throw new FileException("计算文件哈希值失败", e);
        } finally {
            IoUtil.close(inputStream);
        }
    }

    /**
     * 生成存储键
     *
     * <p>格式：[{key-prefix}/]{yyyy}/{MM}/{dd}/{uuid}[.ext]
     * <p>前缀由 {@code file.storage.key-prefix} 配置，可空。
     *
     * @param fileName 原始文件名
     * @return 存储键
     */
    private String generateStorageKey(String fileName) {
        LocalDate now = LocalDate.now();
        String datePath = now.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String uuid = IdUtil.fastSimpleUUID();
        String extension = FileUtil.extName(fileName);

        String objectName = (extension != null && !extension.isEmpty())
                ? datePath + "/" + uuid + "." + extension
                : datePath + "/" + uuid;

        String prefix = properties.getStorage().getKeyPrefix();
        if (prefix == null || prefix.isBlank()) {
            return objectName;
        }
        String normalized = prefix.trim().replaceAll("^/+|/+$", "");
        return normalized.isEmpty() ? objectName : normalized + "/" + objectName;
    }
}
