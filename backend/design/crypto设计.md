# Crypto 包设计规范

## 1. 目标

提供统一、无状态的 Java 加密基础工具，封装：

* Digest：MD5 / SHA-1 / SHA-256 / SHA-512
* HMAC：HmacSHA256 / HmacSHA512
* AES：AES-GCM
* RSA：加密 / 解密 / 签名 / 验签
* Base64：标准 / URL Safe
* Hex：编码 / 解码

工具类仅负责算法封装，不负责密钥存储、密钥生命周期管理或业务逻辑。

---

## 2. 包结构

```text
com.xxx.common.crypto
├── DigestUtil.java
├── HmacUtil.java
├── EncryptUtil.java
├── RsaUtil.java
├── Base64Util.java
└── HexUtil.java
```

所有工具类：

* `final class`
* 私有构造方法
* 静态方法
* 无实例状态
* 默认 UTF-8
* 二进制结果优先使用 `byte[]`
* String 结果统一使用 Base64 或 Hex 表示

---

## 3. DigestUtil

### 职责

提供不可逆消息摘要。

### 支持算法

```text
MD5
SHA-1
SHA-256
SHA-512
```

### API

```java
String md5(String text);
String md5(byte[] data);
String md5(InputStream input);

String sha1(String text);
String sha1(byte[] data);
String sha1(InputStream input);

String sha256(String text);
String sha256(byte[] data);
String sha256(InputStream input);

String sha512(String text);
String sha512(byte[] data);
String sha512(InputStream input);

String digest(String text, String algorithm);
String digest(byte[] data, String algorithm);
String digest(InputStream input, String algorithm);
```

### 约束

* 默认返回 lowercase Hex。
* SHA-256 为默认推荐摘要算法。
* MD5 / SHA-1 仅用于兼容或非安全场景。
* 不用于密码存储。

---

## 4. HmacUtil

### 职责

提供基于密钥的消息认证码。

### 支持算法

```text
HmacSHA256
HmacSHA512
```

### API

```java
String hmacSha256(String data, String secret);
String hmacSha256(byte[] data, byte[] secret);

String hmacSha512(String data, String secret);
String hmacSha512(byte[] data, byte[] secret);

String hmac(
    String data,
    String secret,
    String algorithm
);

String hmac(
    byte[] data,
    byte[] secret,
    String algorithm
);
```

### 约束

* 返回 lowercase Hex。
* Secret 不得硬编码。
* Secret 不由工具类保存。
* 用于 API 签名、Webhook 签名、服务间认证等。

---

## 5. EncryptUtil

### 职责

提供对称加密。

### 算法

```text
AES/GCM/NoPadding
```

### 参数

```text
Key: 16 / 24 / 32 bytes
IV: 12 bytes
Authentication Tag: 128 bits
```

### API

```java
String aesEncrypt(String plaintext, byte[] key);
byte[] aesEncrypt(byte[] plaintext, byte[] key);

String aesDecrypt(String ciphertext, byte[] key);
byte[] aesDecrypt(byte[] ciphertext, byte[] key);
```

### Ciphertext 格式

```text
IV + Ciphertext + Authentication Tag
```

String 结果：

```text
Base64(IV + Ciphertext + Tag)
```

### 约束

* 每次加密必须生成新的随机 IV。
* 使用 `SecureRandom`。
* 禁止 AES/ECB。
* 不允许复用相同 Key + IV。
* 不负责 Key 的生成、保存和管理。

---

## 6. RsaUtil

### 职责

提供 RSA 非对称加密及数字签名。

### 加密算法

```text
RSA/ECB/OAEPWithSHA-256AndMGF1Padding
```

### 签名算法

```text
SHA256withRSA
```

### API

```java
// Key
PublicKey publicKey(String base64);
PrivateKey privateKey(String base64);

// Encrypt
String encrypt(String plaintext, String publicKey);
byte[] encrypt(byte[] data, PublicKey publicKey);

// Decrypt
String decrypt(String ciphertext, String privateKey);
byte[] decrypt(byte[] data, PrivateKey privateKey);

// Sign
String sign(String data, String privateKey);
byte[] sign(byte[] data, PrivateKey privateKey);

// Verify
boolean verify(
    String data,
    String signature,
    String publicKey
);

boolean verify(
    byte[] data,
    byte[] signature,
    PublicKey publicKey
);
```

### Key 格式

公钥：

```text
X.509 / SubjectPublicKeyInfo
Base64
```

私钥：

```text
PKCS#8
Base64
```

### 约束

* RSA 不用于大数据或大文件直接加密。
* 大数据采用混合加密：

```text
Data
  ↓
AES-GCM
  ↓
Encrypted Data

AES Key
  ↓
RSA-OAEP
  ↓
Encrypted AES Key
```

---

## 7. Base64Util

### 职责

提供 Base64 编解码。

### API

```java
String encode(byte[] data);
String encode(String text);

byte[] decode(String value);
String decodeToString(String value);

String encodeUrlSafe(byte[] data);
byte[] decodeUrlSafe(String value);
```

### 实现

使用 JDK：

```java
java.util.Base64
```

### 约束

* 标准 Base64：`Base64.getEncoder()`
* URL Safe：`Base64.getUrlEncoder().withoutPadding()`
* Base64 不是加密算法。

---

## 8. HexUtil

### 职责

提供 byte[] 与 Hex 字符串转换。

### API

```java
String encode(byte[] bytes);
byte[] decode(String hex);
```

### 约束

* 默认 lowercase Hex。
* 不承担加密或摘要职责。

---

## 9. 职责边界

| 工具            | 算法          | 可逆 | Key | 典型用途     |
| ------------- | ----------- | -: | --: | -------- |
| `DigestUtil`  | SHA-256     |  否 |   否 | 文件摘要、完整性 |
| `HmacUtil`    | HMAC-SHA256 |  否 |   是 | API 签名   |
| `EncryptUtil` | AES-GCM     |  是 |   是 | 敏感数据、文件  |
| `RsaUtil`     | RSA-OAEP    |  是 |   是 | 密钥加密     |
| `RsaUtil`     | RSA-SHA256  |  否 |   是 | 数字签名     |
| `Base64Util`  | Base64      |  是 |   否 | 二进制传输编码  |
| `HexUtil`     | Hex         |  是 |   否 | 二进制编码    |

---

## 10. 安全约束

### 禁止

```text
AES/ECB
RSA/PKCS1Padding（新业务）
SHA-256(password)
MD5(password)
SHA-1(password)
硬编码 Secret / Key
固定 AES IV
重复使用 AES Key + IV
```

### 密码

密码必须使用专用密码哈希：

```text
BCrypt
Argon2
```

不要使用 `DigestUtil`。

---

## 11. 实现原则

1. 优先使用 JDK 标准 `java.security` / `javax.crypto`。
2. 不引入额外加密库，除非存在明确算法需求。
3. 工具类保持无状态。
4. 不在工具类中管理密钥。
5. String 输入统一 UTF-8。
6. 加密结果统一 Base64。
7. 摘要/HMAC 结果统一 lowercase Hex。
8. 所有工具类必须提供参数校验。
9. 加密失败抛出明确的运行时异常。
10. 为每个工具类提供单元测试。

---

## 12. AI Agent 实现要求

AI Agent 实现 crypto 模块时必须遵循：

```text
DigestUtil   → 摘要
HmacUtil     → HMAC
EncryptUtil  → AES-GCM
RsaUtil      → RSA
Base64Util   → Base64
HexUtil      → Hex
```

不得：

* 创建统一的 `CryptoUtil` 大杂烩类；
* 在工具类中保存密钥；
* 修改既定算法参数；
* 使用弱加密模式；
* 使用摘要算法替代密码哈希；
* 将 RSA 用于大文件加密；
* 将 Base64/Hex 误认为加密算法。

### 推荐依赖关系

```text
HexUtil
   ↑
DigestUtil

Base64Util
   ↑
EncryptUtil
   ↑
RsaUtil

HmacUtil
   └── 独立
```

`HexUtil` 和 `Base64Util` 作为最底层编码工具，其他工具按需依赖，避免循环依赖。

### 单体测试设计
1. HexUtilTest - 十六进制编码工具测试
* 测试编码和解码功能
* 测试 null 输入处理
* 测试非法长度输入
* 测试编码解码往返
2. Base64UtilTest - Base64 编码工具测试
* 测试标准 Base64 编码和解码
* 测试 URL Safe Base64 编码和解码
* 测试字符串和字节数组支持
* 测试中文字符处理
* 测试 null 和非法输入处理
* 测试编码解码往返
3. DigestUtilTest - 消息摘要工具测试
* 测试 MD5、SHA-1、SHA-256、SHA-512 算法
* 测试字符串、字节数组和输入流支持
* 测试固定摘要值验证
* 测试不支持的算法处理
* 测试一致性（相同输入产生相同输出）
* 测试差异性（不同输入产生不同输出）
4. HmacUtilTest - HMAC 工具测试
* 测试 HmacSHA256 和 HmacSHA512 算法
* 测试字符串和字节数组支持
* 测试 HMAC 验证功能（包含防时序攻击）
* 测试不同密钥和数据产生不同输出
* 测试修改数据或密钥后验证失败
5. EncryptUtilTest - AES-GCM 加密工具测试
* 测试密钥生成（128/192/256 位）
* 测试字符串和字节数组加密解密
* 测试不同密钥大小
* 测试随机 IV（每次加密产生不同密文）
* 测试长文本、空字符串、中文、特殊字符
* 测试错误密钥解密失败
* 测试 null 和非法输入处理
6. RsaUtilTest - RSA 加密签名工具测试
* 测试密钥对生成
* 测试密钥格式转换
* 测试 RSA 加密和解密
* 测试 RSA 签名和验证
* 测试中文文本支持
* 测试错误密钥解密/验证失败
* 测试随机填充（相同明文产生不同密文）
* 测试 null 和非法输入处理
* 测试覆盖要点

所有测试类都覆盖了：
- ✅ 正常功能测试 - 验证核心功能正确性
- ✅ 边界条件测试 - 空字符串、空数组等
- ✅ 异常处理测试 - null 输入、非法参数
- ✅ 一致性测试 - 相同输入产生相同输出
- ✅ 安全性测试 - 错误密钥、修改数据等
- ✅ 国际化测试 - 中文字符、特殊字符
- ✅ 往返测试 - 编码/解码、加密/解密