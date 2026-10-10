# Naive UI 迁移清单（Phase 1）

统计范围：`frontend/src` 里 `from 'naive-ui'` 的具名导入。一个文件导入一次记 1。共 **50** 个文件。  
`adapter/component/index.ts` 另有 `naive-ui/es/*` 动态导入，给 `useVbenForm` 供控件，不计入上表，但在下面单独标出。

Naive **保持安装**。本阶段只建立原子层，并改了开放应用列表的工具栏按钮。

## 目录选择

原子实现继续放在 `@vben-core/shadcn-ui/src/ui`（已有 reka-ui、CVA、`cn()`，颜色经 `frontend/src/styles/theme.css` 的 `--color-*` 接 HSL token）。  
应用侧只加路径入口，避免第二套 Button：

| 层 | 路径 | 导入 |
|----|------|------|
| 原子 | `frontend/src/ui/<atom>` | `import { Button } from '#/ui/button'` |
| 模式 | `frontend/src/ui-patterns/<pattern>` | `import { ToolbarActions } from '#/ui-patterns/toolbar-actions'` |

包增加子路径 `./ui/*`，新原子（Skeleton、Sonner）**不**进入 `@vben-core/shadcn-ui` 根桶，避免壳层整包把 `vue-sonner` 带进去。

第一波入口：`button`、`input`、`label`、`checkbox`、`switch`、`dialog`、`alert-dialog`、`badge`、`skeleton`、`sonner`。

## 可先换

页面上的轻控件，套件里已有对应原子。表单适配器里的同名控件不要在这一波动（见「后换」）。

| Naive | 文件数 | 换成 | 例子 |
|-------|--------|------|------|
| `NButton` | 34 | `#/ui/button` | `views/open/app/index.vue` 工具栏（本阶段）；`views/code/generator/index.vue` |
| `NSpace` | 22 | `flex` + `gap-*` | `views/schedule/job/index.vue` |
| `NInput` | 23 | `#/ui/input` | `views/system/user/index.vue` 搜索框。无清除、密码、字数统计 |
| `NCard` | 12 | 已有 Card（`#/ui` 尚未再导出，需要时再加路径） | `views/demos/naive/index.vue` |
| `NDivider` | 5 | 已有 Separator | `views/system/config/index.vue` |
| `NCheckbox` | 6 | `#/ui/checkbox` | `views/system/role/components/role-permission.vue`。绑定是 `checked`，不是 Naive 的 `value` |
| `NBadge` | 2 | `#/ui/badge` | `views/system/role/index.vue` |
| `NSpin` | 1 | 已有 Spinner | 单页用量 |
| `NText` | 1 | 排版类（`text-foreground` 等） | 单页用量 |

`NButton` 的 34 含表格渲染 `adapter/vxe-table.ts` 的 `CellLink`。那一处跟 Vxe 渲染绑在一起，跟页面工具栏分开换。

## 后换

有原子或模式可对上，但 API、语义色或命令式调用还不一样。先补模式或改适配器，再动页面。

| Naive | 文件数 | 原因 | 例子 |
|-------|--------|------|------|
| `useMessage` | 27 | Sonner 已挂到 `app.vue`，调用点仍是 Naive | `views/open/app/index.vue` |
| `NTag` | 18 | Badge 已补 `success` / `warning`，字典色映射还未收成模式 | `views/schedule/log/index.vue` |
| `NSelect` | 12 | shadcn Select 已有，表单 schema 仍走 Naive | `views/monitor/log/login-log.vue` |
| `NIcon` | 12 | 可换 Lucide，但图标选择器是一整块 | `components/icon-select.vue` |
| `NForm` / `NFormItem` | 10 / 10 | 手写表单；目标是 `useVbenForm` 或后续原子表单 | `views/schedule/job/edit-drawer.vue` |
| `useDialog` | 9 | AlertDialog 已有，命令式 `dialog.warning` 还未包成模式 | `views/open/app/index.vue` |
| `NDrawer` / `NDrawerContent` | 8 / 8 | 新页用 `useVbenDrawer` | `views/monitor/log/operation-log.vue` |
| `NPopconfirm` | 7 | 需要确认模式 | `views/monitor/sms/log/index.vue` |
| `NRadioGroup` / `NRadio` | 7 / 5 | RadioGroup 已在套件内，未做应用路径 | `views/system/menu/index.vue` |
| `NSwitch` | 6 | 原子只接布尔；`checked-value` / `unchecked-value` 对不上 | `views/schedule/job/index.vue`（1 / 0） |
| `NInputNumber` | 6 | NumberField 已在套件内 | `views/schedule/job/edit-drawer.vue` |
| `NModal` | 4 | Dialog 已有；存量居中弹层按页迁 | `views/system/menu/index.vue` |
| `NDropdown` | 3 | DropdownMenu 已在套件内 | `views/system/role/index.vue` |
| `NCheckboxGroup` | 3 | 成组选择还没有应用路径 | `views/schedule/job/edit-drawer.vue` |
| `NPopover` | 2 | Popover 已在套件内 | `views/schedule/job/index.vue` |
| `NTabs` / `NTabPane` | 2 / 2 | Tabs 已在套件内 | `views/system/role/index.vue` |
| `NDescriptions` / `NDescriptionsItem` | 2 / 2 | 需要描述列表模式 | `views/system/user/components/user-detail-drawer.vue` |
| `NImage` | 2 | 含 Vxe `CellImage` | `adapter/vxe-table.ts` |
| `useNotification` | 1 | 仍走 Naive provider | `views/demos/naive/index.vue` |
| `NAlert` | 1 | 套件还没有 Alert 路径 | `views/system/config/index.vue` |
| `NEmpty` | 1 | 套件还没有 Empty | `views/user/profile/components/security-settings.vue` |
| `NPagination` | 1 | 这页的分页跟手写表在一起，随表格迁 | `views/system/role/index.vue` |
| `NSteps` / `NList` / `NTimeline` / `NScrollbar` | 各 1–3 | 套件里有的还没做应用路径 | 见对应页面 |

`adapter/component/index.ts` 动态加载的 Button、Checkbox、Divider、Input、InputNumber、Radio、Select、Space、Switch 属于这一类：`useVbenForm` 约定空值是 `null` 不是 `undefined`（`adapter/form.ts`）。整表替换会碰到重置行为。

## 暂留

表、树、复杂选择器，以及还要托住上述调用的壳。Vxe Grid 继续当表格默认，不换 `NDataTable`。

| Naive | 文件数 | 例子 |
|-------|--------|------|
| `NDataTable` + `DataTableColumns` | 14 / 13 | `views/system/notice/index.vue`、`views/system/user/index.vue`、`views/demos/table/index.vue` |
| `NDatePicker` | 5 | `views/monitor/log/login-log.vue` |
| `NTreeSelect` | 3 | `views/system/menu/index.vue` |
| `NSplit` | 3 | `views/system/user/index.vue`、`views/system/config/index.vue` |
| `NTree` | 2 | `views/system/user/index.vue` |
| `NUpload` + 上传类型 | 2 + 类型 | `views/system/file/index.vue`、`components/image-upload.vue` |
| `NCascader` | 1 | `views/system/file/index.vue` |
| `NTimePicker` | 1 | `views/schedule/job/edit-drawer.vue`（适配器里另有动态导入） |
| `NConfigProvider`、`NMessageProvider`、`NDialogProvider`、`NNotificationProvider`、主题与语言包 | `app.vue` | 调用点还在就留着 |
| `createDiscreteApi` | `adapter/naive.ts` | 设置页等在 setup 外发消息 |

适配器里的 `date-picker`、`time-picker`、`tree-select`、`upload` 同样暂留。

## 新代码

- 优先 `#/ui/<atom>` 与 `#/ui-patterns/<pattern>`。
- 新组件不要新增 `naive-ui` 导入，除非控件属于暂留类。
- 不要新增 `Foo` + `FooShadcn` 两套名字。壳上已有的 `VbenButton` 等封装留在壳里，业务页用原子 `Button`。
