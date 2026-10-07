#!/usr/bin/env bash
set -euo pipefail
# 1 GiB production VM: use existing disk space, no new cloud resources.
# SSH actions may execute script content directly rather than a saved file.
# Pass the complete root-only payload through stdin instead of relying on $0.
sudo -n bash -s <<'MJU_HOST_MEMORY_ROOT'
set -euo pipefail
SWAP_PATH=/mju-swapfile
if [ -L "$SWAP_PATH" ] || { [ -e "$SWAP_PATH" ] && [ ! -f "$SWAP_PATH" ]; }; then
  echo 'Unexpected swap path type' >&2
  exit 1
fi
if ! swapon --noheadings --show=NAME | awk -v p="$SWAP_PATH" '$1 == p {found=1} END {exit !found}'; then
  if [ ! -e "$SWAP_PATH" ]; then
    (umask 077; fallocate -l 1G "$SWAP_PATH")
    chmod 600 "$SWAP_PATH"
    mkswap "$SWAP_PATH"
  fi
  swapon "$SWAP_PATH"
fi
if ! awk -v p="$SWAP_PATH" '$1 == p {found=1} END {exit !found}' /etc/fstab; then
  printf '%s none swap sw 0 0\n' "$SWAP_PATH" >> /etc/fstab
fi
printf 'vm.swappiness=10\n' > /etc/sysctl.d/90-mju-memory.conf
sysctl -p /etc/sysctl.d/90-mju-memory.conf
swapon --show
free -m

# Keep numeric pressure history on this host for future outage diagnosis.
install -d -m 755 /usr/local/sbin
cat > /usr/local/sbin/mju-resource-snapshot <<'SNAPSHOT'
#!/usr/bin/env bash
set -euo pipefail
read -r load_one load_five load_fifteen processes last_pid < /proc/loadavg
printf 'load=%s,%s,%s processes=%s\n' "$load_one" "$load_five" "$load_fifteen" "$processes"
awk '/^(MemTotal|MemAvailable|SwapTotal|SwapFree):/ {printf "%s%s%s ", $1, $2, $3} END {print ""}' /proc/meminfo
for resource in cpu memory io; do
  if [ -r "/proc/pressure/$resource" ]; then
    sed "s/^/$resource /" "/proc/pressure/$resource"
  fi
done
SNAPSHOT
chmod 755 /usr/local/sbin/mju-resource-snapshot
cat > /etc/systemd/system/mju-resource-snapshot.service <<'SERVICE'
[Unit]
Description=MJU numeric host resource snapshot
[Service]
Type=oneshot
User=nobody
ExecStart=/usr/local/sbin/mju-resource-snapshot
TimeoutStartSec=10
MemoryMax=16M
NoNewPrivileges=yes
ProtectSystem=strict
ProtectHome=yes
SERVICE
cat > /etc/systemd/system/mju-resource-snapshot.timer <<'TIMER'
[Unit]
Description=Record MJU host resource pressure once per minute
[Timer]
OnBootSec=60s
OnUnitActiveSec=60s
AccuracySec=5s
[Install]
WantedBy=timers.target
TIMER
systemctl daemon-reload
systemctl enable --now mju-resource-snapshot.timer
systemctl start mju-resource-snapshot.service
systemctl is-active mju-resource-snapshot.timer
MJU_HOST_MEMORY_ROOT
