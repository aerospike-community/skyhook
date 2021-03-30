package com.aerospike.redispike.listener.key

import com.aerospike.client.Key
import com.aerospike.client.Record
import com.aerospike.client.listener.RecordListener
import com.aerospike.redispike.command.RequestCommand
import com.aerospike.redispike.config.AerospikeContext
import com.aerospike.redispike.listener.BaseListener
import io.netty.channel.ChannelHandlerContext

class StrlenCommandListener(
    aeroCtx: AerospikeContext,
    ctx: ChannelHandlerContext
) : BaseListener(aeroCtx, ctx), RecordListener {

    override fun handle(cmd: RequestCommand) {
        require(cmd.argCount == 2) { argValidationErrorMsg(cmd) }

        val key = createKey(cmd.key)
        aeroCtx.client.get(null, this, null, key)
    }

    override fun onSuccess(key: Key?, record: Record?) {
        if (record == null) {
            writeLong(ctx, 0L)
            ctx.flush()
        } else {
            try {
                writeResponse(getValueStrLen(record.bins[aeroCtx.bin]))
                ctx.flush()
            } catch (e: Exception) {
                closeCtx(e)
            }
        }
    }

    private fun getValueStrLen(value: Any?): Long {
        if (value == null) {
            return 0
        }
        if (value is String) {
            return value.length.toLong()
        }
        if (value is ByteArray) {
            return value.size.toLong()
        }
        if (value is Long) {
            return value.toString().length.toLong()
        }
        return if (value is Double) {
            ((value as Double?)!!).toString().length.toLong()
        } else 0
    }
}
