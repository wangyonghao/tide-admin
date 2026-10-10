# Frontend conventions

配合 `.cursor/rules/frontend-vue.mdc`。主应用在 `frontend/src`：新 UI 走 shadcn 原子，Naive 过渡保留。

**UI 风格与页面骨架（写页面必读）：**

- [`frontend/DESIGN.md`](../../frontend/DESIGN.md) — 视觉 token、Do/Don't、Agent 提示
- [`docs/agent/ui-patterns.md`](ui-patterns.md) — CRUD / 树表等范式与黄金样板
- [`docs/agent/naive-migration-inventory.md`](naive-migration-inventory.md) — Naive 可先换 / 后换 / 暂留

## UI atoms

新界面优先 shadcn-vue 原子，路径导入，不建总桶：

```ts
import { Button } from '#/ui/button';
import { ToolbarActions } from '#/ui-patterns/toolbar-actions';
```

- 应用入口：`frontend/src/ui/<atom>`、`frontend/src/ui-patterns/<pattern>`
- 实现：`@vben-core/shadcn-ui/src/ui`（`cn` + CVA + reka-ui）。颜色用现有 HSL token，经 `src/styles/theme.css` 的 `--color-*`
- 新组件不要新增 `naive-ui` 导入，除非控件属于暂留类（表、树、复杂选择器）。`naive-ui` 保持依赖
- 壳层可以继续用 `@vben-core/shadcn-ui` 根导出里的 `Vben*` 封装；业务页不要再复制一套同名原子

## Layout

```
frontend/
├── src/                 # 业务 + src/vben 壳 + src/styles/theme.css
│   ├── ui/              # 原子路径入口（再导出 @vben-core/shadcn-ui）
│   └── ui-patterns/     # 页面组合，按目录导入
└── vendor/@core         # UI 内核（慎改；原子实现已在 shadcn-ui）
```

- 业务代码 → `frontend/src/`（不含 `vben/`）
- 壳层封装 → `frontend/src/vben/`
- 工程配置 → 根目录 `vite.config.ts` / `eslint.config.mjs` / `tsconfig.json` 等
- **慎改** `vendor/@core`

## Naming

- 组件名 PascalCase；文件名 kebab-case（如 `user-list.vue`）
- 变量 camelCase；常量 `UPPER_SNAKE`；CSS 类 kebab-case

## Vue / TS

- 优先 `<script setup lang="ts">` + Composition API
- 对象类型优先 `interface`；避免 `any`，不确定用 `unknown`
- 样式优先 Tailwind；复杂样式再用 scoped SCSS

## API modules

- 按域拆文件（如 `api/modules/user.ts`）
- 使用项目既有 `request` 封装；类型与后端契约对齐

## Dependencies

- 新依赖写入根 `pnpm-workspace.yaml` 的 **catalog**，应用内用 `catalog:` 引用
- Node / pnpm 版本见 `frontend/package.json` `engines` 与 `specs/tech-stack.md`

## Quality

- 遵循仓库 ESLint / Stylelint / oxfmt / oxlint；勿绕过 hooks 提交
- 路由 `meta`（title、权限等）与现有模块保持一致风格
