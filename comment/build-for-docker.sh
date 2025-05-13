#!/bin/bash

# 1. 변수 정의
MODULE_NAME="comment"
SERVICE_NAME="${MODULE_NAME}-service"
IMAGE_NAME="${SERVICE_NAME}"
NETWORK_NAME="my-internal-net"
IMAGE_TAR="${IMAGE_NAME}.tar"
GRADLE_BUILD_CMD="../gradlew :${MODULE_NAME}:clean :${MODULE_NAME}:build"

# 2. Gradle 빌드 수행 (서브모듈 대상)
echo "[INFO] Gradle 빌드 시작 (모듈: ${MODULE_NAME})..."
if $GRADLE_BUILD_CMD; then
  echo "[INFO] Gradle 빌드 완료."
else
  echo "[ERROR] Gradle 빌드 실패. 작업을 중단합니다."
  exit 1
fi

# 3. Docker 네트워크 존재 여부 확인 및 없으면 생성
if ! docker network ls --format '{{.Name}}' | grep -q "^${NETWORK_NAME}$"; then
  echo "[INFO] Docker 네트워크 '${NETWORK_NAME}' 생성 중..."
  docker network create "${NETWORK_NAME}"
else
  echo "[INFO] Docker 네트워크 '${NETWORK_NAME}' 이미 존재함."
fi

# 4. 기존 이미지가 존재하면 관련 컨테이너 종료 및 이미지 삭제
if docker images -q "${IMAGE_NAME}" > /dev/null 2>&1; then
  echo "[INFO] 기존 이미지 '${IMAGE_NAME}' 존재. 관련 컨테이너 정리 및 이미지 삭제..."

  # 이미지 기반 컨테이너 찾기 → 정지 및 삭제
  CONTAINERS=$(docker ps -a --filter "ancestor=${IMAGE_NAME}:latest" -q)
  if [ -n "$CONTAINERS" ]; then
    echo "[INFO] 관련 컨테이너 종료 및 삭제 중..."
    docker stop $CONTAINERS
    docker rm $CONTAINERS
  fi

  docker rmi -f "${IMAGE_NAME}"
else
  echo "[INFO] 기존 이미지 없음. 새로 빌드 진행."
fi

# 5. Dockerfile 기반으로 이미지 빌드
echo "[INFO] Docker 이미지 빌드 중... (${IMAGE_NAME})"
docker build -t "${IMAGE_NAME}" .

# 6. 이미지 tar 파일 존재 시 삭제
if [ -f "${IMAGE_TAR}" ]; then
  echo "[INFO] 기존 이미지 파일 '${IMAGE_TAR}' 삭제 중..."
  rm -f "${IMAGE_TAR}"
fi

# 7. 이미지 저장
echo "[INFO] 이미지 파일로 저장 중... (${IMAGE_TAR})"
docker save -o "${IMAGE_TAR}" "${IMAGE_NAME}"

echo "[SUCCESS] 모든 작업 완료."