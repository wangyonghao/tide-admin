
package top.wyhao.starter.encrypt.field.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;
import top.wyhao.starter.core.constant.PropertiesConstants;
import top.wyhao.starter.encrypt.enums.Algorithm;

/**
 * 字段加密配置属性
 *
 * @author Charles7c
 * @author lishuyan
 * @since 1.4.0
 */
@ConfigurationProperties(PropertiesConstants.ENCRYPT_FIELD)
public class FieldEncryptProperties {

    /**
     * 是否启用
     */
    private Boolean enabled;

    /**
     * 默认算法
     */
    private Algorithm algorithm = Algorithm.AES;

    /**
     * 对称加密算法密钥
     */
    private String password;

    /**
     * 非对称加密算法公钥
     */
    private String publicKey;

    /**
     * 非对称加密算法私钥
     */
    private String privateKey;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Algorithm getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(Algorithm algorithm) {
        this.algorithm = algorithm;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }
}