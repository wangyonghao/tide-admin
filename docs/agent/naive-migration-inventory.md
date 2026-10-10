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

## 页面 Select 与薄表

口径不变。本波开始时：页面 `NSelect` **12**，`NDataTable` **14**，`NSpace` **8**，`from 'naive-ui'` 的文件 **47**。

- **手写下拉**：12 个文件里的 `NSelect` 都改成适配器 `FormSelect`（`#/ui/select`，空值仍是 `null`）。单选、`clearable`、`multiple`、`filterable` 和页面上的 `style` / `class` 宽度沿用原来的字段。多选清空在适配器里是 `null`；公告接收人和用户角色在写回页面前收成数组，空选择不把 `null` 交给接口。代码生成的字段类型带 `tag`：输入列表外的名字会当成字符串提交，命中已有选项仍回写原值。个人日志筛选里原来的「全部」（`value: null`）不是合法选项，改成占位符加清除。
- **ApiSelect**：注册表里的内层控件从 `NSelect` 换成 `FormSelect`。加载中仍由 `ApiComponent` 往 `arrow` 插槽放转圈；可搜索时这个插槽换掉箭头，普通下拉时转圈出现在箭头旁边（套件触发器自带箭头，没有替换入口）。`ApiTreeSelect` 仍是 `NTreeSelect`。
- **表格**：系统选项、在线用户两页改成 `useVbenVxeGrid`（搜索 schema、远程分页、行操作）。选项状态用 Badge。在线用户当前登录不可勾选、不可强退；批量强退留在工具栏。Vxe 仍是 CRUD 默认。没有再抽一套空态/加载组件，这两页用表格自带的加载和空数据。
- **NSpace**：公告通知方式上只包标签的 `NSpace`，以及个人日志筛选那一行，改成 flex。`NSpace` 8 → **6**。`demos/naive` 仍是组件陈列，不改。
- **数量**：页面 `NSelect` 12 → **0**。`NDataTable` 14 → **12**。`useMessage` 26 → **24**，`NInput` 23 → **21**，`NTag` 18 → **17**，`NIcon` 12 → **10**。后四项是因为选项页和在线用户页不再导入 naive-ui，不是把输入框、标签或图标换成原子控件。仍有 **45** 个文件导入 naive-ui。Naive 包不删。

仍不换表的页面，以及原因：

| 页面 | 原因 |
|------|------|
| `system/role/components/role-permission.vue` | 菜单列加权限列，父子关联是页面自己的规则 |
| `system/menu/index.vue`、`system/department/index.vue` | 树表，展开状态和子节点不在现有 Vxe CRUD 样板里 |
| `system/user/index.vue` | 左树右表，表还嵌在分割布局里 |
| `system/role/index.vue` | 角色列表里再嵌用户分配表和分页 |
| `system/file/index.vue` | 上传、分类级联和批量勾选绑在同一张手写表上 |
| `system/notice/index.vue` | 时间范围、预览抽屉、多种标签列，不是薄列表 |
| `monitor/log/login-log.vue`、`operation-log.vue`、`sms-log.vue` | 三个列表塞在同一个 Tab 里，高度跟着 Tab 走；只改其中一张会和旁边两张脱节 |
| `user/profile/components/operation-logs.vue` | 个人中心里的一块，不是独立 `Page` |
| `demos/table/index.vue` | Naive 表格示例 |

## 表格、表单适配器与徽标

口径不变。本波开始时：`NDataTable` **12**，`NTag` **17**，`NSpace` **6**，`from 'naive-ui'` 的文件 **45**。Naive 包不删。

- **表格**：公告、登录日志、操作日志、短信日志、文件列表改成 `useVbenVxeGrid`。公告和文件是独立 `Page`。三张日志在同一个日志页的 Tab 里一起换，Tab 内容区拉满剩余高度，避免只换一张和旁边脱节。搜索走表单 schema，行操作和状态走插槽。公告的编辑/预览抽屉、操作日志详情抽屉、文件的上传、分类级联和行内菜单留在表格旁边。文件勾选只作用于当前页。`NDataTable` 12 → **7**。`DataTableColumns` 11 → **6**。
- **仍不换表**：

| 页面 | 原因 |
|------|------|
| `system/role/components/role-permission.vue` | 菜单列加权限列，父子关联是页面自己的规则 |
| `system/menu/index.vue`、`system/department/index.vue` | 树表，展开状态不在现有 Vxe CRUD 样板里 |
| `system/user/index.vue` | 左树右表，表嵌在分割布局里 |
| `system/role/index.vue` | 角色列表里再嵌用户分配表和分页 |
| `user/profile/components/operation-logs.vue` | 个人中心里的一块，不是独立 `Page`；日期筛选仍是 `NDatePicker` |
| `demos/table/index.vue` | Naive 表格示例 |

- **表单适配器**：文本域、`autosize`、密码显隐、字数统计改为 `#/ui/textarea` / `#/ui/input`。`null` 显示成空字符串，用户清空后回写空字符串，和单行输入一样；重置仍由外层写成 `null`。成对输入（`pair`）套件没有对应控件，仍回退 `NInput`。`RadioGroup` 走 `#/ui/radio-group`，`1` / `true` 原样回写，`null` 是未选。`isButton` 和 `optionType: 'button'` 画成按钮样式。`InputNumber` 走 `#/ui/number-field`，清空回写 `null`，`0` 仍是数字。`Switch` 走 `#/ui/switch`，`null` 显示为关，`checked-value` / `unchecked-value` 用严格相等。注册表补了 `Textarea`。
- **日期 / 树选择**：`@vben-core/shadcn-ui` 没有 DatePicker，也没有带下拉和选项值的 TreeSelect（只有树视图）。`DatePicker`、`TimePicker`、`TreeSelect`、`ApiTreeSelect` 继续动态加载 Naive。
- **徽标**：`success` / `warning` / `error` / `default` 对上 Badge 的 `success` / `warning` / `destructive` / `secondary`。`primary` 和 `info` 没有单独色，用 Badge `default`。对不上的名字不猜颜色，字典项退回 `secondary`。字典颜色读 `tagType`，没有则读 `extra.color`。页面上的 `NTag` 具名导入 17 → **0**。
- **NSpace**：个人设置里只做纵向留白的 `NSpace` 改成 flex。`NSpace` 6 → **1**，只剩 `demos/naive`。`demos/naive` 仍是组件陈列，不改。

仍有 **40** 个文件导入 naive-ui（`utils/render.tsx` 不再导入 `NTag`）。`useMessage` 24 → **21**，`NInput` 21 → **16**，`NIcon` 10 → **5**，`NCard` 12 → **10**，`NDatePicker` 5 → **2**。后几项主要是公告、日志和文件页不再为了表格去导入这些控件。

## 日期、树选择与树表

口径不变。本波开始时：`NDatePicker` **2**，`NTreeSelect` **3**，`NDataTable` **7**，`NSplit` **3**，`NTree` **2**，`from 'naive-ui'` 的文件 **40**。Naive 包不删。

- **日期**：`#/ui/date-picker` 用已有 Popover（reka）加本地月历，不包 Naive。表单注册表的 `DatePicker` 走这个控件，所以公告、登录日志、操作日志的 schema 日期范围一起换掉。`date` / `datetime` / `daterange` / `datetimerange`，以及 `showTime`。没有 `valueFormat` 时提交时间戳。有 `valueFormat` 时提交格式化字符串；`yyyy-MM-dd` 和 `YYYY-MM-DD` 都认，开放应用失效时间因此对得上 `LocalDateTime`。空值、没选完的范围、清除都是 `null`。`0` 仍是时间戳。点「确定」时如果范围只选了一端，不把已有值写成 `null`。周、月、季和快捷范围没有做：`@vben-core/shadcn-ui` 没有 Calendar 原子，也不再引入新的日期库；页面上没有这些模式。
- **树选择**：表单 `TreeSelect` 和 `ApiTreeSelect` 改为 `FormTreeSelect`。选项键优先 `keyField`，没有再退回 `value` / `key`，所以 Api 转成 `value` 之后仍能对上。`0` 是顶级菜单，不是空。清除和多选清空回写 `null`。搜索只留下命中的节点和它们的上级。没有用 `VbenTree` 做下拉：它在选项还没到时会把对不上的值写成空。菜单上级、部门上级、用户部门都改成这个控件。
- **成对输入**：表单 `Input` 的 `pair` 改成两个原子输入。`null` 画成两段空字符串；两段都空回写 `null`；只填一段时另一段是空字符串。文本域、字数和密码显隐仍走上一波的原子控件。当前 schema 没有 `pair`。
- **部门树表**：`system/department/index.vue` 改为 `useVbenVxeGrid`，`treeConfig.childrenField = children`，没有分页。搜索仍是输入后防抖查询。每次查出结果默认展开。导出的查询参数改为 `keyword`，和列表、后端 `DeptQuery` 一致。
- **菜单树表**：`system/menu/index.vue` 同样改为 Vxe 树。名称、状态走表格自己的搜索表单。弹层里的上级菜单改成 `FormTreeSelect`。弹层其余字段仍是 Naive 表单。类型、可见、状态用 Badge。
- **用户左树右表**：分割从 `NSplit` 改为已有的 `ColPage`（reka Splitter）。左侧部门树改为 `VbenTree`，按名称过滤时保留上级。点同一节点可取消，右侧列表跟着刷新。搜索把已选部门滤掉时，不清掉右侧的部门条件。右侧用户表仍是 `NDataTable`：分页、下拉操作和重置密码弹层还绑在这张表上。状态用 Badge。
- **个人日志**：`user/profile/components/operation-logs.vue` 改成一块 Vxe（远程分页）。筛选里的日期范围走新的 DatePicker。状态用 Badge。这一页不再导入 naive-ui。
- **数量**：`NDatePicker` 2 → **0**。`NTreeSelect` 3 → **0**。`NDataTable` 7 → **4**。`DataTableColumns` 6 → **3**。`NSplit` 3 → **2**。`NTree` 2 → **1**。`NInput` 16 → **15**（部门列表搜索改 `FilterInput`）。`NIcon` 5 → **4**。`NCard` 10 → **9**（菜单列表不再用）。`NTag` 仍是 **0**。`useMessage` 仍是 **21**。`NSpace` 仍是 **1**。仍有 **39** 个文件导入 naive-ui。Naive 包不删。

仍不换的，以及原因：

| 页面 | 原因 |
|------|------|
| `system/role/components/role-permission.vue` | 菜单列加权限列，父子关联是页面自己的规则；整表换成树会改保存结果 |
| `system/role/index.vue` | 左侧角色列表加右侧 Tab，用户分配表还有独立分页。不是一张树表 |
| `system/user/index.vue` | 左树和分割已换。右侧仍是手写分页、下拉操作和重置密码弹层 |
| `system/config/index.vue` | 配置分组用 `NSplit`，不是树表 |
| `code/generator/modules/gen-preview-modal.vue` | 预览弹层里的目录树，选中后要拼路径 |
| `demos/table/index.vue`、`demos/naive` | 组件陈列，这一波不改 |

## 目录选择

原子实现继续放在 `@vben-core/shadcn-ui/src/ui`（已有 reka-ui、CVA、`cn()`，颜色经 `frontend/src/styles/theme.css` 的 `--color-*` 接 HSL token）。  
应用侧只加路径入口，避免第二套 Button：

| 层 | 路径 | 导入 |
|----|------|------|
| 原子 | `frontend/src/ui/<atom>` | `import { Button } from '#/ui/button'` |
| 模式 | `frontend/src/ui-patterns/<pattern>` | `ToolbarActions`、`FilterInput`、`ConfirmAction` |

包增加子路径 `./ui/*`，新原子（Skeleton、Sonner）**不**进入 `@vben-core/shadcn-ui` 根桶，避免壳层整包把 `vue-sonner` 带进去。

第一波入口：`button`、`input`、`label`、`checkbox`、`switch`、`dialog`、`alert-dialog`、`badge`、`skeleton`、`sonner`。Phase 3 补了应用路径 `separator`。后来补了 `select`、`popover`、`radio-group`、`textarea`、`number-field`。日期和树这波补了 `date-picker`、`tree`（`VbenTree`）。用户页的左右分割用已有的 `ColPage`，没有再导出一套 Resizable。

## 可先换

页面上的轻控件，套件里已有对应原子。表单适配器里的同名控件不要在这一波动（见「后换」）。

| Naive | 文件数 | 换成 | 例子 |
|-------|--------|------|------|
| `NButton` | 1 | `#/ui/button`（含 `loading`） | 只剩 `views/demos/naive/index.vue` 的组件陈列 |
| `NSpace` | 1 | `flex` + `gap-*` 或 `ToolbarActions` | 只剩 `views/demos/naive/index.vue` 的组件陈列 |
| `NInput` | 15 | `#/ui/input` 或 `FilterInput` | 带前缀的搜索框仍是 Naive，如 `views/system/user/index.vue`。部门列表搜索已改 `FilterInput`。表单成对输入已是两个原子 Input |
| `NCard` | 9 | 已有 Card（`#/ui` 尚未再导出，需要时再加路径）。菜单列表已不用 `NCard` | `views/demos/naive/index.vue` |
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
| `useMessage` | 21 | Sonner 已挂到 `app.vue`；开放应用、系统选项、在线用户、公告、短信日志和文件列表已改 `toast`，其余调用点仍是 Naive | `views/schedule/job/index.vue` |
| `NTag` | 0 | 具名颜色已映射到 Badge。`primary` / `info` 用 Badge `default` | — |
| `NSelect` | 0 | 注册表 `Select`、`ApiSelect` 和页面手写下拉都已接 `FormSelect` | — |
| `NIcon` | 4 | 可换 Lucide，但图标选择器是一整块。菜单列表已不用 `NIcon` | `components/icon-select.vue` |
| `NForm` / `NFormItem` | 10 / 10 | 手写表单；目标是 `useVbenForm` 或后续原子表单 | `views/schedule/job/edit-drawer.vue` |
| `useDialog` | 0 | 页面上的布尔确认已改 `ConfirmAction` | — |
| `NDrawer` / `NDrawerContent` | 8 / 8 | 新页用 `useVbenDrawer` | `views/monitor/log/operation-log.vue` |
| `NPopconfirm` | 0 | 页面上的确认已改 `ConfirmAction` | — |
| `NRadioGroup` / `NRadio` | 7 / 5 | 表单 schema 的 `RadioGroup` 已接 `#/ui/radio-group`。页面上手写的单选还是 Naive | `views/system/menu/index.vue` |
| `NSwitch` | 0 | 页面和表单 schema 都走 `#/ui/switch`。`null` 显示为关；`1/0` 用 `checked-value` / `unchecked-value` | — |
| `NInputNumber` | 6 | 表单 schema 的 `InputNumber` 已接 NumberField，清空回写 `null`。页面上手写数字框还是 Naive | `views/schedule/job/edit-drawer.vue` |
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

`adapter/component/index.ts` 里，提交/重置按钮、Input（含文本域、密码、字数、成对输入）、Textarea、Checkbox、CheckboxGroup、Select、ApiSelect、RadioGroup、InputNumber、Switch、DatePicker、TreeSelect、ApiTreeSelect 已经换成原子控件，空值仍是 `null`（`adapter/form.ts`）。Select / Radio、树选择和日期清除后回写 `null`。日期没有 `valueFormat` 时是时间戳，有 `valueFormat` 时是格式化字符串。InputNumber 清空回写 `null`。Divider、Space、TimePicker、Upload 仍动态加载 Naive。

## 暂留

表、树、复杂选择器，以及还要托住上述调用的壳。Vxe Grid 继续当表格默认，不换 `NDataTable`。

| Naive | 文件数 | 例子 |
|-------|--------|------|
| `NDataTable` + `DataTableColumns` | 4 / 3 | 公告、三张日志、文件、部门、菜单和个人日志已改 Vxe。剩下权限矩阵、角色里的用户分配、用户列表和示例。例子：`views/system/role/components/role-permission.vue`、`views/system/user/index.vue`、`views/demos/table/index.vue` |
| `NDatePicker` | 0 | 表单和页面都改 `#/ui/date-picker`。周、月、季、快捷范围没有对应页面，也没有套件 Calendar |
| `NTreeSelect` | 0 | 表单 `TreeSelect` / `ApiTreeSelect` 和菜单、部门、用户编辑里的上级/部门都改 `FormTreeSelect` |
| `NSplit` | 2 | 用户页已改 `ColPage`。还留 `views/system/config/index.vue`、`views/system/role/index.vue` |
| `NTree` | 1 | 用户部门树已改 `VbenTree`。还留代码生成预览 `views/code/generator/modules/gen-preview-modal.vue` |
| `NUpload` + 上传类型 | 2 + 类型 | `views/system/file/index.vue`、`components/image-upload.vue` |
| `NCascader` | 1 | `views/system/file/index.vue` |
| `NTimePicker` | 1 | `views/schedule/job/edit-drawer.vue`（适配器里另有动态导入） |
| `NConfigProvider`、`NMessageProvider`、`NDialogProvider`、`NNotificationProvider`、主题与语言包 | `app.vue` | 调用点还在就留着 |
| `createDiscreteApi` | `adapter/naive.ts` | 设置页等在 setup 外发消息 |

适配器里的 `time-picker`、`upload` 仍动态加载 Naive。`date-picker` 和 `tree-select` 已换到上面的控件。

## 新代码

- 优先 `#/ui/<atom>` 与 `#/ui-patterns/<pattern>`。
- 新组件不要新增 `naive-ui` 导入，除非控件属于暂留类。
- 不要新增 `Foo` + `FooShadcn` 两套名字。壳上已有的 `VbenButton` 等封装留在壳里，业务页用原子 `Button`。
