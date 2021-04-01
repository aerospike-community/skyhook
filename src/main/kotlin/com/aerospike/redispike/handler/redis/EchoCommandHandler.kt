package com.aerospike.redispike.handler.redis

import com.aerospike.redispike.command.RequestCommand
import com.aerospike.redispike.handler.CommandHandler
import com.aerospike.redispike.handler.NettyResponseWriter
import com.aerospike.redispike.listener.BaseListener
import io.netty.channel.ChannelHandlerContext

class EchoCommandHandler(
    private val ctx: ChannelHandlerContext
) : NettyResponseWriter(), CommandHandler {

    override fun handle(cmd: RequestCommand) {
        require(cmd.argCount == 2) { BaseListener.argValidationErrorMsg(cmd) }

        writeSimpleString(ctx, String(cmd.args!![1]))
        ctx.flush()
    }
}
