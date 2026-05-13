# 字典管理

## 功能说明

字典管理模块用于管理系统中的字典数据，支持字典项的增删改查操作。

## 页面结构

### 主页面 (index.vue)

- **查询表单**：支持按字典类型、字典值、显示名称、状态进行筛选
- **数据表格**：展示字典列表，包含字典类型、字典值、显示名称、状态、排序等信息
- **操作按钮**：
  - 新建字典项：打开编辑抽屉创建新字典
  - 编辑：修改已有字典项
  - 删除：删除字典项（需确认）
  - 清除缓存：清除指定字典类型的缓存

### 编辑抽屉 (components/DictEditDrawer.vue)

支持新建和编辑字典项，包含以下字段：

- **字典类型**：2-30个字符，支持大小写字母、数字、下划线，以字母开头（编辑时不可修改）
- **字典值**：最多255个字符
- **字典标签**：最多255个字符，用于显示
- **排序**：数字类型，用于排序
- **状态**：启用/禁用开关
- **描述**：最多500个字符，可选

## API 接口

### 后端接口

- `GET /system/dict/page` - 分页查询字典列表
- `POST /system/dict` - 新增字典
- `PUT /system/dict/{id}` - 修改字典
- `DELETE /system/dict` - 批量删除字典
- `DELETE /system/dict/cache/{dictType}` - 清除字典缓存

### 前端 API

```typescript
import { dictApi } from '#/api/system';

// 分页查询
dictApi.page(query);

// 新增
dictApi.create(data);

// 修改
dictApi.update(id, data);

// 删除
dictApi.delete([id1, id2]);

// 清除缓存
dictApi.clearCache(dictType);
```

## 数据模型

### DictResult（响应）

```typescript
interface DictResult {
  id: number;              // ID
  dictType: string;        // 字典类型
  value: string;           // 字典值
  label: string;           // 字典标签
  ext?: Record<string, any>; // 扩展信息
  sort: number;            // 排序
  enabled: boolean;        // 是否启用
  description?: string;    // 描述
}
```

### DictRequest（请求）

```typescript
interface DictRequest {
  dictType: string;        // 字典类型（必填）
  value: string;           // 字典值（必填）
  label: string;           // 字典标签（必填）
  extra?: Record<string, any>; // 扩展信息
  sort?: number;           // 排序
  enabled?: boolean;       // 是否启用
  description?: string;    // 描述
}
```

### DictQuery（查询）

```typescript
interface DictQuery {
  description?: string;    // 关键词（搜索字典类型、标签、值、描述）
  dictType?: string;       // 字典类型
}
```

## 权限控制

需要以下权限：

- `system:dict:page` - 查询字典列表
- `system:dict:create` - 新增字典
- `system:dict:update` - 修改字典
- `system:dict:delete` - 删除字典
- `system:dict:clearCache` - 清除缓存

## 使用示例

### 常见字典类型

```typescript
// 公告类型
{
  dictType: 'notice_type',
  value: '1',
  label: '产品新闻',
  sort: 1,
  enabled: true
}

// 用户状态
{
  dictType: 'user_status',
  value: '1',
  label: '启用',
  sort: 1,
  enabled: true
}

// 性别
{
  dictType: 'gender',
  value: '1',
  label: '男',
  sort: 1,
  enabled: true
}
```

## 注意事项

1. **字典类型命名规范**：使用小写字母和下划线，如 `notice_type`、`user_status`
2. **字典值唯一性**：同一字典类型下的字典值必须唯一
3. **缓存机制**：修改或删除字典后会自动清除对应类型的缓存
4. **编辑限制**：编辑时不能修改字典类型，避免数据不一致

## 待优化

1. ✅ 后端添加字典详情接口（`GET /system/dict/{id}`）
2. ✅ 支持扩展信息（ext）的可视化编辑
3. ✅ 支持批量导入导出
4. ✅ 支持字典类型分组管理
