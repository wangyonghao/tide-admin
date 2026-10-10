# OTP 验证码模板清单

## 模板目录结构

```
src/main/resources/templates/otp/
├── email/                          # 邮件模板
│   ├── register_zh_CN.txt         # 注册验证码（中文）
│   ├── register_en_US.txt         # 注册验证码（英文）
│   ├── login_zh_CN.txt            # 登录验证码（中文）
│   ├── login_en_US.txt            # 登录验证码（英文）
│   ├── reset_password_zh_CN.txt   # 重置密码（中文）
│   ├── reset_password_en_US.txt   # 重置密码（英文）
│   ├── bind_email_zh_CN.txt       # 绑定邮箱（中文）
│   ├── bind_email_en_US.txt       # 绑定邮箱（英文）
│   ├── bind_phone_zh_CN.txt       # 绑定手机（中文）
│   ├── bind_phone_en_US.txt       # 绑定手机（英文）
│   ├── change_email_zh_CN.txt     # 修改邮箱（中文）
│   ├── change_email_en_US.txt     # 修改邮箱（英文）
│   ├── change_phone_zh_CN.txt     # 修改手机（中文）
│   ├── change_phone_en_US.txt     # 修改手机（英文）
│   ├── delete_account_zh_CN.txt   # 注销账号（中文）
│   └── delete_account_en_US.txt   # 注销账号（英文）
└── sms/                            # 短信模板
    ├── register_zh_CN.txt         # 注册验证码（中文）
    ├── register_en_US.txt         # 注册验证码（英文）
    ├── login_zh_CN.txt            # 登录验证码（中文）
    ├── login_en_US.txt            # 登录验证码（英文）
    ├── reset_password_zh_CN.txt   # 重置密码（中文）
    ├── reset_password_en_US.txt   # 重置密码（英文）
    ├── bind_email_zh_CN.txt       # 绑定邮箱（中文）
    ├── bind_email_en_US.txt       # 绑定邮箱（英文）
    ├── bind_phone_zh_CN.txt       # 绑定手机（中文）
    ├── bind_phone_en_US.txt       # 绑定手机（英文）
    ├── change_email_zh_CN.txt     # 修改邮箱（中文）
    ├── change_email_en_US.txt     # 修改邮箱（英文）
    ├── change_phone_zh_CN.txt     # 修改手机（中文）
    ├── change_phone_en_US.txt     # 修改手机（英文）
    ├── delete_account_zh_CN.txt   # 注销账号（中文）
    └── delete_account_en_US.txt   # 注销账号（英文）
```

## 模板统计

- **总计**：32 个模板文件
- **邮件模板**：16 个（8 个场景 × 2 种语言）
- **短信模板**：16 个（8 个场景 × 2 种语言）
- **支持语言**：中文（zh_CN）、英文（en_US）
- **支持场景**：8 个业务场景

## 场景与模板映射

| 场景枚举 | 场景名称 | 中文邮件模板 | 英文邮件模板 | 中文短信模板 | 英文短信模板 |
|---------|---------|------------|------------|------------|------------|
| `LOGIN` | 登录验证 | ✅ | ✅ | ✅ | ✅ |
| `REGISTER` | 注册验证 | ✅ | ✅ | ✅ | ✅ |
| `BIND_EMAIL` | 绑定邮箱 | ✅ | ✅ | ✅ | ✅ |
| `BIND_PHONE` | 绑定手机 | ✅ | ✅ | ✅ | ✅ |
| `RESET_PASSWORD` | 重置密码 | ✅ | ✅ | ✅ | ✅ |
| `CHANGE_EMAIL` | 修改邮箱 | ✅ | ✅ | ✅ | ✅ |
| `CHANGE_PHONE` | 修改手机 | ✅ | ✅ | ✅ | ✅ |
| `DELETE_ACCOUNT` | 注销账号 | ✅ | ✅ | ✅ | ✅ |

## 模板命名规范

### 命名格式

```
{scene}_{locale}.txt
```

### 参数说明

- `{scene}`: 场景名称（小写，下划线分隔）
  - `register` - 注册
  - `login` - 登录
  - `reset_password` - 重置密码
  - `bind_email` - 绑定邮箱
  - `bind_phone` - 绑定手机
  - `change_email` - 修改邮箱
  - `change_phone` - 修改手机
  - `delete_account` - 注销账号

- `{locale}`: 语言代码
  - `zh_CN` - 简体中文
  - `en_US` - 英文（美国）
  - 可扩展：`ja_JP`（日语）、`ko_KR`（韩语）等

### 示例

- `register_zh_CN.txt` - 注册场景的中文模板
- `login_en_US.txt` - 登录场景的英文模板

## 模板占位符

所有模板支持以下占位符：

| 占位符 | 说明 | 示例值 | 使用场景 |
|--------|------|--------|---------|
| `{code}` | 验证码 | `123456` | 所有模板 |
| `{expires_in}` | 有效期（分钟） | `5` | 所有模板 |
| `{target}` | 目标地址（脱敏） | `u***@example.com` | 邮件模板（可选） |
| `{timestamp}` | 发送时间 | `2024-01-01 12:00:00` | 邮件模板 |
| `{ip}` | 请求 IP | `192.168.1.100` | 邮件模板（可选） |

## 邮件模板示例

### 中文邮件模板（register_zh_CN.txt）

```
【WYH Admin】注册验证码

尊敬的用户：

您正在注册 WYH Admin 账号，验证码为：

{code}

验证码有效期为 {expires_in} 分钟，请勿泄露给他人。

如非本人操作，请忽略此邮件。

---
WYH Admin 团队
{timestamp}
```

### 英文邮件模板（register_en_US.txt）

```
[WYH Admin] Registration Verification Code

Dear User,

You are registering a WYH Admin account. Your verification code is:

{code}

This code will expire in {expires_in} minutes. Please do not share it with others.

If this was not you, please ignore this email.

---
WYH Admin Team
{timestamp}
```

## 短信模板示例

### 中文短信模板（register_zh_CN.txt）

```
【WYH Admin】您的注册验证码为：{code}，{expires_in}分钟内有效，请勿泄露。
```

### 英文短信模板（register_en_US.txt）

```
[WYH Admin] Your registration verification code is: {code}, valid for {expires_in} minutes. Do not share.
```

## 模板加载逻辑

### 路径解析规则

```java
String templatePath = String.format("%s/%s/%s_%s.txt",
    basePath,           // templates/otp
    channel,            // email 或 sms
    scene,              // register, login 等
    locale              // zh_CN, en_US 等
);
```

### 降级策略

1. **首选**：加载指定语言的模板
   - 例如：`templates/otp/email/register_en_US.txt`

2. **降级**：如果指定语言模板不存在，降级到默认语言（zh_CN）
   - 例如：`templates/otp/email/register_zh_CN.txt`

3. **兜底**：如果默认语言模板也不存在，使用通用模板
   - 邮件：`【WYH Admin】验证码\n\n您的验证码为：{code}...`
   - 短信：`【WYH Admin】您的验证码为：{code}，{expires_in}分钟内有效，请勿泄露。`

## 添加新语言

### 步骤

1. **创建模板文件**

为每个场景创建新语言的模板文件：

```bash
# 日语示例
touch templates/otp/email/register_ja_JP.txt
touch templates/otp/email/login_ja_JP.txt
# ... 其他场景

touch templates/otp/sms/register_ja_JP.txt
touch templates/otp/sms/login_ja_JP.txt
# ... 其他场景
```

2. **编写模板内容**

参考现有模板格式，使用目标语言编写内容。

3. **使用新语言**

发送请求时指定 `locale` 参数：

```json
{
  "channel": "EMAIL",
  "scene": "REGISTER",
  "target": "user@example.com",
  "locale": "ja_JP"
}
```

### 日语模板示例

**email/register_ja_JP.txt**

```
【WYH Admin】登録確認コード

ユーザー様

WYH Admin アカウントを登録しています。確認コードは：

{code}

このコードは {expires_in} 分間有効です。他人に共有しないでください。

ご本人でない場合は、このメールを無視してください。

---
WYH Admin チーム
{timestamp}
```

**sms/register_ja_JP.txt**

```
【WYH Admin】登録確認コード：{code}、{expires_in}分間有効です。共有しないでください。
```

## 添加新场景

### 步骤

1. **在枚举中添加场景**

编辑 `OtpScene.java`：

```java
public enum OtpScene {
    // ... 现有场景
    
    /**
     * 实名认证
     */
    REAL_NAME_AUTH("实名认证");
}
```

2. **创建模板文件**

为新场景创建所有语言和渠道的模板：

```bash
# 中文模板
touch templates/otp/email/real_name_auth_zh_CN.txt
touch templates/otp/sms/real_name_auth_zh_CN.txt

# 英文模板
touch templates/otp/email/real_name_auth_en_US.txt
touch templates/otp/sms/real_name_auth_en_US.txt
```

3. **编写模板内容**

参考现有模板格式编写内容。

4. **使用新场景**

```json
{
  "channel": "EMAIL",
  "scene": "REAL_NAME_AUTH",
  "target": "user@example.com"
}
```

## 模板最佳实践

### 邮件模板

1. **结构清晰**
   - 使用明确的标题
   - 验证码单独一行，突出显示
   - 包含有效期说明
   - 添加安全提示

2. **内容完整**
   - 说明操作场景
   - 提供验证码
   - 说明有效期
   - 安全提示
   - 团队签名
   - 时间戳

3. **用户友好**
   - 语言礼貌、专业
   - 避免技术术语
   - 提供明确的操作指引

### 短信模板

1. **简洁明了**
   - 控制在 70 字符以内（中文）
   - 包含必要信息：验证码、有效期、安全提示

2. **格式规范**
   - 使用【】标注品牌名称
   - 验证码突出显示
   - 避免特殊符号

3. **成本优化**
   - 精简文字
   - 避免冗余信息

## 模板测试

### 测试清单

- [ ] 所有占位符正确替换
- [ ] 中文模板无乱码
- [ ] 英文模板语法正确
- [ ] 邮件格式美观
- [ ] 短信长度合适
- [ ] 降级策略正常工作

### 测试方法

```bash
# 测试中文邮件
curl -X POST http://localhost:8000/api/v1/otp/send \
  -H "Content-Type: application/json" \
  -d '{"channel":"EMAIL","scene":"REGISTER","target":"test@example.com","locale":"zh_CN"}'

# 测试英文邮件
curl -X POST http://localhost:8000/api/v1/otp/send \
  -H "Content-Type: application/json" \
  -d '{"channel":"EMAIL","scene":"REGISTER","target":"test@example.com","locale":"en_US"}'

# 测试不存在的语言（应降级到 zh_CN）
curl -X POST http://localhost:8000/api/v1/otp/send \
  -H "Content-Type: application/json" \
  -d '{"channel":"EMAIL","scene":"REGISTER","target":"test@example.com","locale":"fr_FR"}'
```

## 维护建议

1. **版本控制**
   - 所有模板文件纳入 Git 管理
   - 模板修改需要 Code Review

2. **文档同步**
   - 新增场景/语言时更新本文档
   - 保持模板清单的准确性

3. **定期审查**
   - 定期检查模板内容的准确性
   - 根据用户反馈优化文案

4. **多语言协作**
   - 新增语言时寻求母语者帮助
   - 确保翻译的准确性和地道性

---

**文档版本**: v1.0  
**最后更新**: 2024-01-01  
**维护者**: WYH Admin 团队
