# 实体类重命名操作指南

## ⚠️ 重要提示

实体类重命名涉及大量文件（预计 100+ 个文件），**强烈建议使用 IDE 的重构功能**，而不是手动重命名。

## 🛠️ 推荐方法：使用 IntelliJ IDEA 重构

### 步骤 1: 准备工作

1. **创建新分支**
   ```bash
   git checkout -b refactor/entity-renaming
   ```

2. **确保项目可以正常编译**
   ```bash
   mvn clean compile
   ```

3. **运行所有测试确保通过**
   ```bash
   mvn test
   ```

### 步骤 2: 使用 IDEA 重构功能

#### 方法 A: 重命名类（推荐）

1. 在 IDEA 中打开实体类文件
2. 将光标放在类名上
3. 按 `Shift + F6`（或右键 → Refactor → Rename）
4. 输入新的类名
5. 勾选以下选项：
   - ✅ Search in comments and strings
   - ✅ Search for text occurrences
6. 点击 "Refactor"
7. 查看预览，确认所有变更
8. 点击 "Do Refactor"

#### 方法 B: 批量重命名

1. 使用 IDEA 的 "Structural Search and Replace"
2. Edit → Find → Replace Structurally
3. 配置搜索和替换模式

### 步骤 3: 验证变更

1. **编译项目**
   ```bash
   mvn clean compile
   ```

2. **运行测试**
   ```bash
   mvn test
   ```

3. **检查 Git 变更**
   ```bash
   git status
   git diff
   ```

4. **手动检查关键文件**
   - Mapper 接口
   - Service 类
   - Controller 类

### 步骤 4: 提交变更

```bash
git add .
git commit -m "refactor: 统一实体类命名规范为表名驼峰风格"
git push origin refactor/entity-renaming
```

## 📋 重命名清单

### biz-system 模块（14个）

#### 第一批：核心实体

```
ConfigDO → SysConfig
MenuDO → SysMenu
RoleDO → SysRole
DeptDO → SysDept
```

**操作步骤**:
1. 打开 `ConfigDO.java`
2. 光标放在 `ConfigDO` 类名上
3. `Shift + F6`
4. 输入 `SysConfig`
5. 确认并执行重构
6. 重复以上步骤处理其他类

#### 第二批：关联实体

```
RoleMenuDO → SysRoleMenu
RoleDeptDO → SysRoleDept
UserRoleDO → SysUserRole
```

#### 第三批：业务实体

```
MessageDO → SysMessage
NoticeDO → SysNotice
SettingsDO → SysSettings
SmsConfigDO → SysSmsConfig
```

#### 第四批：日志实体

```
LoginLogDO → SysLoginLog
MessageLogDO → SysMessageLog
NoticeLogDO → SysNoticeLog
SmsLogDO → SysSmsLog
```

### biz-tenant 模块（3个）

```
PackageDO → TenantPackage
TenantDO → Tenant
PackageMenuDO → TenantPackageMenu
```

### biz-coding 模块（2个）

```
GenConfigDO → GenConfig
FieldConfigDO → GenFieldConfig
```

### biz-openapi 模块（1个）

```
AppDO → SysApp
```

## 🔍 验证检查清单

### 编译检查
- [ ] 项目编译无错误
- [ ] 无导入错误
- [ ] 无类型错误

### 功能检查
- [ ] 所有 Mapper 接口正常
- [ ] 所有 Service 方法正常
- [ ] 所有 Controller 接口正常
- [ ] 数据库操作正常

### 测试检查
- [ ] 单元测试全部通过
- [ ] 集成测试全部通过
- [ ] 手动测试关键功能

### 文档检查
- [ ] API 文档生成正常
- [ ] Swagger UI 显示正常
- [ ] 类注释更新

## 🚨 常见问题

### Q1: 重命名后编译报错？

**A**: 检查以下几点：
1. 是否有手动导入的类没有更新
2. 是否有字符串中的类名引用
3. 是否有反射代码使用了类名
4. 是否有配置文件中的类名引用

### Q2: 如何处理 XML 文件中的引用？

**A**: MyBatis XML 文件中的 `resultType` 和 `parameterType` 需要手动更新：

```xml
<!-- 旧的 -->
<select id="selectById" resultType="top.wyhao.admin.system.entity.ConfigDO">
    ...
</select>

<!-- 新的 -->
<select id="selectById" resultType="top.wyhao.admin.system.entity.SysConfig">
    ...
</select>
```

### Q3: 如何处理前端类型定义？

**A**: 如果前端有对应的 TypeScript 类型定义，也需要同步更新：

```typescript
// 旧的
interface ConfigDO {
  id: number;
  configKey: string;
  // ...
}

// 新的
interface SysConfig {
  id: number;
  configKey: string;
  // ...
}
```

## 📊 影响范围分析

### 直接影响的文件类型

1. **实体类本身** (24个文件)
   - `*.java` 实体类文件

2. **Mapper 接口** (约24个文件)
   - `*Mapper.java`
   - `*Mapper.xml`

3. **Service 类** (约48个文件)
   - `*Service.java`
   - `*ServiceImpl.java`

4. **Controller 类** (约24个文件)
   - `*Controller.java`

5. **其他引用** (约50+个文件)
   - DTO 类
   - VO 类
   - 工具类
   - 测试类

**预计总影响文件数**: 150-200 个文件

### 间接影响

1. **API 文档**
   - Swagger/OpenAPI 文档自动更新

2. **数据库映射**
   - MyBatis 映射关系（通过 @TableName 注解，无需修改）

3. **前端代码**
   - 如果前端有类型定义，需要同步更新

## 💡 最佳实践

### 1. 分批次重命名

不要一次性重命名所有实体类，建议分批次进行：

- **第一批**: 核心实体（4个）
- **第二批**: 关联实体（3个）
- **第三批**: 业务实体（4个）
- **第四批**: 日志实体（4个）
- **第五批**: 其他模块（6个）

每批次完成后：
1. 编译测试
2. 提交代码
3. 休息一下 😊

### 2. 使用版本控制

每批次重命名作为一个独立的 commit：

```bash
git commit -m "refactor(entity): 重命名核心实体类 (ConfigDO → SysConfig, MenuDO → SysMenu, RoleDO → SysRole, DeptDO → SysDept)"
```

### 3. 保持团队沟通

- 在团队群中通知重命名计划
- 提醒团队成员拉取最新代码
- 解答团队成员的疑问

### 4. 更新文档

重命名完成后，更新以下文档：

- [ ] 项目 README
- [ ] 开发文档
- [ ] API 文档
- [ ] 数据库设计文档

## 🎯 示例：完整重命名流程

### 示例：重命名 ConfigDO → SysConfig

#### 1. 打开文件

```
backend/biz/biz-system/src/main/java/top/wyhao/admin/system/entity/ConfigDO.java
```

#### 2. 执行重构

- 光标放在 `ConfigDO` 类名上
- 按 `Shift + F6`
- 输入 `SysConfig`
- 勾选所有搜索选项
- 点击 "Refactor"

#### 3. 查看预览

IDEA 会显示所有将被修改的文件，例如：

```
✓ ConfigDO.java → SysConfig.java
✓ ConfigMapper.java (2 occurrences)
✓ ConfigService.java (3 occurrences)
✓ ConfigServiceImpl.java (5 occurrences)
✓ ConfigController.java (4 occurrences)
✓ ... (更多文件)
```

#### 4. 确认并执行

点击 "Do Refactor" 按钮

#### 5. 验证

```bash
# 编译
mvn clean compile

# 测试
mvn test

# 查看变更
git diff
```

#### 6. 提交

```bash
git add .
git commit -m "refactor(entity): 重命名 ConfigDO 为 SysConfig"
```

## 📅 建议时间安排

- **第一批** (核心实体): 1-2 小时
- **第二批** (关联实体): 1 小时
- **第三批** (业务实体): 1-2 小时
- **第四批** (日志实体): 1 小时
- **第五批** (其他模块): 1-2 小时
- **测试验证**: 2-3 小时
- **文档更新**: 1 小时

**总计**: 约 1-2 个工作日

## 🎉 完成后的收益

1. **命名更规范** - 符合表名驼峰命名约定
2. **代码更清晰** - 类名直接对应表名
3. **维护更容易** - 统一的命名风格
4. **团队协作更顺畅** - 减少命名混淆

---

**文档版本**: v1.0  
**创建日期**: 2024-01-01  
**最后更新**: 2024-01-01  
**维护者**: WYH Admin 团队
