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
) : NettyResponseWriter() {

    fun write(ctx: ChannelHandlerContext) {
        writeArrayHeader(ctx, 6)
        writeSimpleString(ctx, commandName)
        writeLong(ctx, commandArity)
        writeObjectListStr(ctx, commandFlags)
        writeLong(ctx, firstKeyPosition)
        writeLong(ctx, lastKeyPosition)
        writeLong(ctx, stepCount)
    }
}
