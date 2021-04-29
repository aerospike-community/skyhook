package com.aerospike.skyhook.handler.redis

import com.aerospike.skyhook.command.RequestCommand
import com.aerospike.skyhook.handler.CommandHandler
import com.aerospike.skyhook.handler.NettyResponseWriter
import com.aerospike.skyhook.listener.BaseListener
import com.aerospike.skyhook.util.SystemUtils
import io.netty.channel.ChannelHandlerContext

class LolwutCommandHandler(
    private val ctx: ChannelHandlerContext
) : NettyResponseWriter(), CommandHandler {

    override fun handle(cmd: RequestCommand) {
        require(cmd.argCount <= 3) { BaseListener.argValidationErrorMsg(cmd) }

        writeSimpleString(ctx, "Skyhook ver. ${SystemUtils.version}\n")
        ctx.flush()
    }
}
