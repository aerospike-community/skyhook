package com.aerospike.skyhook.handler.aerospike

import com.aerospike.skyhook.command.RequestCommand
import com.aerospike.skyhook.config.AerospikeContext
import com.aerospike.skyhook.handler.CommandHandler
import com.aerospike.skyhook.handler.NettyResponseWriter
import com.aerospike.skyhook.listener.BaseListener
import io.netty.channel.ChannelHandlerContext

class FlushCommandHandler(
    private val aeroCtx: AerospikeContext,
    private val ctx: ChannelHandlerContext
) : NettyResponseWriter(), CommandHandler {

    override fun handle(cmd: RequestCommand) {
        require(cmd.argCount <= 2) { BaseListener.argValidationErrorMsg(cmd) }

        aeroCtx.client.truncate(null, aeroCtx.namespace, aeroCtx.set, null)
        writeOK(ctx)
        ctx.flush()
    }
}
