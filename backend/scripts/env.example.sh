#!/usr/bin/env bash
# Tide Admin — 宿主机后端环境变量示例
#
# 与 backend/tide-bootstrap/src/main/resources/config 里的 ${VAR} 对应。
# 本地 Postgres / Redis 等由 backend/docker 的 Compose 提供，口令以该目录 .env 为准。
#
#   cp backend/scripts/env.example.sh backend/scripts/env.sh
#   # 编辑 env.sh：DB_PWD、REDIS_PWD 与 backend/docker/.env 一致，并更换 AES_KEY
#   set -a && source backend/scripts/env.sh && set +a
#   cd backend && mvn -pl tide-bootstrap -am spring-boot:run
#
# env.sh 已被 backend/.gitignore 忽略，不要提交。
# IDEA：Run Configuration → Environment variables，填同名变量。

# application.yml：application.crypto.aes-key（必填，16 或 32 字节）
export AES_KEY='change-me-16chars'

# application-dev.yml：spring.datasource
# 库名、用户须与 Postgres 中已创建的库一致。backend/docker 的示例是 postgres / postgres。
export DB_HOST='localhost'
export DB_PORT='5432'
export DB_NAME='postgres'
export DB_USER='postgres'
export DB_PWD='change-me'

# application-dev.yml：spring.data.redis
export REDIS_HOST='127.0.0.1'
export REDIS_PORT='6379'
export REDIS_PWD='change-me'
export REDIS_DB='15'

# 默认 dev profile 关闭 Liquibase。空库首次建表时再打开：
# export SPRING_LIQUIBASE_ENABLED=true

# 仅 application-prod.yml 需要（本地 dev 不用）：
# export WYH_JWT_SECRET='change-me'
# export WYH_RSA_PUBLIC_KEY=''
# export WYH_RSA_PRIVATE_KEY=''
