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
            val key = createKey(cmd.args[1])
            countSingleKey(key)
        } else {
            val keys = cmd.args.drop(1).map(::createKey)
            countMultipleKeys(keys)
        }
    }

    private fun countSingleKey(key: Key) {
        val operation = HLLOperation.getCount(aeroCtx.bin)
        client.operate(null, this, defaultWritePolicy, key, operation)
    }

    private fun countMultipleKeys(keys: List<Key>) {
        val hllValuesByKey = keys
            .associateWith { client.get(null, it) }
            .filterValues { it != null }
            .mapValues { it.value.getHLLValue(aeroCtx.bin) }

        if (hllValuesByKey.isEmpty()) {
            writeZero()
            return
        }

        val operation = HLLOperation.getUnionCount(aeroCtx.bin, hllValuesByKey.values.toList())

        client.operate(null, this, defaultWritePolicy, hllValuesByKey.keys.first(), operation)
    }

    override fun onSuccess(key: Key?, record: Record?) {
        if (record == null) {
            writeZero()
        } else {
            try {
                writeLong(record.bins[aeroCtx.bin] as Long)
                flushCtxTransactionAware()
            } catch (e: Exception) {
                closeCtx(e)
            }
        }
    }

    private fun writeZero() {
        writeLong(0L)
        flushCtxTransactionAware()
    }
}
