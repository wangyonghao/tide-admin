# Naive UI 迁移清单

统计范围：`frontend/src` 里 `from 'naive-ui'` 的具名导入。一个文件导入一次记 1。共 **50** 个文件。  
`adapter/component/index.ts` 另有 `naive-ui/es/*` 动态导入，给 `useVbenForm` 供控件，不计入上表，但在下面单独标出。

下面表格是 **Phase 4 之后仍在导入** 的数量。Naive **保持安装**。Vxe / `NDataTable` 与表单适配器未动。

## Phase 2 已换

同一 PR 在 Phase 1 原子层上继续换页面，不改架构。

- **工具栏按钮**：列表页里 `#toolbar-tools` 或明显的主/次操作按钮改为 `#/ui/button`。两个及以上按钮用 `ToolbarActions`。只给这些按钮留白的 `NSpace` 去掉。单按钮工具栏直接放 `Button`。
- **筛选框**：无前缀、字符串、清除后当空的搜索框改为 `FilterInput`（登录日志 IP、操作日志的操作类型与 IP、短信日志手机号）。带 `#prefix`、密码、文本域、写在 `NForm` / `useVbenForm` 里的输入未动。
- **勾选**：代码生成配置抽屉里四个列表勾选改为 `#/ui/checkbox`，绑定是 `v-model`，不是 Naive 的 `v-model:checked`。
- **反馈**：只改 `views/open/app/index.vue`。成功/失败从 `useMessage` 换成 `#/ui/sonner` 的 `toast`。当时 `useDialog` 仍留在该页，Phase 3 改成 `ConfirmAction`。
- **未做 drop-in**：部门/角色上的 `NBadge` 是圆点加自定义颜色，不是 Badge 胶囊。

`NButton` 具名导入 34 → **31**，正文里的 `NButton` 出现次数 265 → **189**。`NSpace` 具名导入 22 → **20**。`useMessage` 27 → **26**。`NCheckbox` 6 → **5**。`NInput` 的文件数仍是 23（换掉的四个框所在文件还留着别的 `NInput`）。

彻底不再导入 `NButton` 的页面：`views/code/generator/index.vue`、`views/monitor/log/login-log.vue`、`views/user/profile/components/operation-logs.vue`。不再导入 `NSpace` 的：代码生成列表、`views/user/message/components/my-message.vue`。

仍跳过：表单适配器与 Vxe `CellLink`；带 `:loading` 的抽屉/弹层底栏；`NPopconfirm` 里的行内文字按钮；`demos/naive`；带前缀的搜索框；角色权限树勾选；`NSwitch` 的 `1/0`。

## Phase 3 已换

- **`loading`**：原子 `Button` 增加可选 `loading`（`LoaderCircle` + `disabled`），和壳上的 `VbenButton` 一样。部门、用户、角色三个抽屉底栏，以及角色权限工具栏的「保存」，已经用上。
- **确认**：`ui-patterns/confirm-action` 包一层 AlertDialog，`ask()` 返回 `Promise<boolean>`。开放应用「重置密钥」改走这条。取消不再弹出失败提示。该页不再导入 `naive-ui`。
- **按钮**：角色列表工具栏、我的消息/公告标题、应用详情复制、用户详情底栏、个人资料基本信息和通知设置里的普通按钮。`NButton` 具名导入 31 → **19**，正文出现次数 189 → **131**。`NSpace` 20 → **17**。
- **分割线**：视图里的 `NDivider` 具名导入 5 → **0**，换成 `#/ui/separator`。适配器里的动态导入还在。
- **勾选 / 徽标**：系统配置里 8 个布尔勾选改为 `Checkbox`（`v-model`）。角色用户性别改为 `Badge`。部门状态圆点 `NBadge` 仍留着。`NCheckbox` 5 → **4**，`NBadge` 2 → **1**。`useDialog` 9 → **8**。

仍跳过：`NPopconfirm` 行、菜单/通知/选项/系统配置里还带加载的底栏、`NCheckboxGroup`、角色权限树里的勾选、`NSwitch` 的 `1/0`、`demos/naive`。这些底栏在 Phase 4 换完。

## Phase 4 已换

- **加载底栏**：菜单弹层、通知表单（草稿/发布）、选项抽屉、系统配置保存和邮件抽屉，以及发送验证码按钮，改为带 `loading` 的原子 `Button`。通知范围/定时/置顶那几组只包按钮的 `NSpace` 改成 flex。
- **确认**：`ConfirmAction` 接了部门删除、菜单删除、用户删除与重置密码、选项删除、在线用户强退（单条和批量）、短信日志删除、定时任务删除。都是「布尔确认后再请求」。选项「清除缓存」仍是 `dialog.info`。定时任务「立即执行」仍是 `NPopconfirm`。
- **数量**：`NButton` 19 → **15**（正文 131 → **96**）。`NSpace` 17 → **11**。`useDialog` 8 → **4**。`NPopconfirm` 7 → **5**。

留给当时的下一波：表单适配器、`NSwitch` 的 `1/0`、`NDataTable` / Vxe、`demos/naive`、字典 `NTag`、成组勾选、部门圆点徽标、公告列表和短信日志表格里的渲染函数确认、个人设置里的重置/解绑确认、角色页和「全部已读」、配置页「发送测试邮件」。Naive 包不删。这些确认和一部分开关、按钮、表单控件在下面这一波换了。

## 下一波已换

口径和上面一样：`from 'naive-ui'` 的具名导入记文件数。正文 token 含闭合标签，不含 import 行。本波开始时按这个口径复测：`NButton` 15 / 正文 95（Phase 4 记正文 96），`useDialog` 4，`NPopconfirm` 5 / 正文 8，`NSwitch` 具名 6 / 正文 20，`NSpace` 具名 11。

- **确认**：`useDialog` 4 → **0**，`NPopconfirm` 5 → **0**。选项清除缓存、配置测试邮件、消息全部已读、角色删除与取消分配、公告删除、短信日志表格删除、任务立即执行、偏好重置、三方账号解绑，都是 `ConfirmAction` 返回 true 后再请求。
- **行内按钮**：菜单、部门、用户、文件、公告操作列，公告表单的范围 / 定时 / 置顶，选项行，操作日志「详情」，任务行的执行 / 编辑 / 日志，改为 `#/ui/button`。文字用 `variant="link"`，图标用 `ghost`，删除加上 `text-destructive`。`NButton` 具名 15 → **4**，正文 95 → **59**。剩下 `adapter/vxe-table.ts` 的 `CellLink`、安全设置弹层底栏、`demos/naive`、`demos/form`。
- **开关**：`#/ui/switch` 在布尔之外接受 `checked-value` / `unchecked-value`（默认 `true` / `false`，用 `===`）。任务状态 `1/0`、菜单外链 / 可见 / 状态 `1/0`、部门状态 `1/2`，以及选项、偏好、通知里的布尔开关已经接上。页面不再具名导入 `NSwitch`。表单适配器里的 `Switch` 仍是 Naive。
- **表单适配器**：只换了能对上 `value` / `checked` 的切片。`DefaultButton` 与 `PrimaryButton`、单行 `Input`、`Checkbox` 走原子控件。`emptyStateValue` 仍是 `null`。单行输入把 `null` 画成空字符串，用户清空后回写空字符串，避免 zod 的 `string()` 收到 `null`。文本域、`showWordLimit`、密码显隐仍回退 `NInput`，回写保持 Naive 原来的值（可以是 `null`）。
- **表单里明确没动**：`Select`。套件里的 Select 是 Trigger / Content / Item，页面 schema 用的是 `options`，还带 `clearable`、`multiple`、`filterable`，空值也是 `null`。`CheckboxGroup`、`RadioGroup`、`Switch`、`InputNumber`、日期时间、树选择、上传同样留在 Naive。
- **未做**：角色权限树里的勾选、部门状态圆点 `NBadge`、`NCheckboxGroup`、`NDataTable` / Vxe、`demos/naive`。安全设置改密 / 改手机 / 改邮箱的弹层按钮仍是 `NButton`。

`NSpace` 具名 11 → **9**。用户操作列和短信日志操作列不再用它包按钮。Naive 包不删。Vxe 仍是表格默认。

## 本波已换

口径和上面一样。本波开始时：`NButton` 4（正文 59），`NCheckbox` 4，`NCheckboxGroup` 3，`NBadge` 1，页面 `NSelect` 12，`NSpace` 9。仍有 **47** 个文件导入 `naive-ui`。

- **表单 Select**：注册表里的 `Select` 改为适配器 `FormSelect`。单选和多选走 `#/ui/select`（reka Select 的 `multiple`）。`options` 的数字、布尔、字符串原样回写，`1` 和 `"1"` 不会并成一个。`null` 显示为未选；`clearable` 和多选清空都回写 `null`。`0` 和 `false` 仍是已选。`filterable` 用 Popover 加搜索框：套件下拉的可视高度跟触发器绑在一起，搜索框放不进去。应用路径补了 `select` 和 `popover`。
- **没换的 Select**：`ApiSelect` 仍包 `NSelect`。`ApiComponent` 自己管加载和 `arrow` 插槽，和 schema 上的 `Select` 不是同一条。页面上手写的 `NSelect` 仍是 **12** 个文件（公告接收人、用户编辑、任务执行器这类可搜索多选没进注册表）。
- **勾选**：角色权限树的全选、菜单、权限和「节点关联」改为 `#/ui/checkbox`，全选保留半选。权限矩阵仍是 `NDataTable`。它有菜单列和权限列，父子关联也是页面自己的规则；套件里的树只有一个节点插槽，整表换掉会改保存结果。页面上的 `NCheckboxGroup`（通知方式、通知类型、任务星期）和表单 `CheckboxGroup` 改为 Checkbox 列表。用户取消最后一项回写空数组，和原来的分组一样；`null` 只来自表单重置，显示为全不选。`NCheckbox` 4 → **0**。`NCheckboxGroup` 3 → **0**。
- **表格周边**：`CellLink` 改为 `variant="link"` 的原子按钮。`CellImage` 仍是 `NImage`。部门状态从圆点改为 `success` / `destructive` 的 Badge。安全设置的操作按钮和三个弹层底栏，以及表单演示页的两个按钮，改为原子按钮。`NButton` 4 → **1**，只剩 `views/demos/naive/index.vue`。`NBadge` 1 → **0**。`NSpace` 9 → **8**（公告表单不再用它包勾选）。
- **NDataTable**：14 个文件都留着，没有整页改成 Vxe，也没有新做 shadcn Table。在线用户、公告、用户、部门、角色、菜单、文件、日志都是手写分页或树表，不是薄包装。`views/demos/table` 是示例。Vxe 仍是 CRUD 默认。权限树只换了勾选。
- **`demos/naive`**：仍是 Naive 组件陈列，这一波不改。

Naive 包不删。

## 目录选择

原子实现继续放在 `@vben-core/shadcn-ui/src/ui`（已有 reka-ui、CVA、`cn()`，颜色经 `frontend/src/styles/theme.css` 的 `--color-*` 接 HSL token）。  
应用侧只加路径入口，避免第二套 Button：

| 层 | 路径 | 导入 |
|----|------|------|
| 原子 | `frontend/src/ui/<atom>` | `import { Button } from '#/ui/button'` |
| 模式 | `frontend/src/ui-patterns/<pattern>` | `ToolbarActions`、`FilterInput`、`ConfirmAction` |

包增加子路径 `./ui/*`，新原子（Skeleton、Sonner）**不**进入 `@vben-core/shadcn-ui` 根桶，避免壳层整包把 `vue-sonner` 带进去。

第一波入口：`button`、`input`、`label`、`checkbox`、`switch`、`dialog`、`alert-dialog`、`badge`、`skeleton`、`sonner`。Phase 3 补了应用路径 `separator`。本波补了 `select`、`popover`。

## 可先换

页面上的轻控件，套件里已有对应原子。表单适配器里的同名控件不要在这一波动（见「后换」）。

| Naive | 文件数 | 换成 | 例子 |
|-------|--------|------|------|
| `NButton` | 1 | `#/ui/button`（含 `loading`） | 只剩 `views/demos/naive/index.vue` 的组件陈列 |
| `NSpace` | 8 | `flex` + `gap-*` 或 `ToolbarActions` | 仍包着选择器或整段表单的，如 `views/system/file/index.vue` |
| `NInput` | 23 | `#/ui/input` 或 `FilterInput` | 带前缀的搜索框仍是 Naive，如 `views/system/user/index.vue`。无前缀筛选用 `FilterInput` |
| `NCard` | 12 | 已有 Card（`#/ui` 尚未再导出，需要时再加路径） | `views/demos/naive/index.vue` |
| `NDivider` | 0 | `#/ui/separator` | 视图已换完；适配器仍动态加载 |
| `NCheckbox` | 0 | `#/ui/checkbox` | 页面和表单分组都已换。绑定用 `v-model`（`modelValue`） |
| `NBadge` | 0 | `#/ui/badge` | 部门状态已改为 `success` / `destructive` 徽标 |
| `NSpin` | 1 | 已有 Spinner | 单页用量 |
| `NText` | 1 | 排版类（`text-foreground` 等） | 单页用量 |

`NButton` 只剩 `demos/naive` 的陈列。`CellLink` 和安全设置弹层已经换成原子按钮。

## 后换

有原子或模式可对上，但 API、语义色或命令式调用还不一样。先补模式或改适配器，再动页面。

| Naive | 文件数 | 原因 | 例子 |
|-------|--------|------|------|
| `useMessage` | 26 | Sonner 已挂到 `app.vue`；开放应用列表已改 `toast`，其余调用点仍是 Naive | `views/schedule/job/index.vue` |
| `NTag` | 18 | Badge 已补 `success` / `warning`，字典色映射还未收成模式 | `views/schedule/log/index.vue` |
| `NSelect` | 12 | 注册表 `Select` 已接 `#/ui/select`。这 12 个是手写表单，含可搜索和多选 | `views/monitor/log/login-log.vue`、`views/system/user/components/user-edit-drawer.vue` |
| `NIcon` | 12 | 可换 Lucide，但图标选择器是一整块 | `components/icon-select.vue` |
| `NForm` / `NFormItem` | 10 / 10 | 手写表单；目标是 `useVbenForm` 或后续原子表单 | `views/schedule/job/edit-drawer.vue` |
| `useDialog` | 0 | 页面上的布尔确认已改 `ConfirmAction` | — |
| `NDrawer` / `NDrawerContent` | 8 / 8 | 新页用 `useVbenDrawer` | `views/monitor/log/operation-log.vue` |
| `NPopconfirm` | 0 | 页面上的确认已改 `ConfirmAction` | — |
| `NRadioGroup` / `NRadio` | 7 / 5 | RadioGroup 已在套件内，未做应用路径 | `views/system/menu/index.vue` |
| `NSwitch` | 0（适配器仍动态加载） | 页面用 `#/ui/switch` 的 `checked-value` / `unchecked-value`。表单 schema 里的 Switch 还是 Naive，因为注册表仍按 `value` 绑定，而且常带 `1/0` | `adapter/component/index.ts` |
| `NInputNumber` | 6 | NumberField 已在套件内 | `views/schedule/job/edit-drawer.vue` |
| `NModal` | 4 | Dialog 已有；存量居中弹层按页迁 | `views/system/menu/index.vue` |
| `NDropdown` | 3 | DropdownMenu 已在套件内 | `views/system/role/index.vue` |
| `NCheckboxGroup` | 0 | 页面和表单 `CheckboxGroup` 已改为 Checkbox 列表 | — |
| `NPopover` | 2 | Popover 已在套件内 | `views/schedule/job/index.vue` |
| `NTabs` / `NTabPane` | 2 / 2 | Tabs 已在套件内 | `views/system/role/index.vue` |
| `NDescriptions` / `NDescriptionsItem` | 2 / 2 | 需要描述列表模式 | `views/system/user/components/user-detail-drawer.vue` |
| `NImage` | 2 | 含 Vxe `CellImage` | `adapter/vxe-table.ts` |
| `useNotification` | 1 | 仍走 Naive provider | `views/demos/naive/index.vue` |
| `NAlert` | 1 | 套件还没有 Alert 路径 | `views/system/config/index.vue` |
| `NEmpty` | 1 | 套件还没有 Empty | `views/user/profile/components/security-settings.vue` |
| `NPagination` | 1 | 这页的分页跟手写表在一起，随表格迁 | `views/system/role/index.vue` |
| `NSteps` / `NList` / `NTimeline` / `NScrollbar` | 各 1–3 | 套件里有的还没做应用路径 | 见对应页面 |

`adapter/component/index.ts` 里，提交/重置按钮、单行 Input、Checkbox、CheckboxGroup、Select 已经换成原子控件，空值仍是 `null`（`adapter/form.ts`）。Select 的清除和多选清空回写 `null`。Divider、InputNumber、Radio、Space、Switch，文本域 Input，以及 `ApiSelect` 里的 `NSelect`，仍动态加载 Naive。

## 暂留

表、树、复杂选择器，以及还要托住上述调用的壳。Vxe Grid 继续当表格默认，不换 `NDataTable`。

| Naive | 文件数 | 例子 |
|-------|--------|------|
| `NDataTable` + `DataTableColumns` | 14 / 13 | 本波确认整页都不是薄包装，继续留着。例子：`views/system/notice/index.vue`、`views/system/user/index.vue`、`views/system/role/components/role-permission.vue`、`views/demos/table/index.vue` |
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
