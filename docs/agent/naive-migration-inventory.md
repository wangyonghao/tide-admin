# Naive UI 迁移清单

统计范围：`frontend/src` 里 `from 'naive-ui'` 的具名导入。一个文件导入一次记 1。共 **50** 个文件。  
`adapter/component/index.ts` 另有 `naive-ui/es/*` 动态导入，给 `useVbenForm` 供控件，不计入上表，但在下面单独标出。

下面表格是 **Phase 2 之后仍在导入** 的数量。Naive **保持安装**。Vxe / `NDataTable` 与表单适配器未动。

## Phase 2 已换

同一 PR 在 Phase 1 原子层上继续换页面，不改架构。

- **工具栏按钮**：列表页里 `#toolbar-tools` 或明显的主/次操作按钮改为 `#/ui/button`。两个及以上按钮用 `ToolbarActions`。只给这些按钮留白的 `NSpace` 去掉。单按钮工具栏直接放 `Button`。
- **筛选框**：无前缀、字符串、清除后当空的搜索框改为 `FilterInput`（登录日志 IP、操作日志的操作类型与 IP、短信日志手机号）。带 `#prefix`、密码、文本域、写在 `NForm` / `useVbenForm` 里的输入未动。
- **勾选**：代码生成配置抽屉里四个列表勾选改为 `#/ui/checkbox`，绑定是 `v-model`，不是 Naive 的 `v-model:checked`。
- **反馈**：只改 `views/open/app/index.vue`。成功/失败从 `useMessage` 换成 `#/ui/sonner` 的 `toast`。`useDialog` 仍留在该页。没有新增确认框模式。
- **未做 drop-in**：部门/角色上的 `NBadge` 是圆点加自定义颜色，不是 Badge 胶囊。

`NButton` 具名导入 34 → **31**，正文里的 `NButton` 出现次数 265 → **189**。`NSpace` 具名导入 22 → **20**。`useMessage` 27 → **26**。`NCheckbox` 6 → **5**。`NInput` 的文件数仍是 23（换掉的四个框所在文件还留着别的 `NInput`）。

彻底不再导入 `NButton` 的页面：`views/code/generator/index.vue`、`views/monitor/log/login-log.vue`、`views/user/profile/components/operation-logs.vue`。不再导入 `NSpace` 的：代码生成列表、`views/user/message/components/my-message.vue`。

仍跳过：表单适配器与 Vxe `CellLink`；带 `:loading` 的抽屉/弹层底栏；`NPopconfirm` 里的行内文字按钮；`demos/naive`；带前缀的搜索框；角色权限树勾选；`NSwitch` 的 `1/0`。

## 目录选择

原子实现继续放在 `@vben-core/shadcn-ui/src/ui`（已有 reka-ui、CVA、`cn()`，颜色经 `frontend/src/styles/theme.css` 的 `--color-*` 接 HSL token）。  
应用侧只加路径入口，避免第二套 Button：

| 层 | 路径 | 导入 |
|----|------|------|
| 原子 | `frontend/src/ui/<atom>` | `import { Button } from '#/ui/button'` |
| 模式 | `frontend/src/ui-patterns/<pattern>` | `ToolbarActions`、`FilterInput` |

包增加子路径 `./ui/*`，新原子（Skeleton、Sonner）**不**进入 `@vben-core/shadcn-ui` 根桶，避免壳层整包把 `vue-sonner` 带进去。

第一波入口：`button`、`input`、`label`、`checkbox`、`switch`、`dialog`、`alert-dialog`、`badge`、`skeleton`、`sonner`。

## 可先换

页面上的轻控件，套件里已有对应原子。表单适配器里的同名控件不要在这一波动（见「后换」）。

| Naive | 文件数 | 换成 | 例子 |
|-------|--------|------|------|
| `NButton` | 31 | `#/ui/button` | 行内文字按钮，如 `views/schedule/job/index.vue`；工具栏大多已换 |
| `NSpace` | 20 | `flex` + `gap-*` 或 `ToolbarActions` | 仍包着选择器或行内操作的，如 `views/system/file/index.vue` |
| `NInput` | 23 | `#/ui/input` 或 `FilterInput` | 带前缀的搜索框仍是 Naive，如 `views/system/user/index.vue`。无前缀筛选用 `FilterInput` |
| `NCard` | 12 | 已有 Card（`#/ui` 尚未再导出，需要时再加路径） | `views/demos/naive/index.vue` |
| `NDivider` | 5 | 已有 Separator | `views/system/config/index.vue` |
| `NCheckbox` | 5 | `#/ui/checkbox` | `views/system/role/components/role-permission.vue`。绑定用 `v-model`（`modelValue`） |
| `NBadge` | 2 | `#/ui/badge` | `views/system/role/index.vue` |
| `NSpin` | 1 | 已有 Spinner | 单页用量 |
| `NText` | 1 | 排版类（`text-foreground` 等） | 单页用量 |

`NButton` 的 31 仍含 `adapter/vxe-table.ts` 的 `CellLink`。那一处跟 Vxe 渲染绑在一起，跟页面工具栏分开换。

## 后换

有原子或模式可对上，但 API、语义色或命令式调用还不一样。先补模式或改适配器，再动页面。

| Naive | 文件数 | 原因 | 例子 |
|-------|--------|------|------|
| `useMessage` | 26 | Sonner 已挂到 `app.vue`；开放应用列表已改 `toast`，其余调用点仍是 Naive | `views/schedule/job/index.vue` |
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
