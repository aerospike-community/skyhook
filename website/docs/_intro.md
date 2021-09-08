# Skyhook

[![Build](https://github.com/aerospike/skyhook/actions/workflows/build.yml/badge.svg)](https://github.com/aerospike/skyhook/actions/workflows/build.yml)

Skyhook is a Redis API-compatible gateway to the [Aerospike](https://www.aerospike.com/) Database. Use Skyhook to quickly get your Redis client applications up and running on an Aerospike cluster.

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

## Installation

### Prerequisites

- Java 8 or later
- Aerospike Server version 4.9+

### Installing

Skyhook is distributed as a jar file which may be downloaded from https://github.com/aerospike/skyhook/releases/latest.

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
format. An example configuration file can be found in the [`config`](https://github.com/aerospike/skyhook/blob/a0199da72222984c8417ccaa6e4a02064ed7224b/config/server.yml) folder of this repository.
If no configuration file is specified, the default settings will be applied.

```text
[main] INFO  c.a.skyhook.SkyhookServer$Companion - Starting the Server...
```

Now the server is listening to the `config.redisPort` (default: 6379) and is ready to serve.

If you wish to deploy Skyhook as a cluster of nodes, you can find some example configurations [here](https://aerospike.github.io/skyhook/scaling-out).

### Configuration Properties

The default behavior may be customized by setting the following properties in the configuration file:

| Property name                                | Description                                                                                                                                               | Default value             |
| -------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------- | ------------------------- |
| hostList                                     | The host list to seed the Aerospike cluster.                                                                                                              | localhost:3000            |
| namespace                                    | The Aerospike namespace.                                                                                                                                  | test                      |
| set                                          | The Aerospike set name.                                                                                                                                   | redis                     |
| clientPolicy                                 | The Aerospike Java client [ClientPolicy](https://docs.aerospike.com/apidocs/java/com/aerospike/client/policy/ClientPolicy.html) configuration properties. | ClientPolicyConfig        |
| bin                                          | The Aerospike value bin name.                                                                                                                             | b                         |
| typeBin                                      | The Aerospike value [type](https://redis.io/topics/data-types) bin name.                                                                                  | t                         |
| redisPort                                    | The server port to bind to.                                                                                                                               | 6379                      |
| unixSocket                                   | The server will bind on unix socket if configured.                                                                                                        |                           |
| workerThreads<sup>[1](#worker-threads)</sup> | The Netty worker group size.                                                                                                                              | number of available cores |
| bossThreads                                  | The Netty acceptor group size.                                                                                                                            | 2                         |

<sup name="worker-threads">1</sup> Used to configure the size of the <a href="https://www.aerospike.com/docs/client/java/usage/async/eventloop.html">Aerospike Java Client EventLoops</a> as well.

## License

Licensed under an Apache 2.0 License.

This is an active open source project. You can contribute to it by trying
Skyhook, providing feedback, reporting bugs, and implementing more Redis
commands.
