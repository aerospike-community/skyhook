package com.aerospike.redispike.config

import com.aerospike.client.IAerospikeClient

data class AerospikeContext(

    /**
     * The Aerospike client instance
     */
    val client: IAerospikeClient,

    /**
     * The namespace name to proxy to
     */
    val namespace: String,

    /**
     * The set name to proxy to
     */
    val set: String?,

    /**
     * The bin name to set values
     */
    val bin: String,
)
