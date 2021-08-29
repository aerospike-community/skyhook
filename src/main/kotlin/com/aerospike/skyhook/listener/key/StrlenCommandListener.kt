package com.aerospike.skyhook.listener.key

import com.aerospike.client.Key
import com.aerospike.client.Record
import com.aerospike.client.listener.RecordListener
import com.aerospike.skyhook.command.RequestCommand
import com.aerospike.skyhook.listener.BaseListener
import io.netty.channel.ChannelHandlerContext

class StrlenCommandListener(
    ctx: ChannelHandlerContext
) : BaseListener(ctx), RecordListener, LengthFetcher {

    override fun handle(cmd: RequestCommand) {
        require(cmd.argCount == 2) { argValidationErrorMsg(cmd) }

        val key = createKey(cmd.key)
        client.get(null, this, defaultWritePolicy, key)
    }

    override fun onSuccess(key: Key?, record: Record?) {
        if (record == null) {
            writeLong(0L)
            flushCtxTransactionAware()
        } else {
            try {
                writeResponse(valueLength(record.bins[aeroCtx.bin]))
                flushCtxTransactionAware()
            } catch (e: Exception) {
                closeCtx(e)
            }
        }
    }
}

interface LengthFetcher {

    fun valueLength(value: Any?): Long {
        return when (value) {
            is String -> value.length.toLong()
            is ByteArray -> value.size.toLong()
            null -> 0L
            else -> value.toString().length.toLong()
        }
    }
}
