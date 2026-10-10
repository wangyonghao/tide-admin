# Tide Admin — UI page patterns

> 与 [`frontend/DESIGN.md`](../../frontend/DESIGN.md) 配套。  
> **目的：** 用固定页面骨架消灭「每页一种写法」。视觉细节以 DESIGN.md 为准。  
> **主应用：** `frontend/src`

## 组件分层

| 层 | 路径 | 导入 |
|----|------|------|
| 原子 | `frontend/src/ui/<atom>` | `import { Button } from '#/ui/button'` |
| 模式 | `frontend/src/ui-patterns/<pattern>` | `import { ToolbarActions } from '#/ui-patterns/toolbar-actions'` |

原子实现在 `@vben-core/shadcn-ui/src/ui`（reka-ui + CVA）。应用代码走上面的路径，不从包根桶整包引入，也不要再包一层 `Foo` / `FooShadcn`。

**新代码：** 优先 `#/ui/*`。不要在新组件里新增 `naive-ui` 导入，除非该控件属于暂留类（表、树、复杂选择器，见 [`naive-migration-inventory.md`](naive-migration-inventory.md)）。Naive 保持安装；Vxe Grid 仍是表格默认。

已有模式壳：`ui-patterns/toolbar-actions`（两个及以上工具栏按钮，`inline-flex` + `gap-2`）；`ui-patterns/filter-input`（无前缀的字符串筛选框，清除后为空字符串）；`ui-patterns/confirm-action`（AlertDialog 确认，`ask()` 返回是否确认）。分割线走 `#/ui/separator`。状态开关走 `#/ui/switch`：默认布尔；`checked-value` / `unchecked-value` 用来接 `1/0` 这类字段，比较是严格相等。

---

## 范式总表

| ID | 名称 | 何时用 | 黄金样板 | 状态 |
|----|------|--------|----------|------|
| P-CRUD | CRUD 列表 | 标准资源列表 + 筛选 + 增删改查 | `views/open/app/` | **canonical** |
| P-TREE-TABLE | 树 + 表 | 左组织/分类树，右列表 | `views/system/user/` | transitional（宜迁到 Vxe） |
| P-JOB-GRID | 调度/日志网格 | 与 CRUD 同构，搜索 `submitOnChange` | `views/schedule/job/` | canonical 变体 |
| P-DASH | 仪表盘 | 卡片栅格、图表、工作台 | `views/dashboard/` | canonical |
| P-AUTH | 认证页 | 登录/注册/改密 | `views/_core/authentication/` | canonical（跟 Vben） |
| P-FALLBACK | 空态/错误 | 403/404/500/offline | `views/_core/fallback/` | canonical |
| P-PROFILE | 个人中心 | Tab + 分组表单 | `views/user/profile/`、`views/_core/profile/` | 宜收敛到一种 |
| P-CONFIG | 配置/字典 | 分组表单或可编辑表 | `views/system/option/`、`config/` | 按模块对齐 CRUD 或 Form |
| P-LEGACY-TABLE | 手写 NDataTable | **仅存量** | `views/system/notice/` 等 | **禁止新建**。系统选项、在线用户已改 P-CRUD |

---

## P-CRUD — CRUD 列表（默认新页必选）

### 骨架

```
Page(auto-content-height)
├── FormDrawer / DetailDrawer   ← useVbenDrawer + connectedComponent
└── Grid                        ← useVbenVxeGrid
    ├── formOptions.schema      ← 搜索（data.ts）
    ├── gridOptions.columns     ← 列 + 行操作（data.ts）
    └── #toolbar-tools          ← 新建(primary) → 导出(default) → …
```

### 文件约定

```
views/<domain>/<feature>/
├── index.vue          # 页面组装，少写业务 UI 细节
├── data.ts            # useColumns / useGridFormSchema / useFormSchema
└── modules/           # form.vue、detail.vue（或 components/）
```

### 技术清单

| 项 | 选择 |
|----|------|
| 布局 | `Page` from `@vben/common-ui`，`auto-content-height` |
| 表格 | `useVbenVxeGrid` from `#/adapter/vxe-table` |
| 搜索/编辑表单 | `useVbenForm` / schema in `data.ts` |
| 编辑容器 | `useVbenDrawer`（模块内统一；不要又 Drawer 又 Modal） |
| 反馈 | `useMessage`；删除用 `useDialog` |
| 文案 | `$t(...)` |
| API | `#/api/...` 与后端域对齐 |

### 参考实现

- 完整：`src/views/open/app/index.vue` + `data.ts` + `modules/*`
- 变体（搜索即时提交）：`views/schedule/job/index.vue`

### Checklist（新页 / PR）

- [ ] 使用 `Page` + `useVbenVxeGrid`，无整页手写 `NDataTable`
- [ ] 列与搜索 schema 在 `data.ts`（或 `data-scope.ts`）
- [ ] 工具栏：Primary 新建在前
- [ ] 无硬编码色值
- [ ] Drawer/Modal 二选一且本模块一致
- [ ] 读过 `frontend/DESIGN.md` §7

---

## P-TREE-TABLE — 树 + 表

### 骨架

```
Page
└── NSplit (horizontal, left 200–320px)
    ├── #1  左：搜索输入 + NTree（bg-background p-4）
    └── #2  右：筛选 + 操作 + 表格（bg-background p-4）
        └── Drawers
```

### 约定

- 左右内边距对称（`p-4`）；选中树节点刷新右侧列表。
- **目标态：** 右侧改为 `useVbenVxeGrid`（与 P-CRUD 一致）。
- **现状：** `system/user` 仍为 `NDataTable` + 本地分页 → 标为 transitional，迁移时按 P-CRUD 右栏替换。

### 参考

- `views/system/user/index.vue`（布局参考）
- 迁移完成后更新本表「黄金样板」路径

---

## P-JOB-GRID — CRUD 变体（监控 / 调度）

与 P-CRUD 相同，差异仅：

- `formOptions.submitOnChange: true`（可选）
- `showCollapseButton: false`（字段少时）
- 行内可能有 `NSwitch`、时间线等业务控件（放在列 `slots`）

样板：`views/schedule/job/`、`views/monitor/sms/log/`

---

## P-DASH — 仪表盘

### 骨架

```
Page 或纯容器
└── 栅格（Tailwind grid）
    ├── 统计卡 / Workbench* / AnalysisChartCard
    └── 图表子组件
```

### 约定

- 优先 `@vben/common-ui` 的 `Workbench*`、`AnalysisChartCard` 等，避免自造卡片阴影体系。
- gutter 统一（如 `gap-4`）；卡片不要各用各的 padding 魔法数。
- 样板：`views/dashboard/workspace/`、`views/dashboard/analytics/`

---

## P-AUTH / P-FALLBACK

- 跟 Vben 认证布局与 `_core` 实现；**不要**在业务模块复制登录壳。
- 改品牌（logo/标题）走 preferences / 项目配置，不改每页样式。

---

## P-PROFILE / P-CONFIG

- 个人中心：Tab + 分区表单；注意 `user/profile` 与 `_core/profile` 并存，**新改动只改一处并逐步收敛**。
- 系统配置：能表格式管理的走 P-CRUD；纯表单配置用 `useVbenForm` + 分组标题，表面用 `bg-background`，勿多层嵌套 Card。

---

## P-LEGACY-TABLE — 存量手写表（禁止新建）

特征：页面内 `NDataTable` + `tablePagination` ref + 手写搜索行。

已知存量（迁移候选，非完整列表）：

| 区域 | 示例 |
|------|------|
| system | `notice`、`user`（表侧）、`dept` / `menu` / `role`（树或权限矩阵）、`file` |
| monitor | `log/*`（登录 / 操作 / 短信在同一个 Tab） |
| user | `operation-logs`（个人中心里的一块） |

已迁到 `useVbenVxeGrid`：`views/system/option/`、`views/monitor/online/`。`views/demos/table` 仍是 Naive 示例。

**规则：** 新功能、大改版列表 → 升为 P-CRUD；小 bugfix 可不强制迁移。

---

## 选型决策树

```
需要登录/错误页？ ──是──► P-AUTH / P-FALLBACK
        │否
仪表盘/工作台？ ──是──► P-DASH
        │否
左树右表？ ──是──► P-TREE-TABLE（右表目标 Vxe）
        │否
标准资源 CRUD / 日志列表？ ──是──► P-CRUD（或 P-JOB-GRID）
        │否
个人设置 / 系统项配置？ ──► P-PROFILE / P-CONFIG
```

---

## 反模式（快速对照）

| 反模式 | 改为 |
|--------|------|
| 新页复制 `notice/index.vue` 手写表 | 复制 `open/app` |
| 列表外包一层大 `NCard` | 直接 `Page` > `Grid` |
| 每页不同按钮顺序 | Primary 新建 → 次要 → … |
| 编辑用 Drawer、详情又用另一套居中 Modal 无说明 | 模块内统一；详情可 Drawer |
| `#ff0000` / `text-gray-400` 与语义 token 混用 | `text-muted-foreground` 等 |

---

## 与 Agent / 文档的关系

| 文档 | 职责 |
|------|------|
| `frontend/DESIGN.md` | 看起来怎样（色、字、Do/Don't） |
| **本文件** | 页面怎样搭（骨架、样板、清单） |
| `docs/agent/frontend-conventions.md` | 工程约定（目录、命名、依赖） |

新建或大改 UI 时顺序：DESIGN.md → 本文件选型 → 复制样板 → 填 API/列。
