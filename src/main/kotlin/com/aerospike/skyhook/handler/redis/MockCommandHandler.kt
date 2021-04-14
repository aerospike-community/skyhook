package com.aerospike.skyhook.handler.redis

import com.aerospike.skyhook.command.RedisCommand
import com.aerospike.skyhook.command.RequestCommand
import com.aerospike.skyhook.handler.CommandHandler
import com.aerospike.skyhook.handler.NettyResponseWriter
import com.aerospike.skyhook.listener.BaseListener
import io.netty.channel.ChannelHandlerContext

class MockCommandHandler(
    private val ctx: ChannelHandlerContext
) : NettyResponseWriter(), CommandHandler {

    override fun handle(cmd: RequestCommand) {
        require(cmd.argCount >= 1) { BaseListener.argValidationErrorMsg(cmd) }

        when (cmd.command) {
            RedisCommand.RESET -> writeSimpleString(ctx, "RESET")
            else -> writeOK(ctx)
        }
        ctx.flush()
    }
}
