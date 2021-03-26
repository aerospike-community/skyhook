package com.aerospike.redispike.listener.list

import com.aerospike.client.Key
import com.aerospike.client.Record
import com.aerospike.client.cdt.ListOperation
import com.aerospike.client.cdt.ListReturnType
import com.aerospike.client.listener.RecordListener
import com.aerospike.redispike.command.RequestCommand
import com.aerospike.redispike.config.AerospikeContext
import com.aerospike.redispike.listener.BaseListener
import com.aerospike.redispike.util.Typed
import io.netty.channel.ChannelHandlerContext

class LrangeCommandListener(
    aeroCtx: AerospikeContext,
    ctx: ChannelHandlerContext
) : BaseListener(aeroCtx, ctx), RecordListener {

    override fun handle(cmd: RequestCommand) {
        require(cmd.argCount == 4) { argValidationErrorMsg(cmd) }

        val key = createKey(cmd.key)
        val from = Typed.getInteger(cmd.args!![2])
        val to = Typed.getInteger(cmd.args[3])

        // TODO support negative indexes
        require(from >= 0 && to >= 0) { "${cmd.command} negative index" }
        require(from <= to) { "${cmd.command} invalid indexes" }
        val count = (to - from) + 1

        val operation = ListOperation.getByIndexRange(
            aeroCtx.bin, from,
            count, ListReturnType.VALUE
        )
        aeroCtx.client.operate(
            null, this, defaultWritePolicy,
            key, operation
        )
    }

    override fun onSuccess(key: Key?, record: Record?) {
        if (record == null) {
            writeEmptyList(ctx)
            ctx.flush()
        } else {
            try {
                writeResponse(record.bins[aeroCtx.bin])
                ctx.flush()
            } catch (e: Exception) {
                closeCtx(e)
            }
        }
    }
}
