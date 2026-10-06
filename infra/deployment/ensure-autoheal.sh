#!/usr/bin/env bash
set -euo pipefail

# Routine app deployments must preserve the existing recovery service.
if docker container inspect autoheal >/dev/null 2>&1; then
  if [ "$(docker inspect --format '{{.State.Running}}' autoheal)" != "true" ]; then
    echo "Starting existing Autoheal container"
    docker start autoheal
  else
    echo "Keeping existing Autoheal container"
  fi
else
  AUTOHEAL_IMAGE=willfarrell/autoheal:latest
  if ! docker image inspect "$AUTOHEAL_IMAGE" >/dev/null 2>&1; then
    IMAGE_READY=false
    for attempt in 1 2 3; do
      echo "Pulling Autoheal image ($attempt/3; 180s limit per attempt)"
      if timeout --kill-after=10s 180s docker pull "$AUTOHEAL_IMAGE"; then
        IMAGE_READY=true
        break
      fi
      if [ "$attempt" -lt 3 ]; then
        sleep "$((attempt * 10))"
      fi
    done
    if [ "$IMAGE_READY" != "true" ]; then
      echo "Autoheal image pull failed after 3 attempts; application deployment has already completed." >&2
      exit 1
    fi
  fi

  docker run -d --name autoheal \
    --restart unless-stopped \
    --log-opt max-size=10m --log-opt max-file=3 \
    -e AUTOHEAL_CONTAINER_LABEL=all \
    -e AUTOHEAL_INTERVAL=30 \
    -v /var/run/docker.sock:/var/run/docker.sock \
    "$AUTOHEAL_IMAGE"
fi

for attempt in $(seq 1 30); do
  if [ "$(docker inspect --format '{{.State.Health.Status}}' autoheal)" = "healthy" ]; then
    echo "Autoheal is healthy"
    exit 0
  fi
  sleep 2
done

echo "Autoheal did not become healthy; application deployment has already completed." >&2
docker inspect --format 'status={{.State.Status}} health={{.State.Health.Status}}' autoheal >&2 || true
exit 1
