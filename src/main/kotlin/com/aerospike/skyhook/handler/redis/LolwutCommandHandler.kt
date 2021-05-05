package com.aerospike.skyhook.handler.redis

import com.aerospike.skyhook.command.RequestCommand
import com.aerospike.skyhook.handler.CommandHandler
import com.aerospike.skyhook.handler.NettyResponseWriter
import com.aerospike.skyhook.listener.BaseListener
import com.aerospike.skyhook.util.SystemUtils
import io.netty.channel.ChannelHandlerContext

class LolwutCommandHandler(
    ctx: ChannelHandlerContext
) : NettyResponseWriter(ctx), CommandHandler {

    override fun handle(cmd: RequestCommand) {
        require(cmd.argCount <= 3) { BaseListener.argValidationErrorMsg(cmd) }

        writeSimpleString("Skyhook ver. ${SystemUtils.version}\n")
        flushCtxTransactionAware()
    }
}
