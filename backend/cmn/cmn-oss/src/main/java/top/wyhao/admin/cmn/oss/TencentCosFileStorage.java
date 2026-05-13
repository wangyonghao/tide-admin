package top.wyhao.admin.cmn.oss;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.model.COSObject;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.region.Region;
import lombok.extern.slf4j.Slf4j;
import okio.Path;
import top.wyhao.starter.core.exception.SystemException;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.file.Paths;

/**
 * 腾讯云COS文件存储
 * <a href="https://cloud.tencent.com/document/product/436/10199">腾讯云对象存储SDK文档</a>
 *
 */
@Slf4j
public class TencentCosFileStorage implements FileStorage {

    public static final String STORAGE_TYPE = "tencent";

    /*
     * COSClient 是线程安全的类，允许多线程访问同一实例。
     * 因为实例内部维持了一个连接池，创建多个实例可能导致程序资源耗尽，请确保程序生命周期内实例只有一个，
     * 并在不再需要使用时，调用 shutdown 方法将其关闭。如果需要新建实例，请先将之前的实例关闭。
     */
    private COSClient cosClient;
    private String bucketName;
    private String domain;

    @Override
    public String getStorageType() {
        return STORAGE_TYPE;
    }

    /**
     * 初始化配置
     * <p>
     * <a href="https://console.cloud.tencent.com/cam/capi">腾讯云访问管理控制台</a>
     * </p>
     *
     * @param secretId   腾讯云SecretId
     * @param secretKey  腾讯云SecretKey
     * @param region     地域，如 ap-guangzhou
     * @param bucketName 存储桶名称
     * @param domain     访问域名
     */
    public void init(String secretId, String secretKey, String region, String bucketName, String domain) {
        this.bucketName = bucketName;
        this.domain = domain;

        // 初始化身份信息
        COSCredentials credentials = new BasicCOSCredentials(secretId, secretKey);
        // 设置地域
        ClientConfig clientConfig = new ClientConfig(new Region(region));
        // 创建COS客户端
        this.cosClient = new COSClient(credentials, clientConfig);

        // 检查存储桶是否存在
        try {
            if (!cosClient.doesBucketExist(bucketName)) {
                cosClient.createBucket(bucketName);
                log.info("腾讯云COS存储桶创建成功: {}", bucketName);
            }
            log.info("腾讯云COS存储初始化完成, region: {}, bucket: {}", region, bucketName);
        } catch (Exception e) {
            log.error("腾讯云COS初始化失败", e);
        }
    }

    @Override
    public String upload(InputStream inputStream, String path, String fileName) {
        try {
            String objectKey = path + "/" + fileName;

            ObjectMetadata metadata = new ObjectMetadata();
            // 设置内容长度（如果已知可以设置，否则SDK会自动处理）
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectKey, inputStream, metadata);
            cosClient.putObject(putObjectRequest);

            return getUrl(objectKey);
        } catch (Exception e) {
            log.error("腾讯云COS文件上传失败", e);
            throw new SystemException("文件上传失败", e);
        }
    }

    @Override
    public void delete(String path) {
        try {
            cosClient.deleteObject(bucketName, path);
        } catch (Exception e) {
            log.error("腾讯云COS文件删除失败: {}", path, e);
            throw new SystemException("文件删除失败", e);
        }
    }

    @Override
    public byte[] getFile(String path) {
        try {
            COSObject cosObject = cosClient.getObject(bucketName, path);
            try (InputStream inputStream = cosObject.getObjectContent()) {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                return outputStream.toByteArray();
            }
        } catch (Exception e) {
            log.error("腾讯云COS文件读取失败: {}", path, e);
            throw new SystemException("文件读取失败", e);
        }
    }

    @Override
    public InputStream getFileInputStream(String path) {
        try {
            COSObject cosObject = cosClient.getObject(bucketName, path);
            return cosObject.getObjectContent();
        } catch (Exception e) {
            log.error("腾讯云COS文件读取失败: {}", path, e);
            throw new SystemException("文件读取失败", e);
        }
    }

    @Override
    public String getUrl(String path) {
        String normalizedPath = path.replace("\\", "/");
        if (!normalizedPath.startsWith("/")) {
            normalizedPath = "/" + normalizedPath;
        }
        return domain + normalizedPath;
    }

    @Override
    public boolean exists(String path) {
        try {
            return cosClient.doesObjectExist(bucketName, path);
        } catch (Exception e) {
            log.error("腾讯云COS检查文件存在失败: {}", path, e);
            throw new SystemException("检查文件存在失败", e);
        }
    }

    /**
     * 关闭COS客户端
     */
    public void shutdown() {
        if (cosClient != null) {
            cosClient.shutdown();
        }
    }
}
