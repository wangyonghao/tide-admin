# 本地邮件（Inbucket + Apple Container）

开发用邮件黑洞：SMTP 收信不外发，浏览器查看验证码等测试邮件。

| 端口 | 用途 |
|------|------|
| 2500 | SMTP |
| 9000 | Web UI → http://localhost:9000 |
| 1100 | POP3（可选） |

邮件数据放在容器卷 `tide-mail-storage`（挂到容器内 `/storage`），不挂载本仓库目录。

## 启动

```bash
container system start

container volume create tide-mail-storage

container run -d --name tide-mail \
  -p 2500:2500 -p 9000:9000 -p 1100:1100 \
  -e TZ=Asia/Shanghai \
  --mount type=volume,source=tide-mail-storage,target=/storage \
  docker.io/inbucket/inbucket:3.0.3
```

已存在容器时：`container start tide-mail`。

## 与应用配置

运行时以 `sys_config.mail` 为准（字段：`host` / `port` / `username` / `password` / `from` / `sslEnabled`）。本地 Inbucket 填 host `127.0.0.1`、port `2500`、关闭认证。
