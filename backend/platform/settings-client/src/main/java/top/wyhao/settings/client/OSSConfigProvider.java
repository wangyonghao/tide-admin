package top.wyhao.settings.client;

import top.wyhao.cmn.core.model.OSSConfig;

public interface OSSConfigProvider {
    OSSConfig getOSSConfig(String key);

    String getStorageProvider();

    String getStorageLocalPath();

    String getStorageEndPoint();

    String getStorageMinioEndpoint();

    String getStorageMinioAccessKey();

    String getStorageMinioSecretKey();

    String getStorageMinioBucket();

    String getStorageRustfsEndpoint();

    String getStorageRustfsAccessKey();

    String getStorageRustfsSecretKey();

    String getStorageRustfsBucket();

    String getStorageAliyunEndpoint();

    String getStorageAliyunAccessKey();

    String getStorageAliyunSecretKey();

    String getStorageAliyunBucket();

    String getStorageTencentSecretId();

    String getStorageTencentSecretKey();

    String getStorageTencentRegion();

    String getStorageTencentBucket();
}
