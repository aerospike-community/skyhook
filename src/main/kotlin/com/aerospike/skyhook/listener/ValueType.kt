package com.aerospike.skyhook.listener

/**
 * Redis supported value types.
 */
enum class ValueType(val str: String) {
    STRING("string"),
    LIST("list"),
    SET("set"),
    ZSET("zset"),
    HASH("hash"),
    STREAM("stream")
}
