package com.aerospike.skyhook.handler.aerospike

import com.aerospike.skyhook.command.RequestCommand
import com.aerospike.skyhook.handler.CommandHandler
import com.aerospike.skyhook.listener.BaseListener
import io.netty.channel.ChannelHandlerContext

class FlushCommandHandler(
    ctx: ChannelHandlerContext
) : BaseListener(ctx), CommandHandler {

    override fun handle(cmd: RequestCommand) {
        require(cmd.argCount <= 2) { argValidationErrorMsg(cmd) }

        client.truncate(null, aeroCtx.namespace, aeroCtx.set, null)
        writeOK(ctx)
        ctx.flush()
    }
}
