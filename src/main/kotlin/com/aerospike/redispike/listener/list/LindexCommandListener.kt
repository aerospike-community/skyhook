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

class LindexCommandListener(
    aeroCtx: AerospikeContext,
    ctx: ChannelHandlerContext
) : BaseListener(aeroCtx, ctx), RecordListener {

    override fun handle(cmd: RequestCommand) {
        require(cmd.argCount == 3) { argValidationErrorMsg(cmd) }

        val key = createKey(cmd.key)
        val index = Typed.getInteger(cmd.args[2])

        val operation = ListOperation.getByIndex(
            aeroCtx.bin, index,
            ListReturnType.VALUE
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
