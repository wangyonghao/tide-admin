# 已废弃

`backend/scripts/docker/` 不再维护，后续会删除。不要在这里加服务，也不要把它当作本地开发入口。

本地基础设施和前端 Vite 改用 [`backend/docker/`](../../docker/README.md)：

```bash
cd backend/docker
cp .env.example .env
docker compose --env-file .env -f docker-compose.dev.yml up
```
