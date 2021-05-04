package com.aerospike.skyhook.listener.list

import com.aerospike.client.Key
import com.aerospike.client.Record
import com.aerospike.client.cdt.ListOperation
import com.aerospike.client.cdt.ListReturnType
import com.aerospike.client.listener.RecordListener
import com.aerospike.skyhook.command.RequestCommand
import com.aerospike.skyhook.listener.BaseListener
import com.aerospike.skyhook.util.Typed
import io.netty.channel.ChannelHandlerContext

class LrangeCommandListener(
    ctx: ChannelHandlerContext
) : BaseListener(ctx), RecordListener {

    override fun handle(cmd: RequestCommand) {
        require(cmd.argCount == 4) { argValidationErrorMsg(cmd) }

        val key = createKey(cmd.key)
        val from = Typed.getInteger(cmd.args[2])
        val to = Typed.getInteger(cmd.args[3])

        // TODO support negative indexes
        require(from >= 0 && to >= 0) { "${cmd.command} negative index" }
        require(from <= to) { "${cmd.command} invalid indexes" }
        val count = (to - from) + 1

        val operation = ListOperation.getByIndexRange(
            aeroCtx.bin, from,
            count, ListReturnType.VALUE
        )
        client.operate(
            null, this, defaultWritePolicy,
            key, operation
        )
    }

    override fun onSuccess(key: Key?, record: Record?) {
        if (record == null) {
            writeEmptyList()
            flushCtxTransactionAware()
        } else {
            try {
                writeResponse(record.bins[aeroCtx.bin])
                flushCtxTransactionAware()
            } catch (e: Exception) {
                closeCtx(e)
            }
        }
    }
}
