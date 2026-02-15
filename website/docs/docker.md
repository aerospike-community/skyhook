# Running In Docker

## Running on Docker

:::warning

This section requires installing docker directly from source. Future updates will include a docker image.

:::

### Use a prebuilt image

Use the [Skyhook package](https://github.com/aerospike/skyhook/pkgs/container/skyhook) to start a container:

```sh
docker run -d --name=skyhook -p 6379:6379 ghcr.io/aerospike/skyhook:latest
```

### Build from source

Build an image:

```sh
docker build -t skyhook .
```

Run as a Docker container:

```sh
docker run -d --name=skyhook -p 6379:6379 skyhook
```

The image uses the repository configuration file by default.
[Bind mount](https://docs.docker.com/storage/bind-mounts/) a custom file to configure the server:

```sh
docker run -d --name=skyhook -v "$(pwd)"/config/skyhook.yml:/app/skyhook.yml -p 6379:6379 skyhook
```


If you are running Aerospike in another Docker container, consider changing hostList to `host.docker.internal:3000` as shown in the following exmaple config file (`skyhook.yml`):


```
---
# Skyhook server configuration

# hostList: localhost:3000
hostList: host.docker.internal:3000
namespace: test
set: redis
bin: b
redisPort: 6379

# Aerospike Java Client [com.aerospike.client.policy.ClientPolicy] configuration.
#clientPolicy:
#  user: admin
#  password: pwd@1234
#  clusterName: cluster1
#  authMode: EXTERNAL_INSECURE
#  timeout: 1500
#  loginTimeout: 3000
#  asyncMinConnsPerNode: 50
#  asyncMaxConnsPerNode: 200
#  failIfNotConnected: true
#  useServicesAlternate: true

# Bind on unix socket.
#unixSocket: "/tmp/skyhook.sock"

workerThreads: 2
bossThreads: 1
```

