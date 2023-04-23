package com.aerospike.skyhook.listener.hyperlog

import com.aerospike.client.Key
import com.aerospike.client.Record
import com.aerospike.client.listener.RecordListener
import com.aerospike.client.operation.HLLOperation
import com.aerospike.client.operation.HLLPolicy
import com.aerospike.skyhook.command.RequestCommand
import com.aerospike.skyhook.listener.BaseListener
import com.aerospike.skyhook.util.Typed
import io.netty.channel.ChannelHandlerContext

open class PfaddListener(
    ctx: ChannelHandlerContext
) : BaseListener(ctx), RecordListener {

    override fun handle(cmd: RequestCommand) {
        require(cmd.argCount > 2) { argValidationErrorMsg(cmd) }

        val key = createKey(cmd.key)

        val operation = HLLOperation.add(
            HLLPolicy.Default,
            aeroCtx.bin,
            getValues(cmd),
            16
        )
        client.operate(null, this, defaultWritePolicy, key, operation)
    }

    protected open fun getValues(cmd: RequestCommand) =
        cmd.args.drop(2).map { Typed.getValue(it) }

    override fun onSuccess(key: Key?, record: Record?) {
        if (record == null) {
            writeNullString()
            flushCtxTransactionAware()
        } else {
            try {
                val entitiesWritten = record.bins[aeroCtx.bin] as Long
                writeLong(if (entitiesWritten > 0) 1L else 0L)
                flushCtxTransactionAware()
            } catch (e: Exception) {
                closeCtx(e)
            }
        }
    }
}
