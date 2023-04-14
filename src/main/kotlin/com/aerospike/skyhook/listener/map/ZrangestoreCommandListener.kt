package com.aerospike.skyhook.listener.map

import com.aerospike.client.Key
import com.aerospike.client.Record
import com.aerospike.client.Value
import com.aerospike.client.cdt.MapOperation
import com.aerospike.client.cdt.MapPolicy
import com.aerospike.client.cdt.MapReturnType
import com.aerospike.skyhook.command.RequestCommand
import com.aerospike.skyhook.util.Typed
import io.netty.channel.ChannelHandlerContext

class ZrangestoreCommandListener(
    ctx: ChannelHandlerContext
) : ZrangeCommandListener(ctx) {

    override fun handle(cmd: RequestCommand) {
        require(cmd.argCount >= 4) { argValidationErrorMsg(cmd) }

        val destKey = createKey(cmd.key)
        val sourceKey = createKey(cmd.args[2])
        rangeCommand = RangeCommand(cmd, 5)
        validateAndSet()

        val record = client.operate(
            defaultWritePolicy, sourceKey, getMapOperation()
        )

        @Suppress("UNCHECKED_CAST")
        val putOperation = MapOperation.putItems(
            MapPolicy(),
            aeroCtx.bin,
            (record.bins[aeroCtx.bin] as List<Map.Entry<Any, Long>>).associate {
                Typed.getValue(it.key.toString().toByteArray()) to Value.get(it.value)
            }
        )
        client.operate(
            null, this, defaultWritePolicy,
            destKey, putOperation, *systemOps()
        )
    }

    override fun getMapReturnType(): Int {
        return MapReturnType.KEY_VALUE
    }

    override fun onSuccess(key: Key?, record: Record?) {
        try {
            if (record == null) {
                writeLong(0L)
            } else {
                writeLong(record.getLong(aeroCtx.bin))
            }
            flushCtxTransactionAware()
        } catch (e: Exception) {
            closeCtx(e)
        }
    }
}
