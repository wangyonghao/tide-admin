# P1-A 半迁移收口（tenant / ops / license / biz-system）

> 对应 `specs/roadmap.md` P1 **A**：「无半迁移模块」。本页钉死四域边界；**不**要求把经典分层再拆成 COLA。

## 认定完成态

| 域 | 完成态 | 依据 |
|----|--------|------|
| **ops** | 单模块三子域；settings 已 COLA；notification / audit Web 在 `adapter/web`，其余保持经典 `service` / `mapper` | [`ops-layout.md`](ops-layout.md)（明确「没有继续做」） |
| **tenant** | 能力在 `platform/tenant`；经典 `controller` / `service` / `mapper` + 少量 `domain` 规则；主路径已可交付（W3） | [`backend-conventions.md`](backend-conventions.md) 允许遗留经典分层；`specs/tech-stack.md` 域职责 |
| **license** | `cmn-license-{core,generator,verifier}` 留在 reactor，带 AutoConfiguration；**主应用不依赖**（可选能力，未接入 `tide-bootstrap`） | 无业务消费者；不删模块、不强制接线 |
| **biz-system** | 薄壳：仪表盘 API/服务、跨域编排（如 `TenantDataApi` 实现）、系统 OpenAPI 分组；**不含**已迁出的 iam/ops/file 能力 | [`repo-map.md`](repo-map.md)；基础设施 Bean 不进 biz |

## 本收口已做（代码）

- 仪表盘 Web：`DashboardController` 从 `tide-bootstrap` 迁入 `biz-system`（业务不进 bootstrap）。
- 密码哈希：`PasswordEncoder` Bean 从 `biz-system` 迁入 `cmn-security` 自动配置。

## 刻意未做

- 更名 B（`@tide`→`@wyh`）/ C（`tide-bootstrap` 等）
- 数据源级租户隔离等 W3 Out
- notification / audit 再拆 COLA（见 ops-layout）
- tenant 全量 COLA 化
- 将 `cmn-license-*` 接入主应用或移出 reactor
- 将 `TenantDataApiForSystemImpl` 迁入 tenant app（跨域编排，待决）

## 待决

1. **`TenantDataApiForSystemImpl` 归属**：仍在 `biz-system`，通过 SPI 初始化租户侧用户/角色/部门；迁到 `tenant` app 需理清对 iam client 的依赖，不在本次拆。
2. **license 产品化**：若要做许可证校验主路径，再单独开需求接入 verifier；当前保持「可选、未接线」。
3. **ops 子域拆模块**：仍为一个 `platform/ops` Maven 模块；是否再拆独立 artifact 未定，文档与代码均按「单模块多包」使用。
