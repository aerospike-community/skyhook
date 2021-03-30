package com.aerospike.redispike.handler.redis

import com.aerospike.redispike.command.RequestCommand
import com.aerospike.redispike.handler.CommandHandler
import com.aerospike.redispike.handler.NettyResponseWriter
import com.aerospike.redispike.listener.BaseListener
import io.netty.channel.ChannelHandlerContext
import java.time.Instant
import java.util.concurrent.TimeUnit

class TimeCommandHandler(
    private val ctx: ChannelHandlerContext
) : NettyResponseWriter(), CommandHandler {

    override fun handle(cmd: RequestCommand) {
        require(cmd.argCount == 1) { BaseListener.argValidationErrorMsg(cmd) }

        val now = Instant.now()
        val seconds = now.epochSecond
        val microseconds = TimeUnit.NANOSECONDS.toMicros(now.nano.toLong())

        writeObjectListStr(ctx, arrayListOf(seconds, microseconds))
        ctx.flush()
    }
}
