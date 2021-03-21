package com.aerospike.redispike.config

data class ServerConfiguration(

    /**
     * The hostList to seed the Aerospike cluster.
     */
    val hostList: String = "localhost:3000",

    /**
     * The Aerospike namespace.
     */
    val namespase: String = "test",

    /**
     * The Aerospike set name.
     */
    val set: String? = "redis",

    /**
     * The bin name to set values.
     */
    val bin: String = "b",

    /**
     * The Redis port to bind to.
     */
    val redisPort: Int = 6379,

    /**
     * The Netty worker group size.
     */
    val workerThreads: Int = Runtime.getRuntime().availableProcessors(),

    /**
     * The Netty boss group size.
     */
    val bossThreads: Int = 2,
)
