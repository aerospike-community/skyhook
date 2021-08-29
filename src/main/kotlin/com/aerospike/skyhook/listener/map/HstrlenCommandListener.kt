package com.aerospike.skyhook.listener.map

import com.aerospike.client.Key
import com.aerospike.client.Record
import com.aerospike.client.Value
import com.aerospike.client.cdt.MapOperation
import com.aerospike.client.cdt.MapReturnType
import com.aerospike.client.listener.RecordListener
import com.aerospike.skyhook.command.RequestCommand
import com.aerospike.skyhook.listener.BaseListener
import com.aerospike.skyhook.listener.key.LengthFetcher
import com.aerospike.skyhook.util.Typed
import io.netty.channel.ChannelHandlerContext

class HstrlenCommandListener(
    ctx: ChannelHandlerContext
) : BaseListener(ctx), RecordListener, LengthFetcher {

    override fun handle(cmd: RequestCommand) {
        require(cmd.argCount == 3) { argValidationErrorMsg(cmd) }

        val key = createKey(cmd.key)
        val mapKey: Value = Typed.getValue(cmd.args[2])
        val operation = MapOperation.getByKey(
            aeroCtx.bin, mapKey,
            MapReturnType.VALUE
        )
        client.operate(
            null, this, defaultWritePolicy, key, operation
        )
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
