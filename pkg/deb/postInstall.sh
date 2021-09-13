# create aerospike group if it isn't already there
if ! getent group aerospike >/dev/null; then
    groupadd -r aerospike
fi

# create aerospike user if it isn't already there
if ! getent passwd aerospike >/dev/null; then
    useradd -r -d /opt/aerospike -c 'Aerospike services' -g aerospike -s /sbin/nologin aerospike
fi

mkdir -p /var/log/skyhook
mkdir -p /etc/skyhook
mkdir -p /opt/skyhook/usr-lib

for dir in /opt/skyhook /var/log/skyhook ; do
    if [ -d $dir ]; then
      chown -R aerospike:aerospike $dir
    fi
done

if [ -d /run/systemd/system ]; then
    systemctl --system daemon-reload >/dev/null 2>&1 || true
fi