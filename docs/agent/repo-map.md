# Tide Admin — Repo map

Agent 按需阅读。完整目录树不必背诵；以本表与 `ls` / 搜索为准。

## Layout

```
wyh-admin/
├── backend/           # Java 17 + Spring Boot（模块化单体）
│   ├── tide-bootstrap/# 可运行主服务
│   ├── interfaces/    # tide-web、open-api
│   ├── biz/           # 业务插件（如 biz-system）
│   └── platform/      # cmn-* 基础设施 + 平台域
├── frontend/          # Vite 应用 + packages/ui + vendor 壳
├── specs/             # SDD 宪法
├── skills/            # Agent 工作流技能
├── docs/agent/        # Agent 补充文档（本目录）
├── frontend/DESIGN.md # 前端视觉宪法（中后台 UI）
└── AGENTS.md          # 短索引
```

## Backend modules

```
tide-bootstrap
  → interfaces/* , biz/*
    → platform/{iam,tenant,file,job,ops,spi}
      → platform/cmn-*
```

| 模块 | 职责 |
|------|------|
| `tide-bootstrap` | 启动、配置、Liquibase、装配 |
| `interfaces/open-api` | 开放 API / 签名 |
| `biz/biz-system` | 系统壳、仪表盘等残留/编排 |
| `platform/iam` | identity、organization、security |
| `platform/tenant` | 租户、套餐 |
| `platform/file` | 文件领域与存储适配 |
| `platform/job` | Quartz 任务管理 |
| `platform/ops` | audit、notification、settings |
| `platform/cmn-core` | 共享内核：枚举、异常、常量、工具 |
| `platform/cmn-boot` | 运行时聚合：Web / DB / Cache / API 文档 |
| `platform/cmn-*` | 其余：security、messaging、crypto、storage 等 |

平台域代码常见 COLA 分包：`adapter` / `app` / `domain` / `infrastructure`。

## Quick locate

### Backend

| 用途 | 路径 |
|------|------|
| 启动类 | `backend/tide-bootstrap/src/main/java/top/wyhao/admin/AdminApplication.java` |
| 开发配置 | `backend/tide-bootstrap/src/main/resources/config/application-dev.yml` |
| Liquibase | `backend/tide-bootstrap/src/main/resources/db/changelog/postgresql/` |
| 用户 / 认证 | `backend/platform/iam/.../identity/` |
| 角色 / 菜单 | `backend/platform/iam/.../security/` |
| 部门 | `backend/platform/iam/.../organization/` |
| 租户 | `backend/platform/tenant/` |
| 文件 | `backend/platform/file/` |
| 任务 | `backend/platform/job/` |
| 通知 / 公告 | `backend/platform/ops/.../notification/` |
| 字典 / 配置 | `backend/platform/ops/.../settings/` |
| 操作日志 | `backend/platform/ops/.../audit/` |

本地启动：`cd backend && mvn -pl tide-bootstrap -am spring-boot:run`

### Frontend

| 用途 | 路径 |
|------|------|
| 入口 | `frontend/src/main.ts` |
| 路由 | `frontend/src/router/` |
| 登录 | `frontend/src/views/_core/authentication/` |
| 系统页 | `frontend/src/views/system/` |
| API | `frontend/src/api/` |
| 壳 / 内核 | `frontend/src/vben/`（`@vben/*`）；`frontend/vendor/@core`（慎改） |
| 原子 / 页面模式 | `frontend/packages/ui`（`@tide/ui`）、`frontend/packages/ui-patterns`（`@tide/ui-patterns`）。应用导入仍可用 `#/ui`、`#/ui-patterns` |
| 工程配置 | 根目录 `vite.config.ts`、`eslint.config.mjs`、`tsconfig.json` 等 |

开发：`cd frontend && pnpm dev`
