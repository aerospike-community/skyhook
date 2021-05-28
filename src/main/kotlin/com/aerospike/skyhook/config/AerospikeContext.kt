package com.aerospike.skyhook.config

data class AerospikeContext(

    /**
     * The Aerospike namespace to map to.
     */
    val namespace: String,

    /**
     * The Aerospike set name to map to.
     */
    val set: String?,

    /**
     * The Aerospike bin name to set values.
     */
    val bin: String,

    /**
     * The Aerospike bin name to set value type.
     */
    val typeBin: String,

    /**
     * The Aerospike transaction id bin name.
     */
    val transactionIdBin: String,
)
