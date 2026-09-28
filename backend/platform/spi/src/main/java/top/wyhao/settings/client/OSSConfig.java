package top.wyhao.settings.client;

import lombok.Data;

/**
 * 对象存储配置（跨模块契约）
 */
@Data
public class OSSConfig {

    private String endpoint;

    private String accessKeyId;

    private String accessKeySecret;

    private String bucketName;
}
