package com.aerospike.skyhook.handler.redis

import com.aerospike.skyhook.command.RequestCommand
import com.aerospike.skyhook.handler.CommandHandler
import com.aerospike.skyhook.handler.NettyResponseWriter
import com.aerospike.skyhook.listener.BaseListener
import io.netty.channel.ChannelHandlerContext

class PingCommandHandler(
    private val ctx: ChannelHandlerContext
) : NettyResponseWriter(), CommandHandler {

    override fun handle(cmd: RequestCommand) {
        require(cmd.argCount < 3) { BaseListener.argValidationErrorMsg(cmd) }

        val responseString = if (cmd.argCount == 2) {
            String(cmd.args[1])
        } else {
            "PONG"
        }

        writeSimpleString(ctx, responseString)
        ctx.flush()
    }
}
