# 本地开发 Compose

一条命令启动 Postgres、Redis、Inbucket、MinIO、Spring Boot API，以及 `@tide/admin` 的 Vite 热更新。

旧路径 `backend/scripts/docker/` 已删除；本地入口仅本目录。

## 前置条件

| 项 | 说明 |
|----|------|
| JDK **17** | 宿主机跑 Maven / IDE 时必须。先 `export JAVA_HOME="$(/usr/libexec/java_home -v 17)"`（或 Homebrew `openjdk@17`）。默认若是 JDK 26 会踩坑。 |
| `.env` | `cp .env.example .env` 后改掉所有占位密码、`AES_KEY`、`WYH_JWT_SECRET`。`.env` 已 gitignore，勿提交。 |
| 中间件 | Postgres + Redis 必须可达（Apple Container、本机安装、或其它）。 |
| Docker Compose | **可选**（全栈一键 / CI）。本机无 Docker **不挡**启动验收；勿把未跑的 compose 标成已绿。 |

生成口令示例：`openssl rand -hex 24`。`AES_KEY` 须为 16/24/32 字节（可用 `openssl rand -hex 16`）。

## Docker Compose（可选 / CI）

```bash
cd backend/docker
cp .env.example .env
# 编辑 .env，替换 POSTGRES_PASSWORD、REDIS_PWD、MINIO_ROOT_PASSWORD、AES_KEY
# openssl rand -hex 24
# AES_KEY 必须是 16、24 或 32 字节

docker compose --env-file .env -f docker-compose.dev.yml up
```

浏览器打开 http://localhost:5888 。API 监听 http://localhost:8000 （`/doc.html`）。

第一次启动会构建 `tide-dev-api` 镜像（只含 Temurin 17 JDK 和 Maven），然后在容器里下载依赖并编译。Maven 仓库在卷 `api_m2`，之后会快一些。改 Java 源码不用重建镜像。改了 `backend/docker/api/` 里的 Dockerfile 或入口脚本后，加上 `--build`。

停掉并清掉卷（空库、空桶、前端依赖卷、Maven 仓库、本地文件卷）：

```bash
docker compose --env-file .env -f docker-compose.dev.yml down -v
```

只 `stop` 再 `up` 会沿用当前数据卷。Postgres 使用匿名卷，数据不进仓库。


## 宿主机启动（推荐）+ Apple Container

本机若**没有** `docker` / Compose 插件，不要执行上面的 `docker compose ... up`。可用 [Apple Container](https://github.com/apple/container)（`container` CLI）只跑中间件，API 与前端在宿主机启动。

当前 Apple Container **没有**与本仓库 `docker-compose.dev.yml` 对等的一键 compose 插件时，按服务分别起容器即可。最小可启动后端所需：**Postgres 17** + **Redis**（邮件 / MinIO 可按需再加）。

示例（口令自行替换；端口与 `.env.example` 默认一致）：

```bash
# 先：container system start（若 apiserver 未运行）

container run -d --name postgres \
  -p 5432:5432 \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD='<POSTGRES_PASSWORD>' \
  -e POSTGRES_DB=postgres \
  -e TZ=Asia/Shanghai \
  docker.io/library/postgres:17

container run -d --name redis \
  -p 6379:6379 \
  docker.io/library/redis:7-alpine \
  redis-server --appendonly yes --requirepass '<REDIS_PWD>'
```

然后准备宿主机环境并编译 / 启动：

```bash
cd <repo>/backend/docker && cp .env.example .env   # 仍建议保留，便于对照变量
cd <repo>
cp backend/scripts/env.example.sh backend/scripts/env.sh
# 编辑 env.sh：DB_PWD / REDIS_PWD 与上面容器口令一致；AES_KEY、WYH_JWT_SECRET 自备
set -a && source backend/scripts/env.sh && set +a
java -version   # 必须是 17.x

cd backend
mvn -pl tide-bootstrap -am compile
mvn -pl tide-bootstrap -am spring-boot:run
```

验收：

```bash
curl -s http://127.0.0.1:8000/actuator/health
# 期望含 "status":"UP"
```

说明：

- **本机验收**以「中间件 + 宿主机 API + `/actuator/health=UP`」为准；Compose 全栈仅可选。
- 已有同名容器时用 `container list` / `container start <name>`，不要重复 `run`。
- 前端：`cd frontend && pnpm install && pnpm dev`（API 在 `http://127.0.0.1:8000`）。

## 端口

| 服务 | 宿主机 | 用途 |
|------|--------|------|
| db | 5432 | Postgres。库名 `postgres`，用户 `postgres` |
| redis | 6379 | Redis，口令为 `REDIS_PWD` |
| mail | 2500 | SMTP（不外发） |
| mail | 9000 | Inbucket Web → http://localhost:9000 |
| mail | 1100 | POP3 |
| minio | 9002 | S3 API（容器内 9000；9000 已被 Inbucket 占用） |
| minio | 9001 | MinIO Console → http://localhost:9001 |
| api | 8000 | Spring Boot → http://localhost:8000 |
| web | 5888 | Vite → http://localhost:5888 |

`API_PORT` / `VITE_PORT` 只改宿主机端口。容器内 API 仍监听 8000，Vite 仍监听 5888。

## 前端怎么访问 API

页面请求基址是 `/api`。Vite 在 `web` 容器内把 `/api` 代理到 `VITE_GLOB_API_URL`（默认 `http://api:8000`）。`api` 是 compose 网络里的服务名。浏览器只访问 `localhost:5888`，不必解析这个主机名。

`frontend/apps/admin/.env.development` 里的 `http://127.0.0.1:8000` 给宿主机 `pnpm dev` 用。容器进程环境变量优先于这份文件。

源码目录绑定挂载 `frontend/`，保存即走 Vite HMR。根目录 `node_modules` 在匿名卷里；pnpm 仍可能在各 workspace 包下写入 `node_modules`（已被 gitignore）。宿主机和容器不要同时对同一份 `frontend/` 跑 `pnpm install`。

## API 怎么连库、Redis 和 MinIO

同一份 `.env`。容器网络里的地址：

| 变量 | 默认 | 说明 |
|------|------|------|
| `DB_HOST` / `DB_PORT` | `db` / `5432` | 容器端口，不是 `POSTGRES_PORT` |
| `DB_NAME` / `DB_USER` | `postgres` | 与 `POSTGRES_DB` / `POSTGRES_USER` 一致 |
| `DB_PWD` | `POSTGRES_PASSWORD` | 不单独再写一份 |
| `REDIS_HOST` | `redis` | 容器内端口固定 6379 |
| `REDIS_PWD` / `REDIS_DB` | 见 `.env` | `REDIS_DB` 默认 15 |
| `AES_KEY` | 必填 | 16、24 或 32 字节 |
| `WYH_JWT_SECRET` | 必填 | `sa-token.jwt-secret-key`，开发环境也不再写死 |
| `FILE_STORAGE_TYPE` | `MINIO` | 端点 `http://minio:9000` |
| `FILE_MINIO_*` | 跟 `MINIO_ROOT_*`、`MINIO_BUCKET` | 可在 `.env` 覆盖 |

`api` 会等 Postgres、Redis 健康检查通过，以及 `minio-init` 建好桶之后再启动。`application-dev.yml` 里 Liquibase 已开启，compose 再设 `SPRING_LIQUIBASE_ENABLED=true`，空库会跑 changelog。

改成 `FILE_STORAGE_TYPE=LOCAL` 时，文件写到卷 `api_files`（容器内 `/data/files`），不再使用 `application-dev.yml` 里的本机路径。

邮件种子数据是 `127.0.0.1:2500`，那是给跑在宿主机上的 API 用的。API 在容器里时，把系统配置里的邮件 host 改成 `mail`，端口仍是 2500。Inbucket 页面仍是 http://localhost:9000 。

## 改后端代码

`backend/` 挂在容器 `/src`，入口是 `mvn -pl tide-bootstrap -am spring-boot:run`（profile `docker-dev`，带 Spring Boot DevTools）。

- `API_HOT_RELOAD=true`（默认）：`src/main` 变更后，容器再执行一次 `mvn compile`，DevTools 重启 JVM。这是多模块编译，不是 IDE 的增量编译，一次可能要几十秒到几分钟。
- `API_HOT_RELOAD=false`：不监听文件。改完后执行 `docker compose --env-file .env -f docker-compose.dev.yml restart api`。依赖仍在 `api_m2`，不必重新下载。
- 改了 `pom.xml` 或依赖后需要 `restart api`。热更新那次编译带 `-o`，不会拉新依赖。

容器默认以 root 运行，`backend/**/target` 可能变成 root 所有。和宿主机 Maven 混用这些目录时，注意权限。日志写在容器 `/tmp/logs`，不进仓库。

没有把可运行 jar 打进镜像。第一次 `up` 要在容器里编译；换来的是改代码不用 `docker compose build`。

## 只在宿主机跑 API

不要同时占用 8000。只起依赖和前端：

```bash
docker compose --env-file .env -f docker-compose.dev.yml up db redis mail minio minio-init web
```

把 `.env` 里的 `VITE_GLOB_API_URL` 改成 `http://host.docker.internal:8000`，然后在仓库的 `backend/`：

```bash
export JAVA_HOME="$(/usr/libexec/java_home -v 17)"
export PATH="$JAVA_HOME/bin:$PATH"
mvn -pl tide-bootstrap -am spring-boot:run
```

宿主机环境变量见 `backend/scripts/env.example.sh`（`DB_HOST=localhost` 等）。`DB_PWD`、`REDIS_PWD`、`AES_KEY`、`WYH_JWT_SECRET` 要和这份 `.env` 一致，否则连不上，或者解不开已加密字段、签不出登录令牌。

```bash
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=postgres
export DB_USER=postgres
export DB_PWD='<POSTGRES_PASSWORD>'

export REDIS_HOST=127.0.0.1
export REDIS_PORT=6379
export REDIS_PWD='<REDIS_PWD>'
export REDIS_DB=15
export WYH_JWT_SECRET='<WYH_JWT_SECRET>'
```

数据库名是 `postgres`，不是 `wyhao_admin`。宿主机上的邮件 host 用 `127.0.0.1`，端口 `2500`。文件存储在宿主机上仍是 `application-dev.yml` 的本地路径，除非自己改成 MinIO（端点 `http://127.0.0.1:9002`）。

## 健康检查

Actuator 已在 `tide-bootstrap` 里。`application.yml` 只暴露 `health` 和 `info`，`metrics` 没有打开。

未登录可以访问（`web.security.excludes`）：

- `GET http://localhost:8000/actuator/health`：存活状态。`show-details` 是 `never`，响应里没有库和磁盘明细。另有 `/actuator/health/liveness`、`/actuator/health/readiness`。
- `GET http://localhost:8000/actuator/info`：默认几乎是空对象。没有打开 env 信息。

Compose 里 `api` 的健康检查仍是探测 8000 端口是否能连上，没有改成打 `/actuator/health`。
