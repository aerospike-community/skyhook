# Stop the connector service.
if [ -d /run/systemd/system ]; then
    systemctl stop skyhook >/dev/null 2>&1 || true
fi