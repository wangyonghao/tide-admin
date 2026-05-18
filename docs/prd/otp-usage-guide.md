# OTP 验证码系统使用指南

## 快速开始

### 1. 配置邮件服务

在 `application-dev.yml` 中配置邮件服务：

```yaml
spring:
  mail:
    host: smtp.example.com
    port: 587
    username: your-email@example.com
    password: your-password
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true

otp:
  channel:
    email:
      enabled: true
      from: noreply@example.com
      subject-prefix: "【WYH Admin】"
```

### 2. API 调用示例

#### 发送验证码

**请求**

```bash
curl -X POST http://localhost:8000/api/v1/otp/send \
  -H "Content-Type: application/json" \
  -d '{
    "channel": "EMAIL",
    "scene": "REGISTER",
    "target": "user@example.com",
    "locale": "zh_CN"
  }'
```

**响应**

```json
{
  "otp_uuid": "550e8400-e29b-41d4-a716-446655440000",
  "expires_in": 300,
  "message": "验证码已发送"
}
```

#### 验证验证码

**请求**

```bash
curl -X POST http://localhost:8000/api/v1/otp/verify \
  -H "Content-Type: application/json" \
  -d '{
    "otp_uuid": "550e8400-e29b-41d4-a716-446655440000",
    "code": "123456"
  }'
```

**响应**

```json
{
  "verified": true,
  "message": "验证成功"
}
```

## 业务场景

系统支持以下业务场景：

| 场景 | 枚举值 | 说明 |
|------|--------|------|
| 登录验证 | `LOGIN` | 用户登录时的二次验证 |
| 注册验证 | `REGISTER` | 新用户注册时验证邮箱/手机号 |
| 绑定邮箱 | `BIND_EMAIL` | 绑定新邮箱时验证 |
| 绑定手机 | `BIND_PHONE` | 绑定新手机号时验证 |
| 重置密码 | `RESET_PASSWORD` | 忘记密码时验证身份 |
| 修改邮箱 | `CHANGE_EMAIL` | 修改邮箱时验证 |
| 修改手机 | `CHANGE_PHONE` | 修改手机号时验证 |
| 注销账号 | `DELETE_ACCOUNT` | 注销账号时验证 |

## 发送渠道

### 邮件渠道（EMAIL）

**配置要求**

- 配置 Spring Mail
- 设置发件人邮箱
- 启用邮件渠道

**示例**

```json
{
  "channel": "EMAIL",
  "scene": "REGISTER",
  "target": "user@example.com"
}
```

### 短信渠道（SMS）

**配置要求**

- 集成短信服务商 API（阿里云、腾讯云等）
- 配置短信签名
- 启用短信渠道

**示例**

```json
{
  "channel": "SMS",
  "scene": "LOGIN",
  "target": "13800138000"
}
```

## 安全特性

### 限流策略

| 维度 | 默认限制 | 说明 |
|------|---------|------|
| 全局 | 1000次/分钟 | 防止系统资源耗尽 |
| IP | 10次/小时 | 防止单IP恶意攻击 |
| 目标地址 | 3次/分钟 | 防止骚扰用户 |
| 验证失败 | 5次锁定 | 防止暴力破解 |

### 验证码安全

- 6位纯数字
- 使用安全随机数生成
- 避免连续数字（如123456）
- 避免重复数字（如111111）
- 验证后立即失效
- 5分钟自动过期

### UUID 驱动机制

- 验证时无需传递敏感信息
- 防重放攻击
- 并发隔离
- 完整审计追踪

## 错误处理

### 标准错误码

| 错误码 | HTTP状态码 | 说明 | 处理建议 |
|--------|-----------|------|---------|
| `OTP_INVALID` | 400 | 验证码错误 | 提示用户重新输入 |
| `OTP_EXPIRED` | 400 | 验证码已过期 | 提示用户重新发送 |
| `OTP_NOT_FOUND` | 404 | 会话不存在 | 提示用户重新发送 |
| `OTP_ALREADY_USED` | 410 | 验证码已使用 | 提示用户重新发送 |
| `RATE_LIMIT_EXCEEDED` | 429 | 请求过于频繁 | 显示重试倒计时 |
| `OTP_LOCKED` | 423 | 验证失败次数过多 | 提示用户稍后重试 |
| `INVALID_TARGET` | 400 | 目标地址格式错误 | 提示用户检查格式 |
| `SEND_FAILED` | 500 | 发送失败 | 提示用户稍后重试 |

### 错误响应示例

```json
{
  "code": "RATE_LIMIT_EXCEEDED",
  "message": "请求过于频繁，请 60 秒后重试",
  "data": {
    "retry_after": 60
  }
}
```

## 模板自定义

### 模板路径

```
src/main/resources/templates/otp/
├── email/
│   ├── login_zh_CN.txt
│   ├── register_zh_CN.txt
│   └── ...
└── sms/
    ├── login_zh_CN.txt
    ├── register_zh_CN.txt
    └── ...
```

### 模板占位符

| 占位符 | 说明 | 示例 |
|--------|------|------|
| `{code}` | 验证码 | `123456` |
| `{expires_in}` | 有效期（分钟） | `5` |
| `{target}` | 目标地址（脱敏） | `u***@example.com` |
| `{timestamp}` | 发送时间 | `2024-01-01 12:00:00` |
| `{ip}` | 请求IP | `192.168.1.100` |

### 自定义模板示例

**邮件模板（email/custom_zh_CN.txt）**

```
【WYH Admin】自定义场景验证码

尊敬的用户：

您正在进行自定义操作，验证码为：

{code}

验证码有效期为 {expires_in} 分钟，请勿泄露给他人。

---
WYH Admin 团队
{timestamp}
```

## 多语言支持

### 添加新语言

1. 创建对应语言的模板文件（如 `login_en_US.txt`）
2. 发送请求时指定 `locale` 参数

**示例**

```json
{
  "channel": "EMAIL",
  "scene": "LOGIN",
  "target": "user@example.com",
  "locale": "en_US"
}
```

### 英文模板示例

**email/login_en_US.txt**

```
[WYH Admin] Login Verification Code

Dear User,

Your verification code is:

{code}

This code will expire in {expires_in} minutes. Please do not share it with others.

If this was not you, please ignore this email.

---
WYH Admin Team
{timestamp}
```

## 集成短信服务商

### 阿里云短信示例

1. 添加依赖

```xml
<dependency>
    <groupId>com.aliyun</groupId>
    <artifactId>dysmsapi20170525</artifactId>
    <version>2.0.24</version>
</dependency>
```

2. 修改 `SmsChannelService`

```java
@Override
public void send(String target, String subject, String content) {
    try {
        Config config = new Config()
            .setAccessKeyId("your-access-key-id")
            .setAccessKeySecret("your-access-key-secret");
        
        Client client = new Client(config);
        
        SendSmsRequest request = new SendSmsRequest()
            .setPhoneNumbers(target)
            .setSignName(otpProperties.getChannel().getSms().getSignName())
            .setTemplateCode("SMS_123456789")
            .setTemplateParam("{\"code\":\"" + extractCode(content) + "\"}");
        
        client.sendSms(request);
        log.info("短信发送成功: target={}", target);
    } catch (Exception e) {
        log.error("短信发送失败: target={}, error={}", target, e.getMessage(), e);
        throw OtpException.sendFailed("短信发送失败: " + e.getMessage());
    }
}
```

## 监控与运维

### 关键指标

- 发送成功率
- 验证成功率
- 平均验证时长
- 限流触发次数
- API响应时间

### 日志查询

**发送日志**

```bash
grep "OTP 发送成功" logs/application.log
```

**验证日志**

```bash
grep "OTP 验证" logs/application.log
```

### Redis 数据查看

```bash
# 查看会话数据
redis-cli GET "otp:session:550e8400-e29b-41d4-a716-446655440000"

# 查看限流计数
redis-cli GET "otp:rate:target:EMAIL:user@example.com"

# 查看失败次数
redis-cli GET "otp:fail:550e8400-e29b-41d4-a716-446655440000"
```

## 常见问题

### Q: 如何修改验证码有效期？

A: 在配置文件中修改 `otp.code.expires-in`（单位：秒）

### Q: 如何调整限流策略？

A: 在配置文件中修改 `otp.rate-limit.*` 相关配置

### Q: 如何添加新的业务场景？

A: 
1. 在 `OtpScene` 枚举中添加新场景
2. 为每个渠道添加对应的模板文件
3. 无需修改业务逻辑代码

### Q: 验证码为什么收不到？

A: 检查以下几点：
1. 邮件服务配置是否正确
2. 发件人邮箱是否有效
3. 目标邮箱是否正确
4. 是否触发了限流
5. 查看日志中的错误信息

### Q: 如何测试 OTP 功能？

A: 
1. 配置好邮件服务
2. 使用 Postman 或 curl 调用 API
3. 查看邮箱收到的验证码
4. 调用验证接口测试

## 最佳实践

### 1. 前端集成

```typescript
// 发送验证码
async function sendOtp(email: string, scene: string) {
  try {
    const response = await fetch('/api/v1/otp/send', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        channel: 'EMAIL',
        scene: scene,
        target: email,
        locale: 'zh_CN'
      })
    });
    
    const data = await response.json();
    
    // 保存 UUID 用于验证
    sessionStorage.setItem('otp_uuid', data.otp_uuid);
    
    // 显示倒计时
    startCountdown(data.expires_in);
    
    return data;
  } catch (error) {
    console.error('发送验证码失败:', error);
  }
}

// 验证验证码
async function verifyOtp(code: string) {
  const otpUuid = sessionStorage.getItem('otp_uuid');
  
  try {
    const response = await fetch('/api/v1/otp/verify', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        otp_uuid: otpUuid,
        code: code
      })
    });
    
    const data = await response.json();
    
    if (data.verified) {
      // 验证成功，继续后续流程
      console.log('验证成功');
    }
    
    return data;
  } catch (error) {
    console.error('验证失败:', error);
  }
}
```

### 2. 错误处理

```typescript
async function handleOtpError(error: any) {
  const errorCode = error.code;
  
  switch (errorCode) {
    case 'RATE_LIMIT_EXCEEDED':
      const retryAfter = error.data?.retry_after || 60;
      showMessage(`请求过于频繁，请 ${retryAfter} 秒后重试`);
      startCountdown(retryAfter);
      break;
      
    case 'OTP_INVALID':
      showMessage('验证码错误，请重新输入');
      break;
      
    case 'OTP_EXPIRED':
      showMessage('验证码已过期，请重新发送');
      break;
      
    case 'OTP_LOCKED':
      showMessage('验证失败次数过多，请稍后重试');
      break;
      
    default:
      showMessage('操作失败，请稍后重试');
  }
}
```

### 3. 倒计时组件

```typescript
function startCountdown(seconds: number) {
  let remaining = seconds;
  const button = document.getElementById('send-btn');
  
  const timer = setInterval(() => {
    if (remaining <= 0) {
      clearInterval(timer);
      button.disabled = false;
      button.textContent = '发送验证码';
    } else {
      button.disabled = true;
      button.textContent = `${remaining}秒后重试`;
      remaining--;
    }
  }, 1000);
}
```

---

**文档版本**: v1.0  
**最后更新**: 2024-01-01  
**维护者**: WYH Admin 团队
