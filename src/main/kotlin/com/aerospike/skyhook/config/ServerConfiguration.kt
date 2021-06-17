package com.aerospike.skyhook.config

data class ServerConfiguration(

    /**
     * The host list to seed the Aerospike cluster.
     */
    val hostList: String = "localhost:3000",

    /**
     * The Aerospike namespace.
     */
    val namespace: String = "test",

    /**
     * The Aerospike set name.
     */
    val set: String? = "redis",

    /**
     * Aerospike Client Policy configuration properties.
     */
    val clientPolicy: ClientPolicyConfig = ClientPolicyConfig(),

    /**
     * The Aerospike bin name to set values.
     */
    val bin: String = "b",

    /**
     * The Aerospike bin name to set value type.
     */
    val typeBin: String = "t",

    /**
     * The Aerospike transaction id bin name.
     */
    val transactionIdBin: String = "tid",

    /**
     * The server port to bind to.
     */
    val redisPort: Int = 6379,

    /**
     * The Netty worker group size.
     */
    val workerThreads: Int = Runtime.getRuntime().availableProcessors(),

    /**
     * The Netty acceptor group size.
     */
    val bossThreads: Int = 2,
)
