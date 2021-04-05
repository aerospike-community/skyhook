package com.aerospike.redispike.command

import com.aerospike.client.Value

data class RequestCommand(
    val args: List<ByteArray>
) {

    val argCount: Int = args.size

    val key: Value by lazy {
        Value.get(args[1])
    }

    val command: RedisCommand by lazy {
        RedisCommand.getValue(String(args[0]))
    }
}
