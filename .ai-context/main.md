# ContiNew Admin - 项目目录结构说明

> 本文档为 AI Agent 提供项目目录结构的详细说明，帮助快速理解项目组织方式。

## 项目整体结构

```
项目根目录/
├── backend/          # 后端 Java 项目（Spring Boot 3 + Java 17）
├── frontend/         # 前端 Vue 3 项目（Monorepo 架构）
├── docs/             # 文档项目（VitePress）
├── .ai-context/      # AI Agent 上下文文档
├── .image/           # 项目截图和图片资源
└── README.md         # 项目说明文档
```

---

## 后端目录结构 (backend/)

### 根目录文件
```
backend/
├── pom.xml           # Maven 根配置文件（版本管理、依赖管理）
├── lombok.config     # Lombok 全局配置
├── .gitignore        # Git 忽略配置
└── LICENSE           # 开源协议文件
```

### 主要模块

#### 1. 服务模块 (svr-*)
```
backend/svr-admin/    # 主服务模块（打包部署）
├── src/main/
│   ├── java/top/wyhao/admin/
│   │   ├── config/           # 配置类
│   │   ├── controller/       # 通用 API
│   │   ├── job/              # 定时任务
│   │   └── AdminApplication.java  # 启动类
│   └── resources/
│       ├── config/           # 配置文件目录
│       │   ├── application.yml
│       │   ├── application-dev.yml
│       │   └── application-prod.yml
│       ├── db/changelog/     # Liquibase 数据库脚本
│       ├── templates/        # 模板文件（邮件等）
│       └── logback-spring.xml
└── pom.xml

backend/svr-job/      # 任务调度服务模块
└── src/main/
    ├── java/top/wyhao/job/
    │   └── JobServerApplication.java
    └── resources/
```

#### 2. 业务模块 (biz/)
```
backend/biz/
├── biz-system/       # 系统管理核心模块
│   └── src/main/java/top/wyhao/system/
│       ├── auth/             # 认证相关
│       │   ├── controller/   # 登录、登出等 API
│       │   ├── service/      # 认证业务逻辑
│       │   ├── model/        # 认证相关模型
│       │   └── config/       # 认证配置
│       └── system/           # 系统管理
│           ├── controller/   # 用户、角色、菜单等 API
│           ├── service/      # 业务逻辑
│           ├── mapper/       # MyBatis Mapper
│           ├── model/        # 数据模型
│           │   ├── entity/   # 实体类
│           │   ├── query/    # 查询条件
│           │   ├── req/      # 请求参数
│           │   └── resp/     # 响应参数
│           ├── enums/        # 枚举
│           ├── constant/     # 常量
│           └── config/       # 配置
│
├── biz-coding/       # 代码生成器插件
│   └── src/main/
│       ├── java/top/wyhao/generator/
│       └── resources/templates/  # 代码生成模板
│           ├── backend/          # 后端模板
│           └── frontend/         # 前端模板
│
├── biz-openapi/      # 能力开放插件（第三方应用接入）
│   └── src/main/java/top/wyhao/open/
│       ├── controller/       # 应用管理 API
│       ├── service/          # 业务逻辑
│       ├── model/            # 数据模型
│       └── sign/             # API 签名算法
│
├── biz-tenant/       # 多租户插件
│   └── src/main/java/top/wyhao/tenant/
│       ├── api/              # 租户公共 API
│       ├── controller/       # 租户管理 API
│       ├── service/          # 业务逻辑
│       └── model/            # 数据模型
│
├── biz-job/          # 任务调度插件
│   └── src/main/java/top/wyhao/schedule/
│       ├── controller/       # 任务管理 API
│       ├── service/          # 业务逻辑
│       ├── api/              # Feign API
│       └── model/            # 数据模型
│
└── pom.xml           # 业务模块父 POM
```

#### 3. 公共模块 (cmn/)
```
backend/cmn/
├── biz-base/         # 业务基础模块（所有业务模块的通用能力）
│   └── src/main/java/top/wyhao/common/
│       ├── api/              # 公共业务 API
│       ├── base/             # 基类
│       │   ├── controller/   # Controller 基类
│       │   ├── service/      # Service 基类
│       │   ├── mapper/       # Mapper 基类
│       │   └── model/        # 模型基类
│       ├── model/            # 公共模型
│       ├── context/          # 上下文
│       ├── enums/            # 公共枚举
│       ├── constant/         # 公共常量
│       ├── util/             # 工具类
│       └── config/           # 公共配置
│           ├── crud/         # CRUD 配置
│           ├── mybatis/      # MyBatis 配置
│           ├── websocket/    # WebSocket 配置
│           ├── doc/          # 接口文档配置
│           └── exception/    # 全局异常处理
│
├── cmn-base/         # ContiNew Starter 核心模块
├── cmn-web/          # Web 模块（跨域、异常处理等）
├── cmn-json/         # JSON 处理模块（Jackson）
├── cmn-apidoc/       # API 文档模块（NextDoc4j）
│
├── cmn-security/     # 认证模块（SaToken）
├── cmn-justauth/     # 第三方登录模块（JustAuth）
│
├── cmn-mybatis-plus/ # MyBatis Plus 集成
├── cmn-redis/        # Redisson 集成
├── cmn-springcache/  # Spring Cache 集成
├── cmn-jetcache/     # JetCache 集成
│
├── cmn-captcha-graphic/    # 图形验证码
├── cmn-captcha-behavior/   # 行为验证码（滑动拼图）
│
├── cmn-encrypt-core/       # 加密核心模块
├── cmn-encrypt-field/      # 字段加密
├── cmn-encrypt-api/        # API 加密
│
├── cmn-fastexcel/    # FastExcel 集成
├── cmn-poi/          # Apache POI 集成
├── cmn-storage/      # 文件存储（本地、S3 等）
│
├── cmn-trace/        # 链路追踪（TLog）
├── cmn-mail/         # 邮件发送
├── cmn-websocket/    # WebSocket 支持
├── cmn-tenant/       # 多租户支持
├── cmn-license/      # License 管理
│   ├── cmn-generator/      # License 生成器
│   ├── cmn-license-core/   # License 核心
│   └── cmn-license-verifier/  # License 校验器
│
└── pom.xml           # 公共模块父 POM
```

#### 4. 脚本和配置
```
backend/scripts/
├── docker/           # Docker 部署配置
│   ├── docker-compose.yml   # Docker Compose 配置
│   ├── apiserver/           # API 服务配置
│   ├── jobserver/           # 任务服务配置
│   ├── nginx/               # Nginx 配置
│   └── redis/               # Redis 配置
├── package-and-build-images.sh  # 打包构建脚本
└── tag-and-push-images.sh       # 镜像推送脚本

backend/.style/       # 代码规范配置
├── Java开发手册(黄山版).pdf
├── license-header    # License 文件头
└── p3c-codestyle.xml # 阿里巴巴代码规范

backend/.github/      # GitHub 配置
├── workflows/        # GitHub Actions 工作流
│   ├── build.yml     # 构建工作流
│   ├── deploy.yml    # 部署工作流
│   ├── release-tag.yml  # 发布工作流
│   └── scan.yml      # 代码扫描工作流
└── ISSUE_TEMPLATE/   # Issue 模板
```

---

## 前端目录结构 (frontend/)

### 根目录配置文件
```
frontend/
├── package.json      # 根 package.json（Monorepo 配置）
├── pnpm-workspace.yaml  # pnpm workspace 配置
├── turbo.json        # Turbo 构建配置
├── tsconfig.json     # TypeScript 配置
├── vite.config.ts    # Vite 配置
├── vitest.config.ts  # Vitest 测试配置
│
├── eslint.config.mjs # ESLint 配置
├── stylelint.config.mjs  # Stylelint 配置
├── oxfmt.config.ts   # oxfmt 格式化配置
├── oxlint.config.ts  # oxlint 配置
├── lefthook.yml      # Git hooks 配置
├── .commitlintrc.js  # Commit 规范配置
│
├── .npmrc            # npm 配置
├── .node-version     # Node 版本要求
├── .browserslistrc   # 浏览器兼容配置
├── .editorconfig     # 编辑器配置
└── README.md         # 前端说明文档
```

### 应用目录 (apps/)
```
frontend/apps/
└── web-naive/        # Naive UI 版本的主应用
    ├── src/
    │   ├── api/              # API 接口定义
    │   │   └── modules/      # 按模块组织的 API
    │   ├── views/            # 页面视图
    │   │   ├── _core/        # 核心页面（登录、个人中心等）
    │   │   ├── dashboard/    # 仪表盘
    │   │   ├── system/       # 系统管理
    │   │   ├── monitor/      # 系统监控
    │   │   ├── schedule/     # 任务调度
    │   │   ├── tenant/       # 租户管理
    │   │   ├── open/         # 能力开放
    │   │   ├── code/         # 代码生成器
    │   │   ├── user/         # 用户中心
    │   │   └── demos/        # 示例页面
    │   ├── router/           # 路由配置
    │   ├── store/            # 状态管理（Pinia）
    │   ├── components/       # 组件
    │   ├── layouts/          # 布局
    │   ├── locales/          # 国际化
    │   ├── utils/            # 工具函数
    │   ├── types/            # 类型定义
    │   ├── adapter/          # 适配器
    │   ├── constants/        # 常量
    │   ├── hooks/            # Hooks
    │   ├── app.vue           # 根组件
    │   ├── main.ts           # 入口文件
    │   ├── bootstrap.ts      # 启动配置
    │   └── preferences.ts    # 偏好设置
    ├── public/               # 静态资源
    │   └── favicon.ico
    ├── .env                  # 环境变量
    ├── .env.development      # 开发环境变量
    ├── .env.production       # 生产环境变量
    ├── .env.analyze          # 分析环境变量
    ├── index.html            # HTML 入口
    ├── vite.config.ts        # Vite 配置
    ├── tsconfig.json         # TypeScript 配置
    └── package.json          # 应用 package.json
```

### 共享包目录 (packages/)
```
frontend/packages/
├── @core/            # 核心包
│   ├── base/                 # 基础功能
│   │   ├── design/           # 设计系统
│   │   ├── icons/            # 图标
│   │   ├── shared/           # 共享工具
│   │   └── typings/          # 类型定义
│   ├── ui-kit/               # UI 组件套件
│   │   ├── form-ui/          # 表单组件
│   │   ├── menu-ui/          # 菜单组件
│   │   ├── tabs-ui/          # 标签页组件
│   │   └── shadcn-ui/        # Shadcn UI 组件
│   ├── composables/          # 组合式函数
│   ├── preferences/          # 偏好设置
│   └── forward/              # 转发组件
│
├── effects/          # 副作用相关
│   ├── access/               # 权限控制
│   ├── common-ui/            # 通用 UI
│   ├── hooks/                # Hooks
│   ├── layouts/              # 布局
│   ├── plugins/              # 插件
│   └── request/              # 请求封装
│
├── constants/        # 常量定义
├── icons/            # 图标库
├── locales/          # 国际化资源
├── preferences/      # 偏好设置
├── stores/           # 共享状态（Pinia）
├── styles/           # 样式文件
├── types/            # 类型定义
└── utils/            # 工具函数
```

### 内部工具 (internal/)
```
frontend/internal/
├── lint-configs/     # 代码检查配置
│   ├── commitlint-config/    # Commitlint 配置
│   ├── eslint-config/        # ESLint 配置
│   ├── oxfmt-config/         # oxfmt 配置
│   ├── oxlint-config/        # oxlint 配置
│   └── stylelint-config/     # Stylelint 配置
│
├── node-utils/       # Node 工具
├── tailwind-config/  # Tailwind 配置
├── tsconfig/         # TypeScript 配置
│   ├── base.json             # 基础配置
│   ├── library.json          # 库配置
│   ├── node.json             # Node 配置
│   ├── web.json              # Web 配置
│   └── web-app.json          # Web 应用配置
│
└── vite-config/      # Vite 配置
```

### 脚本工具 (scripts/)
```
frontend/scripts/
├── deploy/           # 部署脚本
│   ├── Dockerfile            # Docker 镜像配置
│   ├── nginx.conf            # Nginx 配置
│   └── build-local-docker-image.sh
│
├── turbo-run/        # Turbo 运行脚本
├── vsh/              # Shell 脚本工具
└── clean.mjs         # 清理脚本
```

### GitHub 配置
```
frontend/.github/
├── workflows/        # GitHub Actions 工作流
│   ├── build.yml             # 构建工作流
│   ├── ci.yml                # CI 工作流
│   ├── deploy.yml            # 部署工作流
│   ├── codeql.yml            # 代码扫描
│   ├── changeset-version.yml # 版本管理
│   └── ...
├── ISSUE_TEMPLATE/   # Issue 模板
│   ├── bug-report.yml
│   ├── feature-request.yml
│   └── docs.yml
└── actions/          # 自定义 Actions
    └── setup-node/
```

---

## 文档目录 (docs/)

```
docs/
├── src/              # 文档源文件
│   ├── guide/                # 指南
│   ├── api/                  # API 文档
│   └── ...
├── .vitepress/       # VitePress 配置
│   ├── config.ts             # 配置文件
│   └── theme/                # 主题配置
├── package.json      # 文档项目配置
└── tsconfig.json     # TypeScript 配置
```

---

## AI Context 目录 (.ai-context/)

```
.ai-context/
├── main.md           # 主要项目说明（本文件）
├── tech.md           # 技术栈详细说明
└── rules.md          # 开发规范和约定
```

---

## 关键目录说明

### 后端关键目录

1. **svr-admin/src/main/resources/config/**
   - 存放所有环境的配置文件
   - 开发时主要修改 `application-dev.yml`

2. **biz-system/src/main/java/top/wyhao/system/**
   - 系统管理核心业务代码
   - 包含用户、角色、菜单、部门等管理功能

3. **cmn/biz-base/src/main/java/top/wyhao/common/**
   - 所有业务模块的基类和公共工具
   - CRUD 套件的核心实现

4. **biz-coding/src/main/resources/templates/**
   - 代码生成器模板
   - 修改模板可自定义生成代码风格

### 前端关键目录

1. **apps/web-naive/src/views/**
   - 所有页面组件
   - 按业务模块组织（system、monitor、schedule 等）

2. **apps/web-naive/src/api/**
   - API 接口定义
   - 与后端 API 一一对应

3. **packages/@core/**
   - 核心共享包
   - 包含基础组件、UI 套件、工具函数等

4. **packages/effects/**
   - 副作用相关功能
   - 权限控制、请求封装、布局等

5. **internal/lint-configs/**
   - 代码规范配置
   - ESLint、Stylelint、Commitlint 等

---

## 模块依赖关系

### 后端模块依赖层次
```
svr-admin (主服务)
    ↓ 依赖
biz-* (业务模块)
    ↓ 依赖
biz-base (业务基础)
    ↓ 依赖
cmn-* (公共模块)
```

### 前端包依赖层次
```
apps/web-naive (应用)
    ↓ 依赖
packages/effects (副作用)
    ↓ 依赖
packages/@core (核心包)
    ↓ 依赖
packages/utils, packages/types (工具和类型)
```

---

## 开发时常用目录

### 后端开发
- **新增业务功能**: `backend/biz/biz-system/src/main/java/top/wyhao/system/`
- **修改配置**: `backend/svr-admin/src/main/resources/config/`
- **数据库脚本**: `backend/svr-admin/src/main/resources/db/changelog/`
- **代码生成模板**: `backend/biz/biz-coding/src/main/resources/templates/`

### 前端开发
- **新增页面**: `frontend/apps/web-naive/src/views/`
- **新增 API**: `frontend/apps/web-naive/src/api/modules/`
- **修改路由**: `frontend/apps/web-naive/src/router/`
- **修改状态**: `frontend/apps/web-naive/src/store/`
- **环境配置**: `frontend/apps/web-naive/.env.development`

---

## 注意事项

### 后端
1. 不要破坏模块依赖层次（svr → biz → cmn）
2. 新增依赖必须在根 pom.xml 的 `<dependencyManagement>` 中声明
3. 公共功能应放在 `cmn/biz-base` 模块
4. 业务功能应放在对应的 `biz-*` 模块

### 前端
1. 不要直接修改 `packages/@core` 核心包
2. 新增依赖应在 `pnpm-workspace.yaml` 的 `catalog` 中声明
3. 共享组件应放在 `packages/` 目录
4. 应用特定代码应放在 `apps/web-naive/` 目录

---

## 快速定位文件

### 后端
- **启动类**: `backend/svr-admin/src/main/java/top/wyhao/admin/AdminApplication.java`
- **配置文件**: `backend/svr-admin/src/main/resources/config/application-dev.yml`
- **用户管理**: `backend/biz/biz-system/src/main/java/top/wyhao/system/user/`
- **角色管理**: `backend/biz/biz-system/src/main/java/top/wyhao/system/role/`
- **菜单管理**: `backend/biz/biz-system/src/main/java/top/wyhao/system/menu/`

### 前端
- **入口文件**: `frontend/apps/web-naive/src/main.ts`
- **路由配置**: `frontend/apps/web-naive/src/router/`
- **登录页面**: `frontend/apps/web-naive/src/views/_core/authentication/`
- **用户管理页**: `frontend/apps/web-naive/src/views/system/user/`
- **API 配置**: `frontend/apps/web-naive/src/api/modules/`

---

## 总结

- **后端**: 采用模块化 Maven 多模块架构，按功能拆分为服务模块、业务模块和公共模块
- **前端**: 采用 pnpm workspace + Turbo 的 Monorepo 架构，按应用和共享包组织
- **依赖管理**: 后端使用 Maven BOM，前端使用 pnpm catalog
- **代码规范**: 后端遵循阿里巴巴 Java 规范，前端使用 ESLint + Stylelint + oxfmt
- **构建工具**: 后端使用 Maven，前端使用 Vite + Turbo

此目录结构设计清晰、模块化程度高，便于团队协作和项目维护。
