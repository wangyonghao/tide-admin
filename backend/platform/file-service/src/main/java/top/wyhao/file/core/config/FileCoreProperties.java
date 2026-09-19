package top.wyhao.file.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import top.wyhao.storage.api.StorageType;

/**
 * 文件核心配置属性
 *
 * @author wyh
 * @since 2026/09/16
 */
@Data
@Component
@ConfigurationProperties(prefix = "file")
public class FileCoreProperties {

    /**
     * 存储配置
     */
    private StorageConfig storage = new StorageConfig();

    @Data
    public static class StorageConfig {
        /**
         * 默认存储类型
         */
        private StorageType type = StorageType.LOCAL;

        /**
         * 存储键前缀（可空）
         *
         * <p>最终 key 格式：[{key-prefix}/]{yyyy}/{MM}/{dd}/{uuid}[.ext]
         * <p>示例配置 {@code files} → {@code files/2026/09/18/abc123.pdf}
         */
        private String keyPrefix = "files";
    }
}
