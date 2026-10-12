# 租户主路径演示

创建套餐并绑定菜单 → 创建两个租户 → 分别登录 → 用户和部门互不可见。

管理页视图还没有落地。创建走 API；登录和核对数据可以走页面，也可以继续用 API。

## 环境

与 `backend/scripts/docker/docker-compose.yml` 对齐。Postgres 用户是 `postgres`，应用库名用 **`DB_NAME=postgres`**（镜像总会有这个库）。Compose 另建了 `tide_admin`，不要指到那个库，除非你改了 `DB_NAME`。

`backend/scripts/docker/.env.example` 和 `application-dev.yml` 注释里的 `wyhao_admin` 与当前 Compose 不一致，演示不要用它。

```bash
export AES_KEY='0123456789abcdef'   # 16/24/32 字节，application.yml 必填

export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=postgres
export DB_USER=postgres
export DB_PWD='<POSTGRES_PASSWORD>'

export REDIS_HOST=127.0.0.1
# 口令与 backend/docker/.env 里的 REDIS_PWD 相同，不要写进仓库。
# 若 Redis 直接听 6379，把端口改成 6379。
export REDIS_PORT=1379
export REDIS_PWD='<REDIS_PWD>'
export REDIS_DB=15
```

空库启动前把 `backend/tide-bootstrap/src/main/resources/config/application-dev.yml` 的 `spring.liquibase.enabled` 设为 `true`。当前 `main` 仍是 `false`；打开后才会执行 `db/changelog/postgresql/`（含 `tenant` 表和各业务表的 `tenant_id`）。这与未合入的 PR #21 相同，本清单不改那份配置。

```bash
cd backend/scripts/docker && docker compose up -d db redis
cd backend && mvn -pl tide-bootstrap -am spring-boot:run
```

API 根地址：`http://127.0.0.1:8000`。前端开发代理把 `/api` 转到这里并去掉前缀。

## 页面与接口

| 步骤 | 路由 / 方法 | 说明 |
|------|-------------|------|
| 登录 | 页面 `/auth/login`，`POST /auth/login` | 登录页有「租户 ID」输入框，留空表示平台租户 `0`。只在登录请求上带 `X-Tenant-Id` |
| 套餐 | `POST /tenant/package` | 菜单种子路径 `/tenant/package`，视图 `frontend/apps/admin/src/views/tenant/package/index.vue` **不存在**，打开是 404 |
| 租户 | `POST /tenant/management` | 菜单种子路径 `/tenant/management`，视图 `frontend/apps/admin/src/views/tenant/management/index.vue` **不存在** |
| 用户 | 页面 `/system/user`，`GET /system/user` | 用来核对用户是否串租户 |
| 部门 | `GET /system/department/tree` | 用来核对部门是否串租户 |

平台管理员种子账号：`admin` / `admin123`（租户 `0`）。创建租户、套餐需要已登录，请求头 `Authorization: Bearer <token>`。

登录请求体还要 `clientId`（前端 `.env` 的 `VITE_CLIENT_ID`）：`ef51c9a3e9046c4f2ea45142c8a8344a`，`grantType`：`ACCOUNT`。

## 1. 平台管理员登录

登录页租户 ID 留空，或：

```bash
# 密码 admin123 的 RSA 密文。公钥是 application-dev.yml 里的开发公钥，与前端 encryptByRsa 相同。
PUB='MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAM51dgYtMyF+tTQt80sfFOpSV27a7t9uaUVeFrdGiVxscuizE7H8SMntYqfn9lp8a5GH5P1/GGehVjUD2gF/4kcCAwEAAQ=='
mkdir -p /tmp/tide-demo
printf '%s\n' '-----BEGIN PUBLIC KEY-----' "$PUB" '-----END PUBLIC KEY-----' > /tmp/tide-demo/pub.pem
ENC=$(printf '%s' 'admin123' | openssl pkeyutl -encrypt -pubin -inkey /tmp/tide-demo/pub.pem -pkeyopt rsa_padding_mode:pkcs1 | base64 -w0)

curl -s http://127.0.0.1:8000/auth/login \
  -H 'Content-Type: application/json' \
  -d "{\"grantType\":\"ACCOUNT\",\"clientId\":\"ef51c9a3e9046c4f2ea45142c8a8344a\",\"username\":\"admin\",\"password\":\"$ENC\"}"
```

响应 `code` 为 `200` 时，`token` 即后续令牌，`tenantId` 应为 `0`。不要带 `X-Tenant-Id`。若返回需要验证码，说明该账号已连续失败，清掉 Redis 里 `login:retry:` 后再试。

## 2. 创建套餐并绑定菜单

`menuIds` 使用种子菜单：系统管理目录 `1000`、用户管理 `1010` 及其列表权限 `1011`、部门管理 `1070` 及其列表权限 `1071`。租户管理员拿到的菜单就是这一份。

```bash
TOKEN='上一步的 token'
curl -s http://127.0.0.1:8000/tenant/package \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"name":"演示套餐","menuIds":[1000,1010,1011,1070,1071]}'
```

记下返回的 `id`，下面称 `PACKAGE_ID`。

## 3. 创建两个租户

管理员密码同样要 RSA 加密（上面的 `$ENC` 可复用，明文 `admin123`）。用户名 4–64 位、字母开头。域名可省略。

```bash
curl -s http://127.0.0.1:8000/tenant/management \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d "{\"name\":\"租户甲\",\"packageId\":$PACKAGE_ID,\"adminUsername\":\"tnta\",\"adminPassword\":\"$ENC\"}"

curl -s http://127.0.0.1:8000/tenant/management \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d "{\"name\":\"租户乙\",\"packageId\":$PACKAGE_ID,\"adminUsername\":\"tntb\",\"adminPassword\":\"$ENC\"}"
```

记下两个 `id`（`TENANT_A`、`TENANT_B`）。创建时会在该租户下写入部门、角色 `admin`（租户管理员）和上述管理员用户，并把套餐菜单赋给这个角色。

## 4. 分租户登录

登录页在「租户 ID」填 `TENANT_A`，用户名 `tnta`，密码 `admin123`。或：

```bash
curl -s http://127.0.0.1:8000/auth/login \
  -H "X-Tenant-Id: $TENANT_A" \
  -H 'Content-Type: application/json' \
  -d "{\"grantType\":\"ACCOUNT\",\"clientId\":\"ef51c9a3e9046c4f2ea45142c8a8344a\",\"username\":\"tnta\",\"password\":\"$ENC\"}"
```

对 `TENANT_B` / `tntb` 再登一次。响应里的 `tenantId` 应等于请求头。

已登录之后，租户以会话为准。再带另一个 `X-Tenant-Id` 不会切到别的租户；要换租户就重新登录。未登录且不带头时落到平台租户 `0`，因此 `admin` 仍按原来的方式登录。

## 5. 核对隔离

用租户甲的 token（不要再改租户头，会话里已经是甲）：

```bash
curl -s 'http://127.0.0.1:8000/system/user?page=1&pageSize=20' \
  -H "Authorization: Bearer $TOKEN_A"

curl -s http://127.0.0.1:8000/system/department/tree \
  -H "Authorization: Bearer $TOKEN_A"
```

期望：

- 租户甲的用户列表里有 `tnta`，没有 `tntb`，也没有平台 `admin`
- 租户乙相反，只有 `tntb`
- 平台 `admin`（租户 `0`）的用户列表里有 `admin`，没有 `tnta` / `tntb`
- 部门树同样只出现本租户的根部门（甲是「租户甲」，乙是「租户乙」，平台是种子「总公司」）

页面上：租户甲登录后打开 `/system/user`，列表里不应出现租户乙的管理员。

## 还没打通

- **数据源级隔离**（`TenantIsolationLevel.DATASOURCE`）只有枚举，没有第二数据源。
- 租户 / 套餐的分页、详情、修改、删除仍是空实现。改套餐菜单不会回写已有租户（`listIdByPackageId` 已能查出租户，但 `PackageServiceImpl.update` 还没调用它）。
- 下列表有 `tenant_id` 列，实体还没实现 `TenantedEntity`，列表仍可能串数据：成员关系、角色菜单、角色部门、社会化绑定、操作日志、消息、通知、文件、应用。登录日志实体已标记租户列，但写入是 `@Async`，异步线程可能拿不到上下文，失败只记错误日志。
- 套餐删除未检查是否仍有租户引用。
- 手机、邮箱登录会按当前租户查用户，演示路径只用账号登录。
