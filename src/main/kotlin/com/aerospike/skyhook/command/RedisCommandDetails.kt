package com.aerospike.skyhook.command

import com.aerospike.skyhook.handler.NettyResponseWriter
import io.netty.channel.ChannelHandlerContext

data class RedisCommandDetails(
    val commandName: String,
    val commandArity: Int,
    val commandFlags: List<String>,
    val firstKeyPosition: Int,
    val lastKeyPosition: Int,
    val stepCount: Int
) {

    fun write(ctx: ChannelHandlerContext) {
        val nrw = NettyResponseWriter(ctx)
        nrw.writeArrayHeader(6)
        nrw.writeSimpleString(commandName)
        nrw.writeLong(commandArity)
        nrw.writeObjectListStr(commandFlags)
        nrw.writeLong(firstKeyPosition)
        nrw.writeLong(lastKeyPosition)
        nrw.writeLong(stepCount)
    }
}
