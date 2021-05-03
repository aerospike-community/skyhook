package com.aerospike.skyhook.handler.redis

import com.aerospike.skyhook.command.RequestCommand
import com.aerospike.skyhook.handler.CommandHandler
import com.aerospike.skyhook.handler.NettyResponseWriter
import com.aerospike.skyhook.listener.BaseListener
import com.aerospike.skyhook.pipeline.AerospikeChannelInitializer.Companion.authDetailsAttrKey
import com.aerospike.skyhook.pipeline.AerospikeChannelInitializer.Companion.clientPoolAttrKey
import com.aerospike.skyhook.util.client.AuthDetails
import io.netty.channel.ChannelHandlerContext

class AuthCommandHandler(
    private val ctx: ChannelHandlerContext,
) : NettyResponseWriter(), CommandHandler {

    override fun handle(cmd: RequestCommand) {
        require(cmd.argCount == 3) { BaseListener.argValidationErrorMsg(cmd) }

        val user = String(cmd.args[1])
        val password = String(cmd.args[2])
        val authDetails = AuthDetails(user, password)

        val client = ctx.channel().attr(clientPoolAttrKey).get().getClient(authDetails)
        if (client != null) {
            ctx.channel().attr(authDetailsAttrKey).set(authDetails.hashString)
            writeOK(ctx)
        } else {
            writeErrorString(ctx, "Invalid AUTH details")
        }
        ctx.flush()
    }
}
