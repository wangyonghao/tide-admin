#!/bin/bash

# 如果发生错误，则退出
set -e

# 1. 使用 Maven 打包应用程序
echo "Packaging applications with Maven..."
# 指定根 pom.xml 文件并跳过测试
mvn -f ../pom.xml clean package -DskipTests

# 2. 构建 Docker 镜像
echo "Building Docker images..."

# 构建 tide-admin 镜像
echo "Building tide-admin image..."
container build -f ../tide-admin-server/Dockerfile -t wangyonghao/tide-admin ../tide-admin-server

# 构建 tide-schedule 镜像
echo "Building tide-schedule image..."
container build -f ../tide-schedule/Dockerfile -t wangyonghao/tide-schedule ../tide-schedule

echo "Docker images built successfully."