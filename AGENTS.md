# Tide Admin — Agent Guide

面向 AI Agent 的**短索引**。细则按需阅读链接；勿把本文件当百科全书扩写。

## Constitution & skills

| 文档 / 技能 | 用途 |
|-------------|------|
| [`specs/mission.md`](specs/mission.md) | 产品使命、用户、成功标准、非目标 |
| [`specs/tech-stack.md`](specs/tech-stack.md) | 技术选型、版本、架构硬规则 |
| [`specs/roadmap.md`](specs/roadmap.md) | 阶段状态与完成标准 |
| [`skills/feature-spec`](skills/feature-spec/SKILL.md) | 开阶段：访谈 + 写规格三件套 |
| [`skills/sdd-continue`](skills/sdd-continue/SKILL.md) | 续作：读宪法 → 推进下一阶段 |
| [`skills/changelog`](skills/changelog/SKILL.md) | 更新根目录 `CHANGELOG.md` |

Cursor 同步加载 [`.cursor/skills/`](.cursor/skills/) 同名技能；编码约束见 [`.cursor/rules/`](.cursor/rules/)。

地图与详细约定（按需打开，非每次必读）：

- [`docs/agent/repo-map.md`](docs/agent/repo-map.md) — 模块地图与快速定位
- [`docs/agent/backend-conventions.md`](docs/agent/backend-conventions.md)
- [`docs/agent/frontend-conventions.md`](docs/agent/frontend-conventions.md)
- [`frontend/DESIGN.md`](frontend/DESIGN.md) — 前端视觉宪法（UI 风格）
- [`docs/agent/ui-patterns.md`](docs/agent/ui-patterns.md) — 页面范式与样板清单

## Commands

```bash
# 后端
cd backend && mvn -pl tide-bootstrap -am spring-boot:run
# 单元测试（默认开启；临时跳过 -DskipUnitTests=true）
cd backend && mvn -pl platform/job,platform/iam -am test

# 前端产品后台（@tide/admin）。pnpm dev 与 pnpm dev:admin 相同
cd frontend && pnpm install && pnpm dev
# 文档站 / 组件演示
cd frontend && pnpm dev:docs
cd frontend && pnpm dev:ds
```

配置：`backend/tide-bootstrap/src/main/resources/config/`；环境变量示例：`backend/scripts/env.example.sh`  
DB 变更：`backend/tide-bootstrap/src/main/resources/db/changelog/postgresql/`

## Hard boundaries

1. **不做微服务拆分**；保持模块化单体（见 mission / tech-stack）。
2. **依赖方向**：`tide-bootstrap` → `interfaces` / `biz-*` → `platform` 域 → `cmn-*`。禁止下层依赖上层。
3. **新依赖**：后端进根 `pom.xml` 的 `dependencyManagement`；前端进 `pnpm-workspace.yaml` catalog。
4. **DB**：PostgreSQL + Liquibase；禁止只改库不改 changelog。
5. **调度**：Spring Quartz / `platform-job`；勿并行引入另一套调度内核。
6. **领域错误**：`XxxException` 静态工厂；禁止业务里直接 `new BizException` / `BadRequestException`；禁止新增 `Check` / `ValidationUtils`。
7. **前端**：产品后台在 `frontend/apps/admin`（`@tide/admin`）+ shadcn-vue / Vben 原子；壳在 `apps/admin/src/vben`；慎改 `vendor/@core`。
8. **宪法**：改 mission / tech-stack / roadmap 须用户显式确认，禁止作实现副作用修改。

## Where to change what

| 意图 | 位置 |
|------|------|
| 启动 / 打包 | `backend/tide-bootstrap` |
| 身份 / 组织 / 授权 | `backend/platform/iam`（`identity` / `department` / `security`） |
| 租户 | `backend/platform/tenant` |
| 文件 | `backend/platform/file` |
| 任务 | `backend/platform/job` |
| 审计 / 通知 / 设置 | `backend/platform/ops` |
| 开放 API | `backend/interfaces/open-api` |
| 系统壳 / 仪表盘等 | `backend/biz/biz-system` |
| 前端页面 / API | `frontend/apps/admin/src/views`、`frontend/apps/admin/src/api` |
| 文档站 | `frontend/apps/docs`（`@tide/docs`） |
| 组件演示 | `frontend/apps/design-system`（只演示 `@tide/ui` / `@tide/ui-patterns`） |
| UI 原子 / 页面模式 | `frontend/packages/ui`（`@tide/ui`）、`frontend/packages/ui-patterns`（`@tide/ui-patterns`）。应用仍可用 `#/ui`、`#/ui-patterns` |

完整表见 [`docs/agent/repo-map.md`](docs/agent/repo-map.md)。

## Delivery

细则见 [`.cursor/rules/delivery.mdc`](.cursor/rules/delivery.mdc)。摘要：

- 交付态干净：无「已修正此处」类痕迹；注释只解释非显而易见的 why。
- 向用户说明变更时，与正文分离，用独立 Changelog 要点（仓库变更日志走 `changelog` skill）。
- 默认中文回复；未要求不 commit / 不 push。
- 需求不清先问；重要结论尽量用仓库内多源交叉验证。
- **后端单测**：`domain`/规则类高覆盖；`adapter` 不强制。细则见 [`docs/agent/backend-conventions.md`](docs/agent/backend-conventions.md#testing)。
