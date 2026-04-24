# ContiNew Admin - 技术栈说明

> 本文档为 AI Agent 提供项目技术栈的详细说明。

## 后端技术栈

### 核心框架
- **Spring Boot 3.4.13** - 应用框架
- **Spring Cloud 2024.0.2** - 微服务支持
- **Java 17** - 编程语言

### 数据访问
- **MyBatis Plus 3.5.14** - ORM 框架，提供 CRUD 增强
- **Redisson 3.52.0** - Redis 客户端
- **JetCache 2.7.8** - 缓存框架（支持两级缓存）
- **P6Spy 3.9.1** - SQL 性能分析

### 认证授权
- **Sa-Token 1.44.0** - 权限认证框架
- **JustAuth 1.16.7** - 第三方登录集成

### 任务调度
- **SnailJob 1.8.0** - 分布式任务调度平台

### 验证码
- **AJ-Captcha 1.4.0** - 行为验证码（滑动拼图）
- **Easy Captcha 1.6.2** - 图形验证码

### 文件处理
- **FastExcel 1.3.0** - Excel 处理（高性能）
- **Apache POI 5.4.1** - Office 文档处理
- **X-File-Storage 2.2.1** - 文件存储（支持本地、S3 等）
- **Thumbnailator 0.4.21** - 图片缩略图

### API 文档
- **NextDoc4j 1.1.7** - 现代化 API 文档 UI（替代 Swagger UI）
- **Swagger Annotations 2.2.36** - API 注解

### 工具库
- **Hutool 5.8.41** - Java 工具类库
- **Lombok** - 简化 JavaBean 编写
- **CosId 2.13.3** - 分布式 ID 生成器
- **TLog 1.5.2** - 分布式日志追踪
- **Ip2region 3.4.7** - IP 地址定位
- **OkHttp 4.12.0** - HTTP 客户端

### 数据库
- **Liquibase** - 数据库版本管理
- **MySQL** - 关系型数据库
- **Redis** - 缓存数据库

### 构建工具
- **Maven 3.x** - 项目构建管理
- **flatten-maven-plugin** - 版本号管理

---

## 前端技术栈

### 核心框架
- **Vue 3.5.32** - 渐进式 JavaScript 框架
- **TypeScript 6.0.2** - 类型安全的 JavaScript 超集
- **Vite 8.0.8** - 下一代前端构建工具

### UI 框架
- **Naive UI 2.44.1** - Vue 3 组件库（主应用使用）
- **Ant Design Vue 4.2.6** - 企业级 UI 组件库
- **Element Plus 2.13.7** - Vue 3 组件库
- **TDesign Vue Next 1.19.0** - 腾讯 UI 组件库

### 路由和状态管理
- **Vue Router 5.0.4** - 官方路由管理器
- **Pinia 3.0.4** - 官方状态管理库
- **pinia-plugin-persistedstate 4.7.1** - Pinia 持久化插件

### 工具库
- **VueUse 14.2.1** - Vue 组合式 API 工具集
- **Axios 1.15.0** - HTTP 客户端
- **Day.js 1.11.20** - 日期处理库
- **Lodash** - JavaScript 工具库
- **es-toolkit 1.45.1** - 现代 JavaScript 工具库

### 表单和验证
- **Vee-Validate 4.15.1** - 表单验证库
- **Zod 3.25.76** - TypeScript 优先的模式验证

### 图表和可视化
- **ECharts 6.0.0** - 数据可视化图表库

### 表格
- **VxeTable 4.18.11** - Vue 表格组件
- **VxePC UI 4.13.21** - VxeTable 配套 UI

### 富文本编辑器
- **TipTap 3.22.3** - 无头富文本编辑器

### 样式和 CSS
- **Tailwind CSS 4.2.2** - 原子化 CSS 框架
- **Sass 1.99.0** - CSS 预处理器
- **PostCSS 8.5.9** - CSS 转换工具
- **tailwind-merge 3.5.0** - Tailwind 类名合并
- **class-variance-authority 0.7.1** - CSS 变体管理

### 图标
- **Iconify** - 统一的图标框架
- **Lucide Vue Next 0.577.0** - 图标库

### 国际化
- **Vue I18n 11.3.2** - 国际化插件

### 代码质量
- **ESLint 10.2.0** - JavaScript 代码检查
- **Stylelint 17.6.0** - CSS 代码检查
- **oxfmt 0.44.0** - 代码格式化工具
- **oxlint 1.60.0** - 快速 Linter
- **Commitlint** - Git 提交信息规范检查
- **Lefthook 2.1.5** - Git hooks 管理

### 构建和部署
- **pnpm 10.33.0** - 快速、节省磁盘空间的包管理器
- **Turbo 2.9.6** - 高性能构建系统（Monorepo）
- **Vite Plugin PWA** - PWA 支持
- **Vite Plugin Compression** - 构建压缩
- **Rollup Plugin Visualizer** - 构建分析

### 测试
- **Vitest 4.1.4** - 单元测试框架
- **Playwright 1.59.1** - E2E 测试框架
- **@vue/test-utils 2.4.6** - Vue 组件测试工具

### 开发工具
- **Vue DevTools** - Vue 开发者工具
- **Vite Plugin Vue DevTools 8.1.1** - Vite 集成的 Vue DevTools

### 其他功能库
- **@tanstack/vue-query 5.97.0** - 数据获取和缓存
- **nprogress 0.2.0** - 页面加载进度条
- **qrcode 1.5.4** - 二维码生成
- **sortablejs 1.15.7** - 拖拽排序
- **watermark-js-plus 1.6.3** - 水印
- **secure-ls 2.0.0** - 本地存储加密
- **json-bigint 1.0.0** - 大整数 JSON 处理

---

## 架构特点

### 后端架构
1. **模块化设计** - Maven 多模块架构，按功能拆分
2. **依赖管理** - 使用 BOM 统一版本管理
3. **CRUD 套件** - 基于 MyBatis Plus 的增强 CRUD
4. **多级缓存** - JetCache 支持本地 + Redis 两级缓存
5. **插件化** - 业务功能以插件形式组织（租户、任务调度、代码生成等）

### 前端架构
1. **Monorepo** - pnpm workspace + Turbo 管理多包
2. **组件化** - 共享组件库 + 应用层分离
3. **类型安全** - 全面使用 TypeScript
4. **原子化 CSS** - Tailwind CSS 提高开发效率
5. **多 UI 框架支持** - 支持 Naive UI、Ant Design、Element Plus 等

---

## 开发规范

### 后端规范
- 遵循阿里巴巴 Java 开发手册
- 使用 Lombok 简化代码
- 统一异常处理
- RESTful API 设计
- 接口文档自动生成

### 前端规范
- ESLint + Stylelint 代码检查
- Commitlint 提交信息规范
- 组件命名规范（PascalCase）
- 文件命名规范（kebab-case）
- 使用 Composition API

---

## 版本要求

### 后端
- Java 17+
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+

### 前端
- Node.js 20.19.0+ / 22.18.0+ / 24.0.0+
- pnpm 10.0.0+

---

## 关键技术决策

### 为什么选择 MyBatis Plus？
- 提供强大的 CRUD 增强，减少样板代码
- 支持 Lambda 表达式查询，类型安全
- 内置分页、性能分析等插件

### 为什么选择 Sa-Token？
- 轻量级，学习成本低
- 功能完善（登录认证、权限验证、单点登录等）
- 与 Spring Boot 集成简单

### 为什么选择 Naive UI？
- 完整的 TypeScript 支持
- 组件丰富，设计现代
- 性能优秀，体积小

### 为什么选择 pnpm + Turbo？
- pnpm 节省磁盘空间，安装速度快
- Turbo 提供增量构建，大幅提升构建速度
- 适合 Monorepo 架构

### 为什么选择 Vite？
- 开发服务器启动快
- HMR（热模块替换）速度快
- 原生 ESM 支持
- 构建产物优化好

---

## 总结

- **后端**：基于 Spring Boot 3 + MyBatis Plus 的现代化 Java 技术栈
- **前端**：基于 Vue 3 + TypeScript + Vite 的现代化前端技术栈
- **架构**：前后端分离，模块化设计，易于扩展和维护
- **工具链**：完善的开发工具链，提高开发效率和代码质量
