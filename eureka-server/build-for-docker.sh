#!/bin/bash

# 1. 변수 정의
IMAGE_NAME="eureka-server-service"
NETWORK_NAME="my-internal-net"
IMAGE_TAR="${IMAGE_NAME}.tar"

# 2. Docker 네트워크 존재 여부 확인 및 없으면 생성
if ! docker network ls --format '{{.Name}}' | grep -q "^${NETWORK_NAME}$"; then
  echo "[INFO] Docker 네트워크 '${NETWORK_NAME}' 생성 중..."
  docker network create "${NETWORK_NAME}"
else
  echo "[INFO] Docker 네트워크 '${NETWORK_NAME}' 이미 존재함."
fi

# 3. 동일한 이미지가 존재하면 관련 컨테이너 종료 및 이미지 삭제
if docker images -q "${IMAGE_NAME}" > /dev/null 2>&1; then
  echo "[INFO] 기존 이미지 '${IMAGE_NAME}' 존재. 관련 컨테이너 정리 및 이미지 삭제..."

  # 이미지 기반 컨테이너 찾기 → 정지 및 삭제
  CONTAINERS=$(docker ps -a --filter "ancestor=${IMAGE_NAME}" -q)
  if [ -n "$CONTAINERS" ]; then
    echo "[INFO] 관련 컨테이너 종료 및 삭제 중..."
    docker stop $CONTAINERS
    docker rm $CONTAINERS
  fi

  # 이미지 삭제
  docker rmi -f "${IMAGE_NAME}"
else
  echo "[INFO] 기존 이미지 없음. 새로 빌드 진행."
fi

# 4. Dockerfile 기반으로 이미지 빌드
echo "[INFO] 이미지 빌드 중... (${IMAGE_NAME})"
docker build -t "${IMAGE_NAME}" .

# 5. 이미지 tar 파일 존재 시 삭제
if [ -f "${IMAGE_TAR}" ]; then
  echo "[INFO] 기존 이미지 파일 '${IMAGE_TAR}' 삭제 중..."
  rm -f "${IMAGE_TAR}"
fi

# 6. 이미지 저장
echo "[INFO] 이미지 파일로 저장 중... (${IMAGE_TAR})"
docker save -o "${IMAGE_TAR}" "${IMAGE_NAME}"

echo "[SUCCESS] 모든 작업 완료."