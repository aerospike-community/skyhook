package com.aerospike.skyhook.handler.aerospike

import com.aerospike.client.AerospikeException
import com.aerospike.client.Bin
import com.aerospike.client.Value
import com.aerospike.client.exp.Exp
import com.aerospike.client.policy.WritePolicy
import com.aerospike.skyhook.command.RequestCommand
import com.aerospike.skyhook.handler.CommandHandler
import com.aerospike.skyhook.handler.NettyResponseWriter
import com.aerospike.skyhook.listener.BaseListener
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
) : BaseListener(ctx), CommandHandler {

    override fun handle(cmd: RequestCommand) {
        require(cmd.argCount == 1) { argValidationErrorMsg(cmd) }

        if (transactionState.inTransaction) {
            if (transactionState.commands.isEmpty()) {
                writeEmptyList()
            } else {
                try {
                    writeArrayHeader(transactionState.commands.size.toLong())
                    for (c in transactionState.commands) {
                        transactionState.pool.submit { c.command.newHandler(ctx).handle(c) }
                        synchronized(ctx) { ctx.wait(5000) }
                    }
                } catch (e: Exception) {
                    writeErrorString("ERR Transaction failed")
                } finally {
                    val writePolicy = transactionClearPolicy(transactionState.transactionId)
                    val tidBin = Bin(aeroCtx.transactionIdBin, Value.NULL)
                    for (key in transactionState.keys) {
                        try {
                            client.put(writePolicy, key, tidBin)
                        } catch (e: AerospikeException) {
                            log.warn { "Exception on clear the transaction id ${transactionState.transactionId}" }
                        }
                    }
                }
                transactionState.clear()
            }
        } else {
            writeErrorString("ERR EXEC without MULTI")
        }
        flushCtx()
    }

    private fun transactionClearPolicy(transactionId: String?): WritePolicy {
        val writePolicy = getWritePolicy()
        writePolicy.filterExp = Exp.build(
            Exp.and(
                Exp.binExists(aeroCtx.transactionIdBin),
                Exp.eq(
                    Exp.bin(aeroCtx.transactionIdBin, Exp.Type.STRING),
                    Exp.`val`(transactionId)
                )
            )
        )
        return writePolicy
    }
}
