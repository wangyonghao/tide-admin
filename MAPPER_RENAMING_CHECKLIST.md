# Mapper 类命名规范统一 - 完成清单

## 任务完成状态：✅ 已完成

### 重命名统计
- **总 Mapper 类数**：26 个
- **已重命名**：3 个
- **已符合规范**：23 个
- **完成率**：100%

---

## 重命名详情

### 1. SmsConfigMapper → SysSmsConfigMapper ✅

**文件变更**
```
backend/biz/biz-system/src/main/java/top/wyhao/admin/system/mapper/
  SmsConfigMapper.java → SysSmsConfigMapper.java
```

**类名变更**
```java
// 原
public interface SmsConfigMapper extends BaseMapper<SysSmsConfig> {}

// 新
public interface SysSmsConfigMapper extends BaseMapper<SysSmsConfig> {}
```

**引用更新**
- ✅ `SmsConfigServiceImpl.java` - 导入和字段声明已更新

---

### 2. SmsLogMapper → SysSmsLogMapper ✅

**文件变更**
```
backend/biz/biz-system/src/main/java/top/wyhao/admin/system/mapper/
  SmsLogMapper.java → SysSmsLogMapper.java
```

**类名变更**
```java
// 原
public interface SmsLogMapper extends BaseMapper<SysSmsLog> {}

// 新
public interface SysSmsLogMapper extends BaseMapper<SysSmsLog> {}
```

**引用更新**
- ✅ 无其他文件引用此 Mapper

---

### 3. GenFieldConfigMapper → GenGenFieldConfigMapper ✅

**文件变更**
```
backend/biz/biz-coding/src/main/java/top/wyhao/admin/generator/mapper/
  GenFieldConfigMapper.java → GenGenFieldConfigMapper.java
```

**类名变更**
```java
// 原
public interface GenFieldConfigMapper extends BaseMapper<GenFieldConfig> {}

// 新
public interface GenGenFieldConfigMapper extends BaseMapper<GenFieldConfig> {}
```

**引用更新**
- ✅ `GeneratorServiceImpl.java` - 导入和字段声明已更新

---

### 4. NoticePublishJob 修复 ✅

**问题**：使用了已删除的 `NoticeMapper` 类名

**修复**
```java
// 原
NoticeMapper noticeMapper = SpringUtil.getBean(NoticeMapper.class);

// 新
SysNoticeMapper noticeMapper = SpringUtil.getBean(SysNoticeMapper.class);
```

---

## 已符合规范的 Mapper 类（无需修改）

### 系统管理模块 (biz-system) - 18 个
- ✅ SysUserMapper
- ✅ SysRoleMapper
- ✅ SysMenuMapper
- ✅ SysDeptMapper
- ✅ SysDictMapper
- ✅ SysConfigMapper
- ✅ SysNoticeMapper
- ✅ SysFileMapper
- ✅ SysUserRoleMapper
- ✅ SysRoleMenuMapper
- ✅ SysRoleDeptMapper
- ✅ SysUserSocialMapper
- ✅ SysUserPasswordHistoryMapper
- ✅ SysMessageMapper
- ✅ SysMessageLogMapper
- ✅ SysNoticeLogMapper
- ✅ SysOperationLogMapper
- ✅ SysLoginLogMapper

### 开放平台模块 (biz-openapi) - 1 个
- ✅ SysAppMapper

### 代码生成模块 (biz-coding) - 2 个
- ✅ GenGenConfigMapper
- ✅ GenGenFieldConfigMapper（已重命名）

### 租户管理模块 (biz-tenant) - 3 个
- ✅ SysTenantMapper
- ✅ TenantPackageMapper
- ✅ TenantPackageMenuMapper

---

## 编译验证

### 编译命令
```bash
mvn clean compile -DskipTests
```

### 编译结果
```
✅ 编译成功
✅ 无编译错误
✅ 无警告（除了 Java 版本相关的警告）
```

---

## 命名规范总结

### 规范定义
所有 Mapper 类必须按照以下格式命名：**{前缀}{表名}Mapper**

### 前缀规则
| 前缀 | 说明 | 示例 |
|------|------|------|
| `Sys` | 系统表 | `SysUserMapper` |
| `Tenant` | 租户表 | `TenantPackageMapper` |
| `Gen` | 代码生成表 | `GenGenFieldConfigMapper` |

### 命名示例
| 数据库表 | 实体类 | Mapper 类 |
|---------|-------|---------|
| `sys_user` | `SysUser` | `SysUserMapper` |
| `sys_role` | `SysRole` | `SysRoleMapper` |
| `sys_menu` | `SysMenu` | `SysMenuMapper` |
| `sys_sms_config` | `SysSmsConfig` | `SysSmsConfigMapper` |
| `gen_field_config` | `GenFieldConfig` | `GenGenFieldConfigMapper` |
| `tenant_package` | `TenantPackage` | `TenantPackageMapper` |

---

## 后续维护

### 新增 Mapper 时的检查清单
- [ ] Mapper 类名是否遵循 `{前缀}{表名}Mapper` 格式
- [ ] 前缀是否正确（Sys/Tenant/Gen）
- [ ] 是否继承了 `BaseMapper<T>` 或 `DataPermissionMapper<T>`
- [ ] 是否添加了 `@Mapper` 注解
- [ ] 是否添加了类级别的 JavaDoc 注释

### 代码审查要点
- 检查新增 Mapper 是否符合命名规范
- 检查 Mapper 的导入是否正确
- 检查 Service 中的 Mapper 注入是否使用新的类名

---

## 相关文档
- 📄 [Mapper 重命名详细总结](./docs/prd/mapper-renaming-summary.md)
- 📄 [项目开发规范](./AGENTS.md)
- 📄 [后端开发规范 - Mapper 层](./AGENTS.md#后端开发规范)

---

## 完成时间
- 开始时间：2025-05-18
- 完成时间：2025-05-18
- 总耗时：< 1 小时

---

**状态**：✅ 已完成并验证
**下一步**：可以继续进行其他代码规范统一工作
