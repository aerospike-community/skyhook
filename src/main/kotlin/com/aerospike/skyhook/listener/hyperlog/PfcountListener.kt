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

        if (cmd.args.size == 2) {
            countSingleKey(createKey(cmd.args[1]))
        } else {
            val keys = cmd.args.drop(1).map(::createKey)
            countMultipleKeys(keys)
        }
    }

    private fun countSingleKey(key: Key) {
        val operation = HLLOperation.getCount(aeroCtx.bin)
        client.operate(null, this, null, key, operation)
    }

    private fun countMultipleKeys(
        keys: List<Key>,
    ) {
        val hllValues = keys
            .mapNotNull { client.get(null, it)?.getHLLValue(aeroCtx.bin) } //TODO: make async

        val operation = HLLOperation.getUnionCount(aeroCtx.bin, hllValues)
        client.operate(null, this, null, keys[1], operation)
    }

    override fun onSuccess(key: Key?, record: Record?) {
        if (record == null) {
            writeLong(0L)
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
