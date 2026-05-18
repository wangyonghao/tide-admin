# 实体类命名统一计划

## 📋 命名规范

**规则**: 实体类名 = 表名的驼峰命名（PascalCase）

**示例**:
- `sys_config` → `SysConfig`
- `sys_user` → `SysUser`
- `sys_role_menu` → `SysRoleMenu`

## 🔄 需要重命名的实体类

### biz-system 模块

| 当前类名 | 表名 | 新类名 | 状态 |
|---------|------|--------|------|
| `ConfigDO` | `sys_config` | `SysConfig` | ⏳ 待处理 |
| `DeptDO` | `sys_dept` | `SysDept` | ⏳ 待处理 |
| `LoginLogDO` | `sys_login_log` | `SysLoginLog` | ⏳ 待处理 |
| `MenuDO` | `sys_menu` | `SysMenu` | ⏳ 待处理 |
| `MessageDO` | `sys_message` | `SysMessage` | ⏳ 待处理 |
| `MessageLogDO` | `sys_message_log` | `SysMessageLog` | ⏳ 待处理 |
| `NoticeDO` | `sys_notice` | `SysNotice` | ⏳ 待处理 |
| `NoticeLogDO` | `sys_notice_log` | `SysNoticeLog` | ⏳ 待处理 |
| `RoleDeptDO` | `sys_role_dept` | `SysRoleDept` | ⏳ 待处理 |
| `RoleDO` | `sys_role` | `SysRole` | ⏳ 待处理 |
| `RoleMenuDO` | `sys_role_menu` | `SysRoleMenu` | ⏳ 待处理 |
| `SettingsDO` | `sys_settings` | `SysSettings` | ⏳ 待处理 |
| `SmsConfigDO` | `sys_sms_config` | `SysSmsConfig` | ⏳ 待处理 |
| `SmsLogDO` | `sys_sms_log` | `SysSmsLog` | ⏳ 待处理 |
| `SysDict` | `sys_dict` | `SysDict` | ✅ 已符合 |
| `SysFile` | `sys_file` | `SysFile` | ✅ 已符合 |
| `SysOperationLog` | `sys_operation_log` | `SysOperationLog` | ✅ 已符合 |
| `UserRoleDO` | `sys_user_role` | `SysUserRole` | ⏳ 待处理 |

### biz-tenant 模块

| 当前类名 | 表名 | 新类名 | 状态 |
|---------|------|--------|------|
| `PackageDO` | `tenant_package` | `TenantPackage` | ⏳ 待处理 |
| `TenantDO` | `tenant` | `Tenant` | ⏳ 待处理 |
| `PackageMenuDO` | `tenant_package_menu` | `TenantPackageMenu` | ⏳ 待处理 |

### biz-coding 模块

| 当前类名 | 表名 | 新类名 | 状态 |
|---------|------|--------|------|
| `GenConfigDO` | `gen_config` | `GenConfig` | ⏳ 待处理 |
| `FieldConfigDO` | `gen_field_config` | `GenFieldConfig` | ⏳ 待处理 |

### biz-openapi 模块

| 当前类名 | 表名 | 新类名 | 状态 |
|---------|------|--------|------|
| `AppDO` | `sys_app` | `SysApp` | ⏳ 待处理 |

## 📊 统计

- **总计**: 24 个实体类
- **需要重命名**: 21 个
- **已符合规范**: 3 个

## ⚠️ 影响范围

### 直接影响
- Mapper 接口
- Service 类
- Controller 类
- 所有引用这些实体类的代码

### 间接影响
- API 文档
- 前端类型定义（如果有）

## 🎯 重命名策略

由于影响范围较大，建议分批次进行：

### 第一批：核心实体（高优先级）
- `ConfigDO` → `SysConfig`
- `MenuDO` → `SysMenu`
- `RoleDO` → `SysRole`
- `DeptDO` → `SysDept`

### 第二批：关联实体（中优先级）
- `RoleMenuDO` → `SysRoleMenu`
- `RoleDeptDO` → `SysRoleDept`
- `UserRoleDO` → `SysUserRole`

### 第三批：日志实体（低优先级）
- `LoginLogDO` → `SysLoginLog`
- `MessageLogDO` → `SysMessageLog`
- `NoticeLogDO` → `SysNoticeLog`
- `SmsLogDO` → `SysSmsLog`

### 第四批：其他实体
- 剩余所有实体类

## 📝 注意事项

1. **使用 IDE 重构功能**
   - 使用 IntelliJ IDEA 的 Rename 功能
   - 自动更新所有引用

2. **测试验证**
   - 每批重命名后进行编译测试
   - 运行单元测试确保功能正常

3. **版本控制**
   - 每批重命名作为一个独立的 commit
   - 便于回滚和追踪

4. **文档更新**
   - 更新相关技术文档
   - 更新 API 文档

## 🚀 执行步骤

### 步骤 1: 准备工作
- [ ] 创建新分支
- [ ] 备份当前代码
- [ ] 确保所有测试通过

### 步骤 2: 执行重命名
- [ ] 使用 IDE 的 Rename 功能
- [ ] 逐个重命名实体类
- [ ] 检查自动更新的引用

### 步骤 3: 验证
- [ ] 编译项目
- [ ] 运行单元测试
- [ ] 运行集成测试
- [ ] 手动测试关键功能

### 步骤 4: 提交
- [ ] 提交代码
- [ ] 更新文档
- [ ] 通知团队

---

**创建日期**: 2024-01-01  
**状态**: 计划中  
**优先级**: 中
