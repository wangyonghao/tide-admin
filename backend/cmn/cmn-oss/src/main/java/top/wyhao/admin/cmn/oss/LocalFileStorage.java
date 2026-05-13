package top.wyhao.admin.cmn.oss;

import lombok.extern.slf4j.Slf4j;
import top.wyhao.starter.core.exception.SystemException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * 本地文件存储
 */
@Slf4j
public class LocalFileStorage implements FileStorage {

    public static final String STORAGE_TYPE = "local";

    private String basePath;
    private String domain;

    @Override
    public String getStorageType() {
        return STORAGE_TYPE;
    }

    /**
     * 初始化配置
     */
    public void init(String basePath, String domain) {
        this.basePath = basePath;
        this.domain = domain;

        // 确保基础目录存在
        try {
            Path path = Paths.get(basePath).normalize();
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
            log.info("本地存储初始化完成, 基础目录: {}", basePath);
        } catch (IOException e) {
            log.error("创建本地存储目录失败: {}", basePath, e);
        }
    }

    @Override
    public String upload(InputStream inputStream, String path, String fileName) {
        try {
            // 构建完整路径
            Path fullPath = Paths.get(basePath, path, fileName);
            // 确保父目录存在
            Files.createDirectories(fullPath.getParent());
            // 复制文件
            Files.copy(inputStream, fullPath, StandardCopyOption.REPLACE_EXISTING);
            // 返回访问URL
            String relativePath = path + "/" + fileName;
            return getUrl(relativePath);
        } catch (IOException e) {
            log.error("本地文件上传失败", e);
            throw new RuntimeException("文件上传失败", e);
        }
    }

    @Override
    public void delete(String path) {
        try {
            Path fullPath = Paths.get(basePath, path);
            Files.deleteIfExists(fullPath);
        } catch (IOException e) {
            log.error("本地文件删除失败: {}", path, e);
        }
    }

    @Override
    public byte[] getFile(String path) {
        try {
            Path fullPath = Paths.get(basePath, path);
            return Files.readAllBytes(fullPath);
        } catch (IOException e) {
            log.error("本地文件读取失败: {}", path, e);
            throw new SystemException("文件读取失败", e);
        }
    }

    @Override
    public InputStream getFileInputStream(String path) {
        try {
            Path fullPath = Paths.get(basePath, path);
            return Files.newInputStream(fullPath);
        } catch (IOException e) {
            log.error("本地文件读取失败: {}", path, e);
            throw new SystemException("文件读取失败", e);
        }
    }

    @Override
    public String getUrl(String path) {
        // 本地存储使用相对路径，由本服务器的 FileAccessController @RequestMapping("/fs/**") 提供访问
        return Paths.get(domain, path).normalize().toString();
    }

    @Override
    public boolean exists(String path) {
        Path fullPath = Paths.get(basePath, path);
        return Files.exists(fullPath);
    }

}
