# Backend conventions

配合 `.cursor/rules/backend-java.mdc`、`biz-exception.mdc`。架构硬规则以 `specs/tech-stack.md` 为准。

## Module & packaging

- 新业务进对应 `platform` 域或 `biz-*`；基础设施进 `cmn-*`。
- **platform 域**优先 COLA：`adapter`（web/client）/ `app`（service、assembler）/ `domain` / `infrastructure`。
- 遗留或简单模块可用经典分层：`controller` / `service` / `mapper` / `entity` / dto。

## Naming

- 类：PascalCase；方法/字段：camelCase；常量：`UPPER_SNAKE`；包名全小写。
- API 模型：`XxxQuery`、`XxxRequest`、响应 `XxxResult` / `XxxVO`（与现有模块一致即可）。
- Service 常用动词：`detail` / `list` / `page` / `create` / `update` / `delete` / `export` / `import`。

## Controller

- `@RestController`；**在方法上写完整路径**，类上避免笼统 `@RequestMapping` 前缀包办一切。
- Controller 负责组装 API 结构（Result/VO）；领域逻辑在 app/service。
- 文档注解：`@Tag`、`@Operation`（Swagger / OpenAPI）。

## API

- REST：GET 查、POST 建、PUT 全量、PATCH 部分、DELETE 删。
- 成功响应一般不强制包装；错误走统一 `code` / `msg`（`BizException` 体系）。
- 分页请求常见：`page`、`pageSize`；响应含 `records`、`total`、`page`、`pageSize`、`pages`。

## Database

- 表/字段：`snake_case`；系统表 `sys_` 前缀；主键 `id`。
- 审计字段常见：`create_time`、`update_time`、`create_user`、`update_user`；逻辑删除 `deleted`。
- 索引：`pk_` / `uk_` / `idx_` 前缀约定。
- **变更必须走 Liquibase**（`db/changelog/postgresql/`）。

## Exceptions

- 一功能一 `XxxException extends BizException`，错误码与文案在**静态工厂**。
- 禁止业务代码 `throw new BizException(...)` / `BadRequestException(...)` 表达领域错误。
- 入参格式：Bean Validation；`Check` / `ValidationUtils` 禁止新增。
- 详见 `.cursor/rules/biz-exception.mdc` 与现有 `UserException`、`AuthException` 等。

## Transactions & logging

- `@Transactional` 只放在 Service / app 层；粒度小；避免事务内远程调用与长耗时 IO。
- 日志：关键业务用 INFO；异常带堆栈用 ERROR；排查用 DEBUG。

## Dependencies

- 版本只在后端根 `pom.xml` 的 `properties` / `dependencyManagement` 登记后再引用。
