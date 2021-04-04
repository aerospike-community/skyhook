package com.aerospike.redispike.command

import com.aerospike.client.Value

data class RequestCommand(
    val args: MutableList<ByteArray> = mutableListOf(),
    var argCount: Int = 0,
) {

    constructor(args: MutableList<ByteArray>) :
            this(args, args.size)

    fun addArgument(arg: ByteArray) {
        args.add(arg)
        argCount++
    }

    val key: Value by lazy {
        Value.get(args[1])
    }

    val command: RedisCommand by lazy {
        RedisCommand.getValue(String(args[0]))
    }
}
