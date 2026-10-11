# 本地开发 API 运行时：Eclipse Temurin 17 JDK + Maven。
# 源码由 compose 挂载到 /src，不在镜像里打包。改 Java 不用重建镜像。
FROM maven:3.9.11-eclipse-temurin-17

COPY api/dev-entrypoint.sh /usr/local/bin/dev-entrypoint.sh
RUN chmod +x /usr/local/bin/dev-entrypoint.sh

WORKDIR /src
ENTRYPOINT ["/usr/local/bin/dev-entrypoint.sh"]
