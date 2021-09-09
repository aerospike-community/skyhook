# Usage With Redis client

## Connectivity

Any Redis client can connect to Skyhook as if it were a regular Redis server.

For tests purposes use [redis-cli](https://redis.io/topics/rediscli) or the [nc](https://www.commandlinux.com/man-page/man1/nc.1.html) (or netcat) utility:

```sh
echo "GET key1\r\n" | nc localhost 6379
```

## Supported Commands

The list of Redis commands supported by Skyhook is maintained [here](supported-redis-api).

