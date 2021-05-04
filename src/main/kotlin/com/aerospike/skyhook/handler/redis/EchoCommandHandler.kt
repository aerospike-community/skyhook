package com.aerospike.skyhook.handler.redis

import com.aerospike.skyhook.command.RequestCommand
import com.aerospike.skyhook.handler.CommandHandler
import com.aerospike.skyhook.handler.NettyResponseWriter
import com.aerospike.skyhook.listener.BaseListener
import io.netty.channel.ChannelHandlerContext

class EchoCommandHandler(
    ctx: ChannelHandlerContext
) : NettyResponseWriter(ctx), CommandHandler {

    override fun handle(cmd: RequestCommand) {
        require(cmd.argCount == 2) { BaseListener.argValidationErrorMsg(cmd) }

        writeSimpleString(String(cmd.args[1]))
        flushCtxTransactionAware()
    }
}
