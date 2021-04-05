package com.aerospike.redispike.handler.aerospike

import com.aerospike.redispike.command.RequestCommand
import com.aerospike.redispike.config.AerospikeContext
import com.aerospike.redispike.handler.CommandHandler
import com.aerospike.redispike.handler.NettyResponseWriter
import com.aerospike.redispike.listener.BaseListener
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
