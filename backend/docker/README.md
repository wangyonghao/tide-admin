# 本地开发 Compose

Postgres、Redis、Inbucket、MinIO，以及 `@tide/admin` 的 Vite 热更新。后端 API 不在这组容器里。

`backend/scripts/docker/` 已废弃，不要再往那里加服务。

## 启动

```bash
cd backend/docker
cp .env.example .env
# 编辑 .env，替换 POSTGRES_PASSWORD、REDIS_PWD、MINIO_ROOT_PASSWORD
# openssl rand -hex 24

docker compose --env-file .env -f docker-compose.dev.yml up
```

停掉并清掉卷（空库、空桶、前端依赖卷）：

```bash
docker compose --env-file .env -f docker-compose.dev.yml down -v
```

只 `stop` 再 `up` 会沿用当前数据卷。Postgres 使用匿名卷，数据不进仓库。

另开一个终端起后端（在仓库的 `backend/`）：

```bash
mvn -pl tide-bootstrap -am spring-boot:run
```

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
| web | 5888 | Vite → http://localhost:5888 |
| （宿主机） | 8000 | Spring Boot，不在本 compose 中 |

`VITE_PORT` 只改前端的宿主机端口，容器内 Vite 仍监听 5888。

## 宿主机后端怎么连这些容器

`application-dev.yml` 从环境变量读库和 Redis。把 `.env` 里的密码填进去：

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
```

数据库名是 `postgres`，不是 `wyhao_admin`。

邮件（`sys_config.mail`）：host `127.0.0.1`，port `2500`，关闭认证。

文件存储默认仍是本地磁盘。若要改走 MinIO（`file.storage.type=MINIO`）：

```yaml
file:
  storage:
    type: MINIO
  minio:
    endpoint: http://127.0.0.1:9002
    access-key: <MINIO_ROOT_USER>
    secret-key: <MINIO_ROOT_PASSWORD>
    bucket: <MINIO_BUCKET>
```

## 前端为什么能访问宿主机 API

页面请求基址是 `/api`。Vite 在容器内把 `/api` 代理到 `VITE_GLOB_API_URL`（默认 `http://host.docker.internal:8000`）。`extra_hosts` 把 `host.docker.internal` 指到宿主机。浏览器只访问 `localhost:5888`。

源码目录绑定挂载 `frontend/`，保存即走 Vite HMR。根目录 `node_modules` 在匿名卷里；pnpm 仍可能在各 workspace 包下写入 `node_modules`（已被 gitignore）。宿主机和容器不要同时对同一份 `frontend/` 跑 `pnpm install`。
