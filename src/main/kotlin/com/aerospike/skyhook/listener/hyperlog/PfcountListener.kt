package com.aerospike.skyhook.listener.hyperlog

import com.aerospike.client.Key
import com.aerospike.client.Record
import com.aerospike.client.listener.RecordListener
import com.aerospike.client.operation.HLLOperation
import com.aerospike.skyhook.command.RequestCommand
import com.aerospike.skyhook.listener.BaseListener
import io.netty.channel.ChannelHandlerContext

class PfcountListener(
    ctx: ChannelHandlerContext
) : BaseListener(ctx), RecordListener {

    override fun handle(cmd: RequestCommand) {
        require(cmd.argCount > 1) { argValidationErrorMsg(cmd) }

        val hllValues = cmd.args.drop(1)
            .map(::createKey)
            .map { client.get(null, it).getHLLValue(aeroCtx.bin) }

        val operation = HLLOperation.getUnionCount(aeroCtx.bin, hllValues)
        client.operate(null, this, null, createKey(cmd.args[1]), operation)
    }

    override fun onSuccess(key: Key?, record: Record?) {
        if (record == null) {
            writeNullString()
            flushCtxTransactionAware()
        } else {
            try {
                writeLong(record.bins[aeroCtx.bin] as Long)
                flushCtxTransactionAware()
            } catch (e: Exception) {
                closeCtx(e)
            }
        }
    }
}
