# Vendor — `@core` only

Vben UI 内核（布局原子组件等）。**日常 vibe coding 只改 `src/`**；壳层业务封装已内联到 `src/vben/`。

| 路径 | 含义 |
|------|------|
| `@core/base` | shared / design / icons / typings |
| `@core/composables` / `preferences` | 组合式与偏好内核 |
| `@core/ui-kit` | form / layout / menu / popup / shadcn / tabs |

`@vben/*` 通过根目录 `vben.aliases.mts` 映射到 `src/vben/*`。
