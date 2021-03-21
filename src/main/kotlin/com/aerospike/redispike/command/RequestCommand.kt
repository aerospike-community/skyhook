package com.aerospike.redispike.command

import java.util.*

data class RequestCommand(
    val args: MutableList<ByteArray>? = null,
    var argCount: Int = 0,
) {

    constructor(args: MutableList<ByteArray>) :
            this(args, args.size)

    fun addArgument(arg: ByteArray) {
        args?.run { this.add(arg) }
        argCount++
    }

    @Throws(IllegalArgumentException::class)
    fun getCommand(): RedisCommand {
        return RedisCommand.valueOf(String(args!![0]).toUpperCase(Locale.ENGLISH))
    }
}
