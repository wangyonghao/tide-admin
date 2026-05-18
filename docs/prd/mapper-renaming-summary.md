# Mapper 类命名规范统一总结

## 概述
本文档记录了将所有 Mapper 类统一命名为 **表名+Mapper** 格式的重命名工作。

## 重命名规则
- **系统表 Mapper**：`Sys{TableName}Mapper`（如 `SysUserMapper`、`SysRoleMapper`）
- **租户表 Mapper**：`Tenant{TableName}Mapper`（如 `TenantPackageMapper`）
- **代码生成表 Mapper**：`Gen{TableName}Mapper`（如 `GenGenFieldConfigMapper`）

## 重命名清单

### 已重命名的 Mapper 类

| 原名称 | 新名称 | 所在模块 | 对应实体 |
|--------|--------|---------|---------|
| `SmsConfigMapper` | `SysSmsConfigMapper` | biz-system | `SysSmsConfig` |
| `SmsLogMapper` | `SysSmsLogMapper` | biz-system | `SysSmsLog` |
| `GenFieldConfigMapper` | `GenGenFieldConfigMapper` | biz-coding | `GenFieldConfig` |

### 已符合规范的 Mapper 类

以下 Mapper 类已符合命名规范，无需修改：

**系统管理模块 (biz-system)**
- `SysUserMapper` → `SysUser`
- `SysRoleMapper` → `SysRole`
- `SysMenuMapper` → `SysMenu`
- `SysDeptMapper` → `SysDept`
- `SysDictMapper` → `SysDict`
- `SysConfigMapper` → `SysConfig`
- `SysNoticeMapper` → `SysNotice`
- `SysFileMapper` → `SysFile`
- `SysUserRoleMapper` → `SysUserRole`
- `SysRoleMenuMapper` → `SysRoleMenu`
- `SysRoleDeptMapper` → `SysRoleDept`
- `SysUserSocialMapper` → `SysUserSocial`
- `SysUserPasswordHistoryMapper` → `SysUserPasswordHistory`
- `SysMessageMapper` → `SysMessage`
- `SysMessageLogMapper` → `SysMessageLog`
- `SysNoticeLogMapper` → `SysNoticeLog`
- `SysOperationLogMapper` → `SysOperationLog`
- `SysLoginLogMapper` → `SysLoginLog`

**开放平台模块 (biz-openapi)**
- `SysAppMapper` → `SysApp`

**代码生成模块 (biz-coding)**
- `GenGenConfigMapper` → `GenConfig`

**租户管理模块 (biz-tenant)**
- `SysTenantMapper` → `Tenant`
- `TenantPackageMapper` → `TenantPackage`
- `TenantPackageMenuMapper` → `TenantPackageMenu`

## 更新的文件

### 1. Mapper 文件重命名
```
backend/biz/biz-system/src/main/java/top/wyhao/admin/system/mapper/
  SmsConfigMapper.java → SysSmsConfigMapper.java
  SmsLogMapper.java → SysSmsLogMapper.java

backend/biz/biz-coding/src/main/java/top/wyhao/admin/generator/mapper/
  GenFieldConfigMapper.java → GenGenFieldConfigMapper.java
```

### 2. 类名更新
- `SysSmsConfigMapper.java`：接口名从 `SmsConfigMapper` 改为 `SysSmsConfigMapper`
- `SysSmsLogMapper.java`：接口名从 `SmsLogMapper` 改为 `SysSmsLogMapper`
- `GenGenFieldConfigMapper.java`：接口名从 `GenFieldConfigMapper` 改为 `GenGenFieldConfigMapper`

### 3. 导入和引用更新

**SmsConfigServiceImpl.java**
```java
// 导入更新
import top.wyhao.admin.system.mapper.SysSmsConfigMapper;

// 字段声明更新
private final SysSmsConfigMapper baseMapper;
```

**GeneratorServiceImpl.java**
```java
// 导入更新
import top.wyhao.admin.generator.mapper.GenGenFieldConfigMapper;

// 字段声明更新
private final GenGenFieldConfigMapper fieldConfigMapper;
```

**NoticePublishJob.java**
```java
// 导入更新
import top.wyhao.admin.system.mapper.SysNoticeMapper;

// 使用更新
SysNoticeMapper noticeMapper = SpringUtil.getBean(SysNoticeMapper.class);
```

## 验证结果

✅ 所有 Mapper 类已按照规范重命名
✅ 所有引用已更新
✅ 后端项目编译成功（`mvn clean compile -DskipTests`）

## 命名规范说明

### 为什么采用 "表名+Mapper" 的命名规范？

1. **一致性**：所有 Mapper 类名与对应的数据库表名保持一致
2. **可读性**：开发者能够快速识别 Mapper 对应的数据表
3. **可维护性**：减少命名混乱，便于代码审查和维护
4. **规范性**：符合 MyBatis Plus 的最佳实践

### 命名示例

| 数据库表名 | 实体类名 | Mapper 类名 |
|-----------|---------|-----------|
| `sys_user` | `SysUser` | `SysUserMapper` |
| `sys_role` | `SysRole` | `SysRoleMapper` |
| `sys_menu` | `SysMenu` | `SysMenuMapper` |
| `gen_field_config` | `GenFieldConfig` | `GenGenFieldConfigMapper` |
| `tenant_package` | `TenantPackage` | `TenantPackageMapper` |

## 后续维护建议

1. **新增 Mapper 时**：严格按照 "表名+Mapper" 的规范命名
2. **代码审查**：检查新增 Mapper 是否符合命名规范
3. **文档更新**：在项目开发规范文档中明确说明 Mapper 命名规范

## 相关文档

- [项目开发规范](../AGENTS.md) - 后端开发规范部分
- [MyBatis Plus 官方文档](https://baomidou.com/)
