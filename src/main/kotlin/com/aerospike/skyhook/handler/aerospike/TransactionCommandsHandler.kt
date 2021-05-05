package com.aerospike.skyhook.handler.aerospike

import com.aerospike.skyhook.command.RequestCommand
import com.aerospike.skyhook.handler.CommandHandler
import com.aerospike.skyhook.handler.NettyResponseWriter
import com.aerospike.skyhook.listener.BaseListener.Companion.argValidationErrorMsg
import com.aerospike.skyhook.pipeline.AerospikeChannelInitializer.Companion.transactionAttrKey
import com.aerospike.skyhook.util.wait
import io.netty.channel.ChannelHandlerContext

class MultiCommandHandler(
    ctx: ChannelHandlerContext,
) : NettyResponseWriter(ctx), CommandHandler {

    override fun handle(cmd: RequestCommand) {
        require(cmd.argCount == 1) { argValidationErrorMsg(cmd) }

        ctx.channel().attr(transactionAttrKey).get().startTransaction()
        writeOK()
        flushCtx()
    }
}

class DiscardCommandHandler(
    ctx: ChannelHandlerContext,
) : NettyResponseWriter(ctx), CommandHandler {

    override fun handle(cmd: RequestCommand) {
        require(cmd.argCount == 1) { argValidationErrorMsg(cmd) }

        ctx.channel().attr(transactionAttrKey).get().clear()
        writeOK()
        flushCtx()
    }
}

class ExecCommandHandler(
    ctx: ChannelHandlerContext,
) : NettyResponseWriter(ctx), CommandHandler {

    override fun handle(cmd: RequestCommand) {
        require(cmd.argCount == 1) { argValidationErrorMsg(cmd) }

        val state = ctx.channel().attr(transactionAttrKey).get()
        if (state.inTransaction) {
            if (state.commands.isEmpty()) {
                writeEmptyList()
            } else {
                try {
                    writeArrayHeader(state.commands.size.toLong())
                    for (c in state.commands) {
                        state.pool.submit { c.command.newHandler(ctx).handle(c) }
                        synchronized(ctx) { ctx.wait(5000) }
                    }
                } catch (e: Exception) {
                    writeErrorString("ERR Transaction failed")
                }
                state.clear()
            }
        } else {
            writeErrorString("ERR EXEC without MULTI")
        }
        flushCtx()
    }
}
