# redispike
[![Build](https://github.com/aerospike/redispike/actions/workflows/build.yml/badge.svg)](https://github.com/aerospike/redispike/actions/workflows/build.yml)

The [Redis](https://redis.io/) server interface to the [Aerospike](https://www.aerospike.com/) database.

`redispike` is a standalone server application written in Kotlin which projects Redis protocol commands
to an Aerospike cluster using the Aerospike Java client under the hood.
The server supports a single namespace and set configuration where the incoming commands will be applied.
This project uses [Netty](https://netty.io/) as a non-blocking I/O client-server framework.

## Prerequisites
* Java 8 or later
* Aerospike Server version 4.9+

## Installation
To build the project:
```sh
./gradlew clean build 
```
A fat executable jar will be created under the `build/libs` folder.

Usage:
```text
redispike % java -jar redispike-[version]-all.jar -h                   
Usage: redispike [-h] [-f=<configFile>]
Redis to Aerospike proxy server
  -f, --config-file=<configFile>
               yaml formatted configuration file
  -h, --help   display this help and exit
```

To run the server:
```sh
java -jar redispike-[version]-all.jar -f config/server.yml
```
The configuration file carries all the settings the server needs and is in YAML format.
An example configuration file can be found in the `config` folder.
If no configuration file is specified, the default settings will be applied.

```text
[main] INFO  c.a.r.RedispikeServer$Companion - Starting the Server...
```

Now the server is listening to the `config.redisPort` (default: 6379) and is ready to serve.

## Implemented Commands
<details><summary>List of the supported Redis commands</summary>

Operation | Description
----------|------------
[APPEND](https://redis.io/commands/append) *key value* | If key already exists and is a string, this command appends the value at the end of the string. If key does not exist it is created and set as an empty string.
[DEL](https://redis.io/commands/del) *key* | Removes the specified key.
[EXISTS](https://redis.io/commands/exists) *key [key ...]* | Returns if key exists.
[EXPIRE](https://redis.io/commands/expire) *key seconds* | Set a timeout on key. After the timeout has expired, the key will automatically be deleted.
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
[HSETNX](https://redis.io/commands/hsetnx) *key field value* | Sets field in the hash stored at key to value, only if field does not yet exist.
[HSET](https://redis.io/commands/hset) *key field value [field value ...]* | Sets field in the hash stored at key to value.
[HSTRLEN](https://redis.io/commands/hstrlen) *key field* | Returns the string length of the value associated with field in the hash stored at key.
[HVALS](https://redis.io/commands/hvals) *key* | Returns all values in the hash stored at key.
[INCRBYFLOAT](https://redis.io/commands/incrbyfloat) *key increment* | Increment the string representing a floating point number stored at key by the specified increment.
[INCRBY](https://redis.io/commands/incrby) *key increment* | Increments the number stored at key by increment.
[INCR](https://redis.io/commands/incr) *key* | Increments the number stored at key by one.
[LINDEX](https://redis.io/commands/lindex) *key index* | Returns the element at index index in the list stored at key.
[LLEN](https://redis.io/commands/llen) *key* | Returns the length of the list stored at key.
[LPOP](https://redis.io/commands/lpop) *key [count]* | Removes and returns the first elements of the list stored at key.
[LPUSHX](https://redis.io/commands/lpushx) *key element [element ...]* | Inserts specified values at the head of the list stored at key, only if key already exists and holds a list.
[LPUSH](https://redis.io/commands/lpush) *key element [element ...]* | Insert all the specified values at the head of the list stored at key.
[LRANGE](https://redis.io/commands/lrange) *key start stop* | Returns the specified elements of the list stored at key.
[MGET](https://redis.io/commands/mget) *key [key ...]* | Returns the values of all specified keys.
[PEXPIRE](https://redis.io/commands/pexpire) *key milliseconds* | This command works exactly like EXPIRE but the time to live of the key is specified in milliseconds instead of seconds.
[PSETEX](https://redis.io/commands/psetex) *key milliseconds value* | PSETEX works exactly like SETEX with the sole difference that the expire time is specified in milliseconds instead of seconds.
[PTTL](https://redis.io/commands/pttl) *key* | Returns the amount of remaining time in milliseconds.
[RPOP](https://redis.io/commands/rpop) *key [count]* | Removes and returns the last elements of the list stored at key.
[RPUSHX](https://redis.io/commands/rpushx) *key element [element ...]* | Inserts specified values at the tail of the list stored at key, only if key already exists and holds a list.
[RPUSH](https://redis.io/commands/rpush) *key element [element ...]* | Insert all the specified values at the tail of the list stored at key.
[SADD](https://redis.io/commands/sadd) *key member [member ...]* | Add the specified members to the set stored at key.
[SCARD](https://redis.io/commands/scard) *key* | Returns the set cardinality (number of elements) of the set stored at key.
[SETEX](https://redis.io/commands/setex) *key seconds value* | Set key to hold the string value and set key to timeout after a given number of seconds.
[SETNX](https://redis.io/commands/setnx) *key value* | Set key to hold string value if key does not exist.
[SET](https://redis.io/commands/set) *key value* | Set key to hold the string value. If key already holds a value, it is overwritten, regardless of its type.
[SISMEMBER](https://redis.io/commands/sismember) *key member* | Returns if member is a member of the set stored at key.
[SMEMBERS](https://redis.io/commands/smembers) *key* | Returns all the members of the set value stored at key.
[SREM](https://redis.io/commands/srem) *key member [member ...]* | Remove the specified members from the set stored at key.
[STRLEN](https://redis.io/commands/strlen) *key* | Returns the length of the string value stored at key. An error is returned when key holds a non-string value.
[TTL](https://redis.io/commands/ttl) *key* | Returns the remaining time to live of a key that has a timeout.
[ZCARD](https://redis.io/commands/zcard) *key* | Returns the sorted set cardinality (number of elements) of the sorted set stored at key.
[ZREM](https://redis.io/commands/zrem) *key member [member ...]* | Removes the specified members from the sorted set stored at key.

</details>

## Connectivity
Any Redis client can connect to `redispike` as if it were a regular Redis server.

For tests purposes use [redis-cli](https://redis.io/topics/rediscli) or even the [nc](https://www.commandlinux.com/man-page/man1/nc.1.html) (or netcat) utility:
```sh
echo "GET key1\r\n" | nc localhost 6379
```
