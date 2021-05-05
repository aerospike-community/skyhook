package com.aerospike.skyhook.handler.redis

import com.aerospike.skyhook.command.RequestCommand
import com.aerospike.skyhook.handler.CommandHandler
import com.aerospike.skyhook.handler.NettyResponseWriter
import com.aerospike.skyhook.listener.BaseListener
import io.netty.channel.ChannelHandlerContext
import java.time.Instant
import java.util.concurrent.TimeUnit

class TimeCommandHandler(
    ctx: ChannelHandlerContext
) : NettyResponseWriter(ctx), CommandHandler {

    override fun handle(cmd: RequestCommand) {
        require(cmd.argCount == 1) { BaseListener.argValidationErrorMsg(cmd) }

        val now = Instant.now()
        val seconds = now.epochSecond
        val microseconds = TimeUnit.NANOSECONDS.toMicros(now.nano.toLong())

        writeObjectListStr(arrayListOf(seconds, microseconds))
        flushCtxTransactionAware()
    }
}
