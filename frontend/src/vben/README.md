# src/vben — 内联的 Vben 壳

原 workspace 薄包 + `effects`（layouts / request / hooks / common-ui / plugins 等）。

通过根目录 [`vben.aliases.mts`](../../vben.aliases.mts) 以 `@vben/*` 解析；**业务代码仍用 `@vben/...` import，不必改业务文件。**

| 目录 | 原包名 |
|------|--------|
| `layouts` | `@vben/layouts` |
| `request` | `@vben/request` |
| `hooks` | `@vben/hooks` |
| `common-ui` | `@vben/common-ui` |
| `plugins` | `@vben/plugins` |
| `stores` / `locales` / `preferences` / … | 同名 `@vben/*` |

UI 内核仍在 `vendor/@core`（`@vben-core/*`）。
