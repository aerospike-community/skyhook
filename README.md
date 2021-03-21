# redispike
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

## Connectivity
Any Redis client can connect to `redispike` as if it were a regular Redis server.

For tests purposes use [redis-cli](https://redis.io/topics/rediscli) or even the [nc](https://www.commandlinux.com/man-page/man1/nc.1.html) (or netcat) utility:
```sh
echo "GET key1\r\n" | nc localhost 6379
```
