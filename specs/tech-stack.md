# Tide Admin — Tech Stack

版本以仓库依赖声明为准：后端 `backend/pom.xml`，前端 `frontend/pnpm-workspace.yaml` catalog。本文约束架构选择与 Agent 不可擅自违背的规则。

## Runtime requirements

| 环境 | 要求 |
|------|------|
| Java | 17+ |
| Maven | 3.6+ |
| PostgreSQL | 17+（主库） |
| Redis | 6.0+ |
| Node.js | ^20.19.0 \|\| ^22.18.0 \|\| ^24.0.0 |
| pnpm | >= 10.0.0（当前 packageManager：10.33.4） |

## Backend stack

| 类别 | 选型 | 版本 / 备注 |
|------|------|-------------|
| 语言 | Java | 17 |
| 应用框架 | Spring Boot | 3.4.13 |
| 云组件 BOM | Spring Cloud | 2024.0.2（OpenFeign 等能力；**非**微服务拆分目标） |
| Web 容器 | Undertow | 随 Spring Boot |
| ORM | MyBatis Plus | 3.5.16 |
| SQL 分析 | P6Spy | 3.9.1（开发期） |
| DB 迁移 | Liquibase | changelog 在 `tide-bootstrap/.../db/changelog/postgresql/` |
| 主库 | PostgreSQL | JDBC 默认；生产配置以 Postgres 为准 |
| 缓存 | Redisson + JetCache | 3.52.0 / 2.7.8（二级缓存） |
| 认证授权 | Sa-Token | 1.44.0 |
| 第三方登录 | JustAuth | 1.16.7 |
| 任务调度 | Spring Quartz（`platform-job`） | 非 SnailJob |
| 验证码 | AJ-Captcha / Easy Captcha | 1.4.0 / 1.6.2 |
| Excel / Office | FastExcel / Apache POI | 1.3.0 / 5.4.1 |
| 文件存储 | X-File-Storage + Thumbnailator | 2.2.1 / 0.4.21（本地 / S3 兼容） |
| API 文档 | NextDoc4j + Swagger Annotations | 1.1.7 / 2.2.36 |
| ID | CosId | 2.13.3 |
| 链路追踪 | TLog | 1.5.2 |
| 工具 | Hutool、Lombok、MapStruct、OkHttp、Ip2region 等 | 见根 POM |

构建：Maven 多模块 + `flatten-maven-plugin` 统一 `${revision}`（当前 `4.1.0-SNAPSHOT`）。

## Frontend stack

| 类别 | 选型 | 版本 / 备注 |
|------|------|-------------|
| 框架 | Vue | 3.5.32 |
| 语言 | TypeScript | 6.0.2 |
| 构建 | Vite | 8.0.8 |
| Monorepo | pnpm workspace（`vendor/@core/*`、`packages/*`、`apps/*`） | catalog 在 `frontend/pnpm-workspace.yaml`；无独立工程配置包 |
| UI（主应用） | shadcn-vue / Vben 原子 | 应用包 `@tide/ui`、`@tide/ui-patterns` 5.7.0（`frontend/packages/*`）。导入别名 `#/ui`、`#/ui-patterns`，也可写包名。底子 `@vben-core/shadcn-ui` 5.7.0。基座 reka-ui 2.9.5、CVA 0.7.1、vue-sonner 2.0.9。`naive-ui` 已移除 |
| 路由 / 状态 | Vue Router / Pinia | 5.0.4 / 3.0.4（含 persistedstate） |
| 请求 / 工具 | Axios、VueUse、Day.js | 1.15.0 / 14.2.1 / 1.11.20 |
| 校验 | Zod | 3.25.76 |
| 表格 / 图表 | VxeTable + VxePC UI / ECharts | 4.18.11 / 4.13.21 / 6.0.0 |
| 富文本 | TipTap | 3.22.3 |
| 样式 | Tailwind CSS 4 | 4.2.2 |
| i18n | Vue I18n | 11.3.2 |
| 质量工具 | ESLint、Stylelint、oxfmt、oxlint、Commitlint、Lefthook | 见 catalog |
| 测试 | Vitest、Playwright、@vue/test-utils | 4.1.4 / 1.59.1 等 |

主应用：`frontend/apps/admin`（`@tide/admin`）。共享 UI 在 `packages/ui`（`@tide/ui`）、`packages/ui-patterns`（`@tide/ui-patterns`）。可部署面在 `apps/`：`apps/admin`（产品后台）、`apps/docs`（`@tide/docs`，VitePress）、`apps/design-system`（`@tide/design-system`，只演示上述两个包）。壳在 `apps/admin/src/vben/`，UI 内核在 `vendor/@core`。应用构建配置在 `apps/admin`（Vite、env、tsconfig）；仓库级 ESLint / Stylelint 仍在 `frontend/` 根目录（无 `configs/` 目录）。脚本：在 `frontend/` 下 `pnpm dev` / `pnpm build` 与 `pnpm dev:admin` / `pnpm build:admin` 相同，都启动产品后台；另有 `pnpm dev:docs`、`pnpm dev:ds`。

## Architecture

### Backend module layout

```
tide-bootstrap          # 可运行主服务（打包 / spring-boot:run）
  ↓
interfaces/*            # tide-web、open-api 等接口层
biz/*                   # 业务插件（如 biz-system）
  ↓
platform/*              # 平台能力
  ├── cmn-*             # 基础设施（core、boot、security、storage…）
  ├── iam / tenant / file / job / ops / coder
  └── spi
```

依赖方向：**bootstrap → interfaces / biz → platform 域 → cmn-***。禁止下层依赖上层；跨域通过 client / SPI 解耦，避免循环依赖。

平台域职责（摘要）：

- **iam**：身份、组织、授权（用户 / 部门 / 角色 / 菜单等）
- **tenant**：租户与套餐
- **file**：文件领域与存储适配
- **job**：Quartz 任务管理
- **ops**：运维 / 审计类能力
- **coder**：代码生成

### Frontend package layout

```
frontend/apps/admin             # @tide/admin；产品后台（入口 src/；views / api / …）
  └── src/vben/                 # 内联壳（@vben/*），直接依赖 vendor
frontend/apps/docs              # @tide/docs 5.7.0；VitePress
frontend/apps/design-system     # @tide/design-system；只演示 @tide/ui 与 @tide/ui-patterns
frontend/packages/ui-patterns   # @tide/ui-patterns 5.7.0；依赖 @tide/ui；应用别名 #/ui-patterns
frontend/packages/ui            # @tide/ui 5.7.0；依赖 @vben-core/shadcn-ui；应用别名 #/ui
vendor/@core                    # UI 内核（慎改）
```

### Delivery shape

- 前后端分离；后端模块化单体，**不追求微服务拆分**。
- 后端 BOM 统一版本；前端依赖在 `pnpm-workspace.yaml` catalog 声明。
- 默认 Web 容器 Undertow；主库 PostgreSQL + Redis；缓存 JetCache（本地 + Redis）模式。

## Architecture rules (do not violate)

1. **不做微服务拆分**：不引入「一域一可独立部署服务」作为默认方向；Spring Cloud 仅用于已选集成能力。
2. **遵守模块依赖层次**：新代码放入对应 `platform` 域或 `biz-*`；公共能力进 `cmn-*`，不得把业务逻辑塞进 bootstrap。
3. **新增依赖先登记版本**：后端写入根 `pom.xml` 的 `properties` / `dependencyManagement`；前端写入 catalog，禁止应用内随意钉死游离版本。
4. **数据库变更走 Liquibase**：脚本放在 `db/changelog/postgresql/`，禁止只改库不改 changelog。
5. **任务调度以 Quartz（platform-job）为准**：勿回退或并行引入另一套分布式调度框架，除非宪法与 roadmap 明确变更。
6. **前端主 UI 为 shadcn-vue / Vben 原子**：源码在 `@tide/ui`、`@tide/ui-patterns`（`frontend/packages/*`）。业务页用别名 `#/ui`、`#/ui-patterns`，或直接写包名。组件演示是 `frontend/apps/design-system`，只消费这两个包，不放业务页。勿默认再开平行产品应用，也不要再引入 `naive-ui`。
7. **慎改 `@core`**：共享核心包变更影响面大；应用特有逻辑放 `frontend/apps/admin/src`，共享原子放 `frontend/packages/ui`，页面模式放 `frontend/packages/ui-patterns`，壳层封装在 `frontend/apps/admin/src/vben`。
8. **配置集中**：运行配置以 `tide-bootstrap/src/main/resources/config/` 为准；敏感项用环境变量覆盖。

## Deliberate exclusions

| 排除项 | 原因 |
|--------|------|
| 微服务多服务拆分 | 与 mission 非目标一致；优先模块化单体与清晰边界 |
| 以 MySQL 为主库默认 | 当前 changelog 与默认 JDBC 面向 PostgreSQL |
| SnailJob 作为调度内核 | 代码已收敛到 Spring Quartz + `platform-job` |
| 在业务域直接堆基础设施细节 | 基础设施归 `cmn-*`，域内保持应用 / 领域边界 |
| Naive UI 作为主 UI | 已从依赖移除；产品界面走 shadcn-vue / Vben 原子 |

## Key decisions (why)

- **MyBatis Plus**：CRUD 增强、Lambda 查询、分页等，减少样板。
- **Sa-Token**：轻量、鉴权能力完整、与 Boot 集成简单。
- **shadcn-vue / Vben 原子**：底子在 `@vben-core/shadcn-ui`（reka-ui + CVA）。应用包是 `@tide/ui`、`@tide/ui-patterns`；页面按 `#/ui`、`#/ui-patterns` 或包名导入。`naive-ui` 已从依赖移除。
- **pnpm + Turbo**：磁盘与安装效率、增量构建，适合前端 Monorepo。
- **Vite**：开发启动与 HMR 快，构建产物可控。
- **PostgreSQL + Liquibase**：版本化 schema，与现网默认路径一致。
- **Quartz**：与当前 `platform-job` 管理 API、集群/持久化配置一体，避免双调度栈。

## Agent notes

- 改栈或换默认库 / 调度 / UI 属于宪法变更：先改 `specs/tech-stack.md`（及必要时 `mission.md` / `roadmap.md`），再改代码。
- 目录导航与编码约定：短索引见 `AGENTS.md`；地图与细则见 `docs/agent/`；Cursor glob 规则见 `.cursor/rules/`。**技术选型与版本以本文 + 依赖文件为准**。
