# platform/ops 分层

`platform/ops` 仍是一个模块，里面是 audit、notification、settings。没有拆成独立服务。

## 现在的边界

| 域 | Web | 其余 |
|----|-----|------|
| settings | `settings/adapter/web`（配置、字典） | 已按 adapter / app / domain / infrastructure |
| notification | `notification/adapter/web`（公告、短信日志、站内信、短信配置） | service / mapper 仍是原来的包，没有改成 app / domain |
| audit | `audit/adapter/web`（操作日志） | service / mapper 仍是原来的包 |

控制器都在 `adapter.web`，和 iam 的 Web 入口同一层。扫描根仍是 `top.wyhao`，移动包名不改变 URL。

## 没有继续做的

notification 和 audit 的领域服务没有按 iam 再拆一遍。那样要改事务边界和 Mapper 调用，超出这次对齐。不要在 `controller` 包下再加新的 Web 类；新接口放 `adapter/web`。

## 与 P1-A 的关系

本页描述即 ops 的半迁移**收口态**（不再把 notification/audit 拆成 COLA）。总表见 [`p1a-half-migrate.md`](p1a-half-migrate.md)。
