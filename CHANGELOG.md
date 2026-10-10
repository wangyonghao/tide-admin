# Changelog

本文件记录 Tide Admin 对用户与集成方有意义的变更。

格式基于 Keep a Changelog，版本遵循 Semantic Versioning。

## [Unreleased]

### Added

- 前端可按路径使用 shadcn-vue 原子（按钮、输入、对话框、徽标、骨架屏、Sonner 等）；Naive UI 仍保留，表格继续用 Vxe。
- 列表筛选可使用无前缀的 `FilterInput`（清除后为空字符串）。

### Changed

- 开放应用列表工具栏的「新建 / 导出」改为原子按钮；行内操作仍走 Naive。
- 开放应用列表的成功/失败提示改为 Sonner；确认仍走 Naive 对话框。
- 部门、通知、用户、菜单、文件、定时任务、日志等列表的工具栏按钮改为原子按钮。
