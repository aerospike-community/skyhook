package com.aerospike.skyhook.command

import com.aerospike.client.Value

data class RequestCommand(
    val args: List<ByteArray>
) {

    val argCount: Int = args.size

    val key: Value by lazy {
        Value.get(String(args[1]))
    }

    val command: RedisCommand =
        RedisCommand.getValue(String(args[0]))

    val transactional: Boolean by lazy {
        command == RedisCommand.MULTI ||
                command == RedisCommand.EXEC ||
                command == RedisCommand.DISCARD
    }
}
