# ContiNew Admin - 开发规范和约定

> 本文档为 AI Agent 提供项目开发规范和约定说明。

## 后端开发规范

### 代码规范

#### 命名规范
- **类名**：大驼峰（PascalCase），如 `UserController`、`UserService`
- **方法名**：小驼峰（camelCase），如 `getUserById`、`saveUser`
- **常量**：全大写下划线分隔，如 `MAX_SIZE`、`DEFAULT_PAGE_SIZE`
- **包名**：全小写，如 `top.wyhao.system.user`

#### 包结构规范
```
top.wyhao.system.user/
├── controller/       # 控制器层（API 接口）
├── service/          # 业务逻辑层
│   └── impl/         # 业务逻辑实现
├── mapper/           # 数据访问层（MyBatis Mapper）
├── model/            # 数据模型
│   ├── entity/       # 实体类（对应数据库表）
│   ├── query/        # 查询条件类
│   ├── req/          # 请求参数类
│   └── resp/         # 响应参数类
├── enums/            # 枚举类
├── constant/         # 常量类
└── config/           # 配置类
```

#### 注解使用规范
- **Controller 层**：使用 `@RestController`、`@RequestMapping`
- **Service 层**：使用 `@Service`
- **Mapper 层**：使用 `@Mapper`
- **实体类**：使用 `@Data`（Lombok）、`@TableName`（MyBatis Plus）
- **API 文档**：使用 `@Tag`、`@Operation`（Swagger）

### API 设计规范

#### RESTful API 规范
- **GET**：查询资源，如 `GET /api/users/{id}`
- **POST**：创建资源，如 `POST /api/users`
- **PUT**：更新资源（全量），如 `PUT /api/users/{id}`
- **PATCH**：更新资源（部分），如 `PATCH /api/users/{id}`
- **DELETE**：删除资源，如 `DELETE /api/users/{id}`

#### 统一响应格式
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {},
  "timestamp": 1234567890
}
```

#### 分页响应格式
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "records": [],
    "total": 100,
    "size": 10,
    "current": 1,
    "pages": 10
  }
}
```

### 数据库规范

#### 表命名规范
- 使用小写字母和下划线，如 `sys_user`、`sys_role`
- 表名使用复数形式（可选）
- 系统表使用 `sys_` 前缀

#### 字段命名规范
- 使用小写字母和下划线，如 `user_name`、`create_time`
- 主键统一使用 `id`
- 创建时间：`create_time`
- 更新时间：`update_time`
- 创建人：`create_user`
- 更新人：`update_user`
- 逻辑删除：`deleted`（0-未删除，1-已删除）

#### 索引规范
- 主键索引：`pk_表名`
- 唯一索引：`uk_表名_字段名`
- 普通索引：`idx_表名_字段名`

### 异常处理规范

#### 自定义异常
- 业务异常：`BusinessException`
- 参数异常：`ParamException`
- 认证异常：`AuthException`

#### 全局异常处理
- 使用 `@RestControllerAdvice` 统一处理异常
- 返回统一的错误响应格式

### 日志规范

#### 日志级别
- **ERROR**：错误信息，需要立即处理
- **WARN**：警告信息，可能存在问题
- **INFO**：重要信息，如业务流程关键节点
- **DEBUG**：调试信息，开发时使用

#### 日志内容
- 记录关键业务操作（登录、修改、删除等）
- 记录异常信息（包含堆栈信息）
- 记录性能信息（慢查询、慢接口等）

### 事务管理规范

#### 事务注解
- 使用 `@Transactional` 注解
- 只在 Service 层使用事务
- 指定事务传播行为和隔离级别

#### 事务粒度
- 事务粒度尽量小
- 避免在事务中调用外部接口
- 避免在事务中执行耗时操作

---

## 前端开发规范

### 代码规范

#### 命名规范
- **组件名**：大驼峰（PascalCase），如 `UserList.vue`、`UserForm.vue`
- **文件名**：kebab-case，如 `user-list.vue`、`user-form.vue`
- **变量名**：小驼峰（camelCase），如 `userName`、`userList`
- **常量名**：全大写下划线分隔，如 `MAX_SIZE`、`API_BASE_URL`
- **CSS 类名**：kebab-case，如 `.user-list`、`.user-form`

#### 目录结构规范
```
src/
├── api/              # API 接口定义
│   └── modules/      # 按模块组织
├── views/            # 页面视图
│   ├── _core/        # 核心页面（登录、个人中心等）
│   └── system/       # 系统管理页面
├── components/       # 组件
│   ├── common/       # 通用组件
│   └── business/     # 业务组件
├── router/           # 路由配置
├── store/            # 状态管理
├── utils/            # 工具函数
├── types/            # 类型定义
├── constants/        # 常量定义
├── hooks/            # 组合式函数
└── styles/           # 样式文件
```

### Vue 3 规范

#### 组合式 API（Composition API）
- 优先使用 Composition API
- 使用 `<script setup>` 语法糖
- 使用 `ref` 和 `reactive` 管理响应式数据

#### 组件规范
```vue
<script setup lang="ts">
// 1. 导入依赖
import { ref, computed, onMounted } from 'vue'

// 2. 定义 Props
interface Props {
  userId: string
}
const props = defineProps<Props>()

// 3. 定义 Emits
interface Emits {
  (e: 'update', value: string): void
}
const emit = defineEmits<Emits>()

// 4. 定义响应式数据
const userName = ref('')

// 5. 定义计算属性
const displayName = computed(() => userName.value.toUpperCase())

// 6. 定义方法
const handleUpdate = () => {
  emit('update', userName.value)
}

// 7. 生命周期钩子
onMounted(() => {
  // 初始化逻辑
})
</script>

<template>
  <div class="user-info">
    <p>{{ displayName }}</p>
    <button @click="handleUpdate">更新</button>
  </div>
</template>

<style scoped lang="scss">
.user-info {
  padding: 16px;
}
</style>
```

### TypeScript 规范

#### 类型定义
- 优先使用 `interface` 定义对象类型
- 使用 `type` 定义联合类型、交叉类型等
- 避免使用 `any`，使用 `unknown` 替代

#### 类型文件组织
```typescript
// types/user.ts
export interface User {
  id: string
  name: string
  email: string
}

export interface UserQuery {
  name?: string
  page: number
  size: number
}

export interface UserListResponse {
  records: User[]
  total: number
}
```

### API 调用规范

#### API 文件组织
```typescript
// api/modules/user.ts
import { request } from '@/utils/request'
import type { User, UserQuery, UserListResponse } from '@/types/user'

export const userApi = {
  // 获取用户列表
  getList: (params: UserQuery) => {
    return request.get<UserListResponse>('/api/users', { params })
  },
  
  // 获取用户详情
  getById: (id: string) => {
    return request.get<User>(`/api/users/${id}`)
  },
  
  // 创建用户
  create: (data: Omit<User, 'id'>) => {
    return request.post<User>('/api/users', data)
  },
  
  // 更新用户
  update: (id: string, data: Partial<User>) => {
    return request.put<User>(`/api/users/${id}`, data)
  },
  
  // 删除用户
  delete: (id: string) => {
    return request.delete(`/api/users/${id}`)
  }
}
```

### 路由规范

#### 路由配置
```typescript
// router/modules/system.ts
import type { RouteRecordRaw } from 'vue-router'

export const systemRoutes: RouteRecordRaw[] = [
  {
    path: '/system',
    name: 'System',
    component: () => import('@/layouts/default.vue'),
    meta: {
      title: '系统管理',
      icon: 'system'
    },
    children: [
      {
        path: 'user',
        name: 'SystemUser',
        component: () => import('@/views/system/user/index.vue'),
        meta: {
          title: '用户管理',
          icon: 'user'
        }
      }
    ]
  }
]
```

### 状态管理规范

#### Pinia Store
```typescript
// store/modules/user.ts
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { User } from '@/types/user'

export const useUserStore = defineStore('user', () => {
  // State
  const userInfo = ref<User | null>(null)
  const token = ref<string>('')
  
  // Getters
  const isLoggedIn = computed(() => !!token.value)
  
  // Actions
  const setUserInfo = (user: User) => {
    userInfo.value = user
  }
  
  const setToken = (newToken: string) => {
    token.value = newToken
  }
  
  const logout = () => {
    userInfo.value = null
    token.value = ''
  }
  
  return {
    userInfo,
    token,
    isLoggedIn,
    setUserInfo,
    setToken,
    logout
  }
}, {
  persist: true // 持久化
})
```

### 样式规范

#### Tailwind CSS 使用
- 优先使用 Tailwind 原子类
- 复杂样式使用 `@apply` 组合
- 自定义样式使用 `<style scoped>`

#### 样式组织
```vue
<template>
  <div class="user-card">
    <h3 class="text-lg font-bold">{{ user.name }}</h3>
    <p class="text-sm text-gray-600">{{ user.email }}</p>
  </div>
</template>

<style scoped lang="scss">
.user-card {
  @apply p-4 rounded-lg shadow-md;
  
  &:hover {
    @apply shadow-lg;
  }
}
</style>
```

---

## Git 规范

### 分支管理

#### 分支命名
- **主分支**：`main` 或 `master`
- **开发分支**：`develop`
- **功能分支**：`feature/功能名称`，如 `feature/user-management`
- **修复分支**：`fix/问题描述`，如 `fix/login-error`
- **发布分支**：`release/版本号`，如 `release/1.0.0`

### 提交规范

#### Commit Message 格式
```
<type>(<scope>): <subject>

<body>

<footer>
```

#### Type 类型
- **feat**：新功能
- **fix**：修复 Bug
- **docs**：文档更新
- **style**：代码格式调整（不影响功能）
- **refactor**：重构（不是新功能，也不是修复 Bug）
- **perf**：性能优化
- **test**：测试相关
- **chore**：构建过程或辅助工具的变动

#### 示例
```
feat(user): 添加用户管理功能

- 添加用户列表页面
- 添加用户新增/编辑表单
- 添加用户删除功能

Closes #123
```

---

## 代码审查规范

### 审查要点

#### 后端
- 代码是否符合规范
- 是否有潜在的性能问题
- 是否有安全漏洞
- 异常处理是否完善
- 日志记录是否合理
- 单元测试是否完善

#### 前端
- 代码是否符合规范
- 组件是否可复用
- 是否有性能问题（如不必要的重渲染）
- 类型定义是否完善
- 是否有无障碍问题
- 是否有浏览器兼容性问题

---

## 测试规范

### 后端测试

#### 单元测试
- 使用 JUnit 5
- 测试覆盖率 > 80%
- 测试类命名：`XxxTest`
- 测试方法命名：`testXxx` 或 `shouldXxxWhenYyy`

#### 集成测试
- 使用 Spring Boot Test
- 测试关键业务流程
- 测试数据库操作

### 前端测试

#### 单元测试
- 使用 Vitest
- 测试工具函数
- 测试组合式函数（Composables）

#### 组件测试
- 使用 @vue/test-utils
- 测试组件渲染
- 测试用户交互

#### E2E 测试
- 使用 Playwright
- 测试关键业务流程
- 测试跨浏览器兼容性

---

## 性能优化规范

### 后端优化
- 使用缓存减少数据库查询
- 使用分页避免一次性加载大量数据
- 使用异步处理耗时操作
- 优化 SQL 查询（避免 N+1 问题）
- 使用连接池管理数据库连接

### 前端优化
- 使用懒加载（路由、组件、图片）
- 使用虚拟滚动处理大列表
- 使用防抖和节流优化事件处理
- 使用 Web Worker 处理计算密集型任务
- 优化打包体积（Tree Shaking、代码分割）

---

## 安全规范

### 后端安全
- 使用参数化查询防止 SQL 注入
- 使用 HTTPS 传输敏感数据
- 使用 JWT 或 Session 管理用户认证
- 使用 RBAC 实现权限控制
- 敏感数据加密存储
- 接口限流防止恶意攻击

### 前端安全
- 防止 XSS 攻击（使用 Vue 的自动转义）
- 防止 CSRF 攻击（使用 Token）
- 敏感数据不存储在 LocalStorage
- 使用 HTTPS
- 验证用户输入

---

## 文档规范

### 代码注释
- 类和方法必须有注释
- 复杂逻辑必须有注释
- 使用 JavaDoc（后端）或 JSDoc（前端）格式

### API 文档
- 使用 Swagger 注解生成 API 文档
- 包含请求参数、响应格式、错误码说明

### README 文档
- 项目介绍
- 技术栈
- 快速开始
- 目录结构
- 开发指南
- 部署指南

---

## 总结

- **代码规范**：统一的命名、注释、格式规范
- **API 规范**：RESTful 设计，统一响应格式
- **Git 规范**：规范的分支管理和提交信息
- **测试规范**：完善的单元测试和集成测试
- **安全规范**：防止常见的安全漏洞
- **文档规范**：完善的代码注释和项目文档
