# Tide Admin

前后端分离的多租户中后台。后端是 Spring Boot 3 模块化单体，前端是 Vue 3 + shadcn-vue / Vben 原子。技术选型以 [`specs/tech-stack.md`](specs/tech-stack.md) 和依赖声明为准。

仓库：<https://github.com/wangyonghao/tide-admin>

## 环境要求

| 环境 | 要求 |
|------|------|
| Java | 17+ |
| Maven | 3.6+ |
| PostgreSQL | 17+ |
| Redis | 6+ |
| Node.js | ^20.19.0 \|\| ^22.18.0 \|\| ^24.0.0 |
| pnpm | >= 10（当前 `packageManager`：10.33.4） |

## 核心技术栈

版本摘自 [`specs/tech-stack.md`](specs/tech-stack.md)。后端锁定在 `backend/pom.xml`，前端锁定在 `frontend/pnpm-workspace.yaml` 的 catalog。

| 类别 | 选型 | 版本 |
|------|------|------|
| 语言 / 框架 | Java、Spring Boot 3 | 17、3.4.13 |
| Web 容器 | Undertow | 随 Spring Boot |
| 前端 | Vue、TypeScript、Vite | 3.5.32、6.0.2、8.0.8 |
| UI | shadcn-vue / Vben 原子（`@tide/ui`、`@tide/ui-patterns`） | 5.7.0 |
| 路由 / 状态 | Vue Router、Pinia | 5.0.4、3.0.4 |
| 样式 | Tailwind CSS | 4.2.2 |
| 认证 | Sa-Token、JustAuth | 1.44.0、1.16.7 |
| ORM | MyBatis Plus | 3.5.16 |
| 主库 / 迁移 | PostgreSQL、Liquibase | changelog 在 `backend/tide-bootstrap/src/main/resources/db/changelog/postgresql/` |
| 缓存 | Redisson、JetCache | 3.52.0、2.7.8 |
| 调度 | Spring Quartz（`platform-job`） | 不是另一套分布式调度内核 |
| 接口文档 | NextDoc4j、Swagger Annotations | 1.1.7、2.2.36 |
| 其他 | FastExcel、CosId、X-File-Storage | 1.3.0、2.13.3、2.2.1 |

主应用是 `frontend/apps/admin`（`@tide/admin`）。`pnpm dev` 与 `pnpm dev:admin` 相同。

## 快速开始

本地基础设施在 [`backend/docker`](backend/docker/README.md)（Postgres、Redis、Inbucket、MinIO，以及可选的前端容器）。不要使用已废弃的 `backend/scripts/docker/`。

### 1. 克隆

```bash
git clone https://github.com/wangyonghao/tide-admin.git
cd tide-admin
```

### 2. 启动依赖

```bash
cd backend/docker
cp .env.example .env
# 编辑 .env，替换 POSTGRES_PASSWORD、REDIS_PWD、MINIO_ROOT_PASSWORD
# openssl rand -hex 24

docker compose --env-file .env -f docker-compose.dev.yml up
```

端口与连法见 [`backend/docker/README.md`](backend/docker/README.md)。数据库名是 `postgres`，用户是 `postgres`。

### 3. 配置后端环境变量

开发配置没有给数据库、Redis 和 AES 密钥默认值，不导出变量后端起不来。

```bash
cp backend/scripts/env.example.sh backend/scripts/env.sh
# DB_PWD、REDIS_PWD 与 backend/docker/.env 一致，并更换 AES_KEY
set -a && source backend/scripts/env.sh && set +a
```

`backend/scripts/env.sh` 已被忽略，不要提交。

默认 profile 是 `dev`（`application.yml`）。`application-dev.yml` 里 **Liquibase 默认关闭**，空库不会自动建表。第一次对着空库启动时：

```bash
export SPRING_LIQUIBASE_ENABLED=true
```

生产配置 `application-prod.yml` 默认开启 Liquibase，并额外要求 `WYH_JWT_SECRET`、`WYH_RSA_PUBLIC_KEY`、`WYH_RSA_PRIVATE_KEY`。开发 profile 的 JWT / RSA 写在 `application-dev.yml`，不必设这三项。

开发环境文件落在 `application-dev.yml` 的 `file.local.root-path`，按本机目录修改。

### 4. 启动后端

IDE 运行 `top.wyhao.admin.AdminApplication`，或：

```bash
cd backend
mvn -pl tide-bootstrap -am spring-boot:run
```

开发端口 **8000**。启动日志会打印服务地址和接口文档（`/doc.html`）。

### 5. 启动前端

另开终端。前端开发端口与接口地址在 `frontend/apps/admin/.env.development`：页面 `http://localhost:5888`，接口 `http://127.0.0.1:8000`。

```bash
cd frontend
pnpm install
pnpm dev
```

文档站：`pnpm dev:docs`。组件演示：`pnpm dev:ds`。

若第 2 步已经用 Compose 拉起了前端容器，浏览器访问 `http://localhost:5888` 即可，不必再在宿主机执行 `pnpm dev`。宿主机和容器不要同时对同一份 `frontend/` 执行 `pnpm install`。

### 常用命令

```bash
# 后端关键模块单测（CI 同此范围）
cd backend && mvn -pl platform/job,platform/iam,platform/cmn-crypto -am test

# 前端单测（Vitest，不含 Playwright）
cd frontend && pnpm test:unit
```

## 项目结构

```
tide-admin/
├── backend/
│   ├── tide-bootstrap/     # 启动、配置、Liquibase
│   ├── interfaces/         # tide-web、open-api
│   ├── biz/biz-system/     # 系统壳、仪表盘等
│   ├── platform/           # iam、tenant、file、job、ops 与 cmn-*
│   ├── docker/             # 本地 Compose
│   └── scripts/env.example.sh
├── frontend/
│   ├── apps/admin/         # @tide/admin 产品后台
│   ├── apps/docs/          # @tide/docs
│   ├── apps/design-system/ # 只演示 @tide/ui、@tide/ui-patterns
│   └── packages/           # ui、ui-patterns
├── specs/                  # 技术栈与路线图
└── AGENTS.md               # 给 Agent 的短索引
```

依赖方向：`tide-bootstrap` → `interfaces` / `biz` → `platform` 域 → `cmn-*`。

产品页在 `frontend/apps/admin/src/views`：工作台、个人中心与消息、用户 / 角色 / 菜单 / 部门、通知公告、字典、系统配置、文件、在线用户、登录与操作日志、短信日志、开放应用、代码生成、定时任务。租户与套餐的 API 在 `frontend/apps/admin/src/api/tenant`，后端在 `platform/tenant`。

## 参与贡献

1. Fork 后基于 `main` 开分支。
2. 提交说明使用 [Conventional Commits](https://www.conventionalcommits.org/)。
3. 向 `main` 提 Pull Request。改 `backend/**` 会跑后端编译和关键模块单测；改 `frontend/**` 会跑 `pnpm test:unit` 和前端构建。

更细的模块边界见 [`AGENTS.md`](AGENTS.md)。

## License

Apache-2.0。见 [`backend/LICENSE`](backend/LICENSE)。
