# Running In Docker

## Running on Docker

:::warning

This section requires installing docker directly from source. Future updates will include a docker image.

:::

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
docker run -d --name=skyhook -v "$(pwd)"/config/server.yml:/app/server.yml -p 6379:6379 skyhook
```
