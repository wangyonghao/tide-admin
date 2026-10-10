# Vendor — `@core` only

Vben UI 内核（布局原子组件等）。**日常改产品代码在 `apps/admin/src/`**；壳层业务封装在 `apps/admin/src/vben/`。

| 路径 | 含义 |
|------|------|
| `@core/base` | shared / design / icons / typings |
| `@core/composables` / `preferences` | 组合式与偏好内核 |
| `@core/ui-kit` | form / layout / menu / popup / shadcn / tabs |

`@vben/*` 通过 `apps/admin/vben.aliases.mts` 映射到 `apps/admin/src/vben/*`。
