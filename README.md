# Skyhook
[![Build](https://github.com/aerospike/skyhook/actions/workflows/build.yml/badge.svg)](https://github.com/aerospike/skyhook/actions/workflows/build.yml)

Skyhook is a Redis API compatible gateway to [Aerospike](https://www.aerospike.com/) Database.

  * [Overview](#overview)
  * [Connecting a Redis Client to Skyhook](#connectivity)
  * [Redis Command Coverage](#redis-command-coverage)
  * [Known Limitations](#known-limitations)
  * [Deploying Skyhook](#deploying-skyhook)
    * [Prerequisites](#prerequisites)
    * [Installing](#installing)
      * [Building from Source](#building-from-source)
    * [Running the Server](#running)
      * [Configuring Skyhook](#configuration-properties)
  * [License](#license)

## Overview

Skyhook is designed as a standalone server application written in Kotlin, which
accepts Redis protocol commands and projects them to an Aerospike cluster using
the Aerospike Java client under the hood. It uses [Netty](https://netty.io/) as
a non-blocking I/O client-server framework.

This project is now in **beta**. If you're an enterprise customer feel free to
reach out to our support with feedback and feature requests.
We appreciate feedback from the Aerospike community on
[issues](https://github.com/aerospike/skyhook/issues)
related to Skyhook.

## Connectivity
Any Redis client can connect to Skyhook as if it were a regular Redis server.

For tests purposes use [redis-cli](https://redis.io/topics/rediscli) or even the [nc](https://www.commandlinux.com/man-page/man1/nc.1.html) (or netcat) utility:
```sh
echo "GET key1\r\n" | nc localhost 6379
```

## Redis Command Coverage
<details><summary>List of the supported Redis commands</summary>

Operation | Description
----------|------------
[APPEND](https://redis.io/commands/append) *key value* | If key already exists and is a string, this command appends the value at the end of the string. If key does not exist it is created and set as an empty string.
[AUTH](https://redis.io/commands/auth) *[username] password* | The AUTH command authenticates the current connection.
[BGSAVE](https://redis.io/commands/bgsave) | Returns OK.
[COMMAND](https://redis.io/commands/command) | Returns Array reply of details about all Redis commands.
[COMMAND COUNT](https://redis.io/commands/command-count) | Returns Integer reply of number of total commands in this Redis server.
[COMMAND INFO](https://redis.io/commands/command-info) *command-name [command-name ...]* | Returns Array reply of details about multiple Redis commands.
[DBSIZE](https://redis.io/commands/dbsize) | Returns the number of keys in the currently-selected database.
[DECR](https://redis.io/commands/decr) *key* | Decrements the number stored at key by one.
[DECRBY](https://redis.io/commands/decrby) *key decrement* | Decrements the number stored at key by decrement.
[DEL](https://redis.io/commands/del) *key* | Removes the specified key.
[DISCARD](https://redis.io/commands/discard) | Flushes all previously queued commands in a [transaction](https://redis.io/topics/transactions) and restores the connection state to normal.
[ECHO](https://redis.io/commands/echo) *message* | Returns message.
[EXEC](https://redis.io/commands/exec) | Executes all previously queued commands in a [transaction](https://redis.io/topics/transactions) and restores the connection state to normal.
[EXISTS](https://redis.io/commands/exists) *key [key ...]* | Returns if key exists.
[EXPIRE](https://redis.io/commands/expire) *key seconds* | Set a timeout on key. After the timeout has expired, the key will automatically be deleted.
[EXPIREAT](https://redis.io/commands/expireat) *key timestamp* | EXPIREAT has the same effect and semantic as EXPIRE, but instead of specifying the number of seconds representing the TTL (time to live), it takes an absolute Unix timestamp (seconds since January 1, 1970).
[FLUSHALL](https://redis.io/commands/flushall) | Delete all the keys of all the existing databases, not just the currently selected one.
[FLUSHDB](https://redis.io/commands/flushdb) | Delete all the keys of the currently selected DB.
[GETSET](https://redis.io/commands/getset) *key value* | Atomically sets key to value and returns the old value stored at key.
[GET](https://redis.io/commands/get) *key* | Get the value of key.
[HDEL](https://redis.io/commands/hdel) *key field [field ...]* | Removes the specified fields from the hash stored at key.
[HEXISTS](https://redis.io/commands/hexists) *key field* | Returns if field is an existing field in the hash stored at key.
[HGETALL](https://redis.io/commands/hgetall) *key* | Returns all fields and values of the hash stored at key.
[HGET](https://redis.io/commands/hget) *key field* | Returns the value associated with field in the hash stored at key.
[HINCRBYFLOAT](https://redis.io/commands/hincrbyfloat) *key field increment* | Increment the specified field of a hash stored at key, and representing a floating point number, by the specified increment.
[HINCRBY](https://redis.io/commands/hincrby) *key field increment* | Increments the number stored at field in the hash stored at key by increment.
[HKEYS](https://redis.io/commands/hkeys) *key* | Returns all field names in the hash stored at key.
[HLEN](https://redis.io/commands/hlen) *key* | Returns the number of fields contained in the hash stored at key.
[HMGET](https://redis.io/commands/hmget) *key field [field ...]* | Returns the values associated with the specified fields in the hash stored at key.
[HMSET](https://redis.io/commands/hmset) *key field value [field value ...]* | Sets the specified fields to their respective values in the hash stored at key.
[HSCAN](https://redis.io/commands/hscan) *key cursor [MATCH pattern] [COUNT count]* | See SCAN for HSCAN documentation.
[HSETNX](https://redis.io/commands/hsetnx) *key field value* | Sets field in the hash stored at key to value, only if field does not yet exist.
[HSET](https://redis.io/commands/hset) *key field value [field value ...]* | Sets field in the hash stored at key to value.
[HSTRLEN](https://redis.io/commands/hstrlen) *key field* | Returns the string length of the value associated with field in the hash stored at key.
[HVALS](https://redis.io/commands/hvals) *key* | Returns all values in the hash stored at key.
[INCRBYFLOAT](https://redis.io/commands/incrbyfloat) *key increment* | Increment the string representing a floating point number stored at key by the specified increment.
[INCRBY](https://redis.io/commands/incrby) *key increment* | Increments the number stored at key by increment.
[INCR](https://redis.io/commands/incr) *key* | Increments the number stored at key by one.
[KEYS](https://redis.io/commands/keys) *pattern* | Returns all keys matching pattern.
[LINDEX](https://redis.io/commands/lindex) *key index* | Returns the element at index index in the list stored at key.
[LLEN](https://redis.io/commands/llen) *key* | Returns the length of the list stored at key.
[LOLWUT](https://redis.io/commands/lolwut) *[VERSION version]* | The LOLWUT command displays the Redis version.
[LPOP](https://redis.io/commands/lpop) *key [count]* | Removes and returns the first elements of the list stored at key.
[LPUSHX](https://redis.io/commands/lpushx) *key element [element ...]* | Inserts specified values at the head of the list stored at key, only if key already exists and holds a list.
[LPUSH](https://redis.io/commands/lpush) *key element [element ...]* | Insert all the specified values at the head of the list stored at key.
[LRANGE](https://redis.io/commands/lrange) *key start stop* | Returns the specified elements of the list stored at key.
[MGET](https://redis.io/commands/mget) *key [key ...]* | Returns the values of all specified keys.
[MSET](https://redis.io/commands/mset) *key value [key value ...]* | Sets the given keys to their respective values.
[MSETNX](https://redis.io/commands/msetnx) *key value [key value ...]* | Sets the given keys to their respective values. MSETNX will not perform any operation at all even if just a single key already exists.
[MULTI](https://redis.io/commands/multi) | Marks the start of a [transaction](https://redis.io/topics/transactions) block. Subsequent commands will be queued for execution using EXEC.
[PERSIST](https://redis.io/commands/persist) *key* | Remove the existing timeout on key, turning the key from volatile (a key with an expire set) to persistent.
[PEXPIRE](https://redis.io/commands/pexpire) *key milliseconds* | This command works exactly like EXPIRE but the time to live of the key is specified in milliseconds instead of seconds.
[PEXPIREAT](https://redis.io/commands/pexpireat) *key milliseconds-timestamp* | PEXPIREAT has the same effect and semantic as EXPIREAT, but the Unix time at which the key will expire is specified in milliseconds instead of seconds.
[PING](https://redis.io/commands/ping) *[message]* | Returns PONG if no argument is provided, otherwise return a copy of the argument as a bulk.
[PSETEX](https://redis.io/commands/psetex) *key milliseconds value* | PSETEX works exactly like SETEX with the sole difference that the expire time is specified in milliseconds instead of seconds.
[PTTL](https://redis.io/commands/pttl) *key* | Returns the amount of remaining time in milliseconds.
[QUIT](https://redis.io/commands/quit) | Returns OK.
[RANDOMKEY](https://redis.io/commands/randomkey) | Return a random key from the currently selected database.
[RESET](https://redis.io/commands/reset) | Returns 'RESET'.
[RPOP](https://redis.io/commands/rpop) *key [count]* | Removes and returns the last elements of the list stored at key.
[RPUSHX](https://redis.io/commands/rpushx) *key element [element ...]* | Inserts specified values at the tail of the list stored at key, only if key already exists and holds a list.
[RPUSH](https://redis.io/commands/rpush) *key element [element ...]* | Insert all the specified values at the tail of the list stored at key.
[SADD](https://redis.io/commands/sadd) *key member [member ...]* | Add the specified members to the set stored at key.
[SAVE](https://redis.io/commands/save) | Returns OK.
[SCAN](https://redis.io/commands/scan) *cursor [MATCH pattern] [COUNT count] [TYPE type]* | The SCAN command and the closely related commands SSCAN, HSCAN and ZSCAN are used in order to incrementally iterate over a collection of elements.
[SCARD](https://redis.io/commands/scard) *key* | Returns the set cardinality (number of elements) of the set stored at key.
[SET](https://redis.io/commands/set) *key value [EX seconds/PX milliseconds/EXAT timestamp/PXAT milliseconds-timestamp/KEEPTTL] [NX/XX] [GET]* | Set key to hold the string value. If key already holds a value, it is overwritten, regardless of its type.
[SETEX](https://redis.io/commands/setex) *key seconds value* | Set key to hold the string value and set key to timeout after a given number of seconds.
[SETNX](https://redis.io/commands/setnx) *key value* | Set key to hold string value if key does not exist.
[SINTER](https://redis.io/commands/sinter) *key [key ...]* | Returns the members of the set resulting from the intersection of all the given sets.
[SINTERSTORE](https://redis.io/commands/sinterstore) *destination key [key ...]* | This command is equal to SINTER, but instead of returning the resulting set, it is stored in destination.
[SISMEMBER](https://redis.io/commands/sismember) *key member* | Returns if member is a member of the set stored at key.
[SMEMBERS](https://redis.io/commands/smembers) *key* | Returns all the members of the set value stored at key.
[SREM](https://redis.io/commands/srem) *key member [member ...]* | Remove the specified members from the set stored at key.
[SSCAN](https://redis.io/commands/sscan) *key cursor [MATCH pattern] [COUNT count]* | See SCAN for SSCAN documentation.
[STRLEN](https://redis.io/commands/strlen) *key* | Returns the length of the string value stored at key. An error is returned when key holds a non-string value.
[SUNION](https://redis.io/commands/sunion) *key [key ...]* | Returns the members of the set resulting from the union of all the given sets.
[SUNIONSTORE](https://redis.io/commands/sunionstore) *destination key [key ...]* | This command is equal to SUNION, but instead of returning the resulting set, it is stored in destination.
[TIME](https://redis.io/commands/time) | Returns the current server time.
[TOUCH](https://redis.io/commands/touch) *key [key ...]* | Alters the last access time of a key(s). A key is ignored if it does not exist.
[TTL](https://redis.io/commands/ttl) *key* | Returns the remaining time to live of a key that has a timeout.
[TYPE](https://redis.io/commands/type) *key* | Returns the string representation of the type of the value stored at key. The different types that can be returned are: string, list, set, zset, hash and stream.
[UNLINK](https://redis.io/commands/unlink) *key [key ...]* | This command is an alias to DEL.
[ZADD](https://redis.io/commands/zadd) *key [NX/XX] [GT/LT] [CH] [INCR] score member [score member ...]* | Adds all the specified members with the specified scores to the sorted set stored at key.
[ZCARD](https://redis.io/commands/zcard) *key* | Returns the sorted set cardinality (number of elements) of the sorted set stored at key.
[ZCOUNT](https://redis.io/commands/zcount) *key min max* | Returns the number of elements in the sorted set at key with a score between min and max.
[ZINCRBY](https://redis.io/commands/zincrby) *key increment member* | Increments the score of member in the sorted set stored at key by increment.
[ZLEXCOUNT](https://redis.io/commands/zlexcount) *key min max* | When all the elements in a sorted set are inserted with the same score, in order to force lexicographical ordering, this command returns the number of elements in the sorted set at key with a value between min and max.
[ZMSCORE](https://redis.io/commands/zmscore) *key member [member ...]* | Returns the scores associated with the specified members in the sorted set stored at key.
[ZPOPMAX](https://redis.io/commands/zpopmax) *key [count]* | Removes and returns up to count members with the highest scores in the sorted set stored at key.
[ZPOPMIN](https://redis.io/commands/zpopmin) *key [count]* | Removes and returns up to count members with the lowest scores in the sorted set stored at key.
[ZRANDMEMBER](https://redis.io/commands/zrandmember) *key [count [WITHSCORES]]* | When called with just the key argument, return a random element from the sorted set value stored at key.
[ZRANGE](https://redis.io/commands/zrange) *key min max [BYSCORE/BYLEX] [REV] [LIMIT offset count] [WITHSCORES]* | Returns the specified range of elements in the sorted set stored at <key>.
[ZRANGEBYLEX](https://redis.io/commands/zrangebylex) *key min max [LIMIT offset count]* | When all the elements in a sorted set are inserted with the same score, in order to force lexicographical ordering, this command returns all the elements in the sorted set at key with a value between min and max.
[ZRANGEBYSCORE](https://redis.io/commands/zrangebyscore) *key min max [WITHSCORES] [LIMIT offset count]* | Returns all the elements in the sorted set at key with a score between min and max (including elements with score equal to min or max). The elements are considered to be ordered from low to high scores.
[ZRANGESTORE](https://redis.io/commands/zrangestore) *dst src min max [BYSCORE/BYLEX] [REV] [LIMIT offset count]* | This command is like ZRANGE, but stores the result in the <dst> destination key.
[ZRANK](https://redis.io/commands/zrank) *key member* | Returns the rank of member in the sorted set stored at key, with the scores ordered from low to high.
[ZREM](https://redis.io/commands/zrem) *key member [member ...]* | Removes the specified members from the sorted set stored at key.
[ZREMRANGEBYLEX](https://redis.io/commands/zremrangebylex) *key min max* | When all the elements in a sorted set are inserted with the same score, in order to force lexicographical ordering, this command removes all elements in the sorted set stored at key between the lexicographical range specified by min and max.
[ZREMRANGEBYRANK](https://redis.io/commands/zremrangebyrank) *key start stop* | Removes all elements in the sorted set stored at key with rank between start and stop.
[ZREMRANGEBYSCORE](https://redis.io/commands/zremrangebyscore) *key min max* | Removes all elements in the sorted set stored at key with a score between min and max (inclusive).
[ZREVRANGE](https://redis.io/commands/zrevrange) *key start stop [WITHSCORES]* | Returns the specified range of elements in the sorted set stored at key.
[ZREVRANGEBYLEX](https://redis.io/commands/zrevrangebylex) *key max min [LIMIT offset count]* | Apart from the reversed ordering, ZREVRANGEBYLEX is similar to ZRANGEBYLEX.
[ZREVRANGEBYSCORE](https://redis.io/commands/zrevrangebyscore) *key max min [WITHSCORES] [LIMIT offset count]* | Returns all the elements in the sorted set at key with a score between max and min (including elements with score equal to max or min). In contrary to the default ordering of sorted sets, for this command the elements are considered to be ordered from high to low scores.
[ZSCAN](https://redis.io/commands/zscan) *key cursor [MATCH pattern] [COUNT count]* | See SCAN for ZSCAN documentation.

</details>

## Known Limitations
 * A partial but growing list of Redis commands. See [Redis Command Coverage](#redis-command-coverage).
 * By default, an Aerospike namespace does not allow for TTLs. Read [more](https://discuss.aerospike.com/t/faq-what-are-expiration-eviction-and-stop-writes/2311) on how to set up expiration and eviction support.
 * Like Redis Cluster, Skyhook supports a single Redis 'database 0', which maps to a single namespace and set in the Aerospike Database.
 * Will not try to implement the cluster operations sub-commands of `CLUSTER`, `CLIENT`, `CONFIG`,  `MEMORY`, `MONITOR`, `LATENCY`.
 * No support for Pub/Sub commands.
 * No support for Lua scripts.

## Deploying Skyhook

### Prerequisites
* Java 8 or later
* Aerospike Server version 4.9+

### Installing

#### Building from Source
To build the project:
```sh
./gradlew clean build
```
A fat executable jar will be created under the `build/libs` folder.

### Running
Usage:
```text
% java -jar skyhook-[version]-all.jar -h

Usage: skyhook [-h] [-f=<configFile>]
Redis to Aerospike proxy server
  -f, --config-file=<configFile>
               yaml formatted configuration file
  -h, --help   display this help and exit
```

To run the server:
```sh
java -jar skyhook-[version]-all.jar -f config/server.yml
```

The configuration file carries all the settings the server needs and is in YAML
format. An example configuration file can be found in the `config` folder.
If no configuration file is specified, the default settings will be applied.

```text
[main] INFO  c.a.skyhook.SkyhookServer$Companion - Starting the Server...
```

Now the server is listening to the `config.redisPort` (default: 6379) and is ready to serve.

If you wish to deploy Skyhook as a cluster of nodes, you can find some example configurations [here](docs/scaling-out.md).

### Running on Docker
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

#### Configuration Properties

| Property name | Description | Default value |
| ------------- | ----------- | ------------- |
| hostList | The host list to seed the Aerospike cluster. | localhost:3000 |
| namespace | The Aerospike namespace. | test |
| set | The Aerospike set name. | redis |
| clientPolicy | The Aerospike Java client [ClientPolicy](https://docs.aerospike.com/apidocs/java/com/aerospike/client/policy/ClientPolicy.html) configuration properties. | ClientPolicyConfig |
| bin | The Aerospike value bin name. | b |
| typeBin | The Aerospike value [type](https://redis.io/topics/data-types) bin name. | t |
| redisPort | The server port to bind to. | 6379 |
| unixSocket | The server will bind on unix socket if configured. | |
| workerThreads<sup>[1](#worker-threads)</sup> | The Netty worker group size. | number of available cores |
| bossThreads | The Netty acceptor group size. | 2 |

<sup name="worker-threads">1</sup> Used to configure the size of the [Aerospike Java Client EventLoops](https://www.aerospike.com/docs/client/java/usage/async/eventloop.html) as well.

## License
Licensed under an Apache 2.0 License.

This is an active open source project. You can contribute to it by trying
Skyhook, providing feedback, reporting bugs, and implementing more Redis
commands.
