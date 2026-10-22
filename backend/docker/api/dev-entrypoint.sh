#!/usr/bin/env bash
# 在挂载的 backend/ 上执行 spring-boot:run。
# 源码变更后再 compile 一次，由 DevTools 重启 JVM，避免为每次保存重建镜像。
set -euo pipefail

cd /src

if [[ ! -f pom.xml ]]; then
  echo "api: /src/pom.xml 不存在。compose 需要把 backend/ 挂到 /src。" >&2
  exit 1
fi

export MAVEN_OPTS="${MAVEN_OPTS:--Xms128m -Xmx768m}"
export SPRING_DEVTOOLS_RESTART_QUIET_PERIOD="${SPRING_DEVTOOLS_RESTART_QUIET_PERIOD:-8s}"
export SPRING_DEVTOOLS_RESTART_POLL_INTERVAL="${SPRING_DEVTOOLS_RESTART_POLL_INTERVAL:-2s}"

APP_PORT="${SERVER_PORT:-8000}"
APP_PID=""
WATCH_PID=""

kill_tree() {
  local pid="${1:-}"
  local child
  if [[ -z "${pid}" ]]; then
    return 0
  fi
  if command -v ps >/dev/null 2>&1; then
    for child in $(ps -o pid= --ppid "${pid}" 2>/dev/null || true); do
      kill_tree "${child}"
    done
  fi
  kill -TERM "${pid}" 2>/dev/null || true
}

shutdown() {
  kill_tree "${WATCH_PID}"
  kill_tree "${APP_PID}"
  if [[ -n "${WATCH_PID}" ]]; then
    wait "${WATCH_PID}" 2>/dev/null || true
  fi
  if [[ -n "${APP_PID}" ]]; then
    wait "${APP_PID}" 2>/dev/null || true
  fi
  exit 0
}

trap shutdown TERM INT

port_open() {
  bash -c "echo >/dev/tcp/127.0.0.1/${APP_PORT}" >/dev/null 2>&1
}

watch_sources() {
  local stamp changed
  # 子进程不要继承主流程的 TERM 处理，否则停掉监听时会把 API 一起杀掉。
  trap - TERM INT
  stamp="$(mktemp)"
  touch "${stamp}"
  # 等首次编译结束并监听端口，避免和 spring-boot:run 同时解析依赖。
  while kill -0 "${APP_PID}" 2>/dev/null; do
    if port_open; then
      break
    fi
    sleep 5
  done
  while kill -0 "${APP_PID}" 2>/dev/null; do
    sleep 3
    changed="$(
      find . \
        \( -path ./docker -o -path '*/target' -o -name .git \) -prune -o \
        \( -path '*/src/main/java/*' -o -path '*/src/main/resources/*' \) \
        -type f -newer "${stamp}" -print -quit || true
    )"
    if [[ -n "${changed}" ]]; then
      # 先拨快时间戳，编译期间的新保存留到下一轮。
      touch "${stamp}"
      echo "api: 源码已变更，正在编译以便 DevTools 重启（${changed}）" >&2
      if ! mvn -B -pl wyh-bootstrap -am compile -DskipTests -o -q; then
        echo "api: 增量编译失败。若刚改过 pom 或依赖，请 restart api 容器。" >&2
      fi
    fi
  done
  rm -f "${stamp}"
}

mvn_cmd=(mvn)
if command -v stdbuf >/dev/null 2>&1; then
  mvn_cmd=(stdbuf -oL -eL mvn)
fi

"${mvn_cmd[@]}" -B -pl wyh-bootstrap -am spring-boot:run \
  -Pdocker-dev \
  -DskipTests \
  -Dspring-boot.run.profiles=dev \
  -Dspring-boot.run.jvmArguments="-XX:+UseContainerSupport -XX:MaxRAMPercentage=70.0 -Djava.security.egd=file:/dev/./urandom" &
APP_PID=$!

if [[ "${API_HOT_RELOAD:-true}" == "true" ]]; then
  watch_sources &
  WATCH_PID=$!
fi

set +e
wait "${APP_PID}"
status=$?
set -e

kill_tree "${WATCH_PID}"
if [[ -n "${WATCH_PID}" ]]; then
  wait "${WATCH_PID}" 2>/dev/null || true
fi
APP_PID=""
WATCH_PID=""
exit "${status}"
