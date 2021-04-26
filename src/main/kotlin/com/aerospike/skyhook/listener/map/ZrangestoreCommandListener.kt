package com.aerospike.skyhook.listener.map

import com.aerospike.client.Key
import com.aerospike.client.Record
import com.aerospike.client.Value
import com.aerospike.client.cdt.MapOperation
import com.aerospike.client.cdt.MapPolicy
import com.aerospike.client.cdt.MapReturnType
import com.aerospike.skyhook.command.RequestCommand
import com.aerospike.skyhook.config.AerospikeContext
import com.aerospike.skyhook.util.Typed
import io.netty.channel.ChannelHandlerContext

class ZrangestoreCommandListener(
    aeroCtx: AerospikeContext,
    ctx: ChannelHandlerContext
) : ZrangeCommandListener(aeroCtx, ctx) {

    override fun handle(cmd: RequestCommand) {
        require(cmd.argCount >= 4) { argValidationErrorMsg(cmd) }

        val destKey = createKey(cmd.key)
        val sourceKey = createKey(Value.get(cmd.args[2]))
        rangeCommand = RangeCommand(cmd, 5)
        validateRangeCommand()

        val record = aeroCtx.client.operate(
            defaultWritePolicy, sourceKey, getMapOperation()
        )

        @Suppress("UNCHECKED_CAST")
        val putOperation = MapOperation.putItems(
            MapPolicy(),
            aeroCtx.bin,
            (record.bins[aeroCtx.bin] as List<Map.Entry<Any, Long>>).map {
                Typed.getValue(it.key.toString().toByteArray()) to
                        Value.get(it.value)
            }.toMap()
        )
        aeroCtx.client.operate(
            null, this, defaultWritePolicy, destKey, putOperation
        )
    }

    override fun getMapReturnType(): Int {
        return MapReturnType.KEY_VALUE
    }

    override fun onSuccess(key: Key?, record: Record?) {
        try {
            if (record == null) {
                writeLong(ctx, 0L)
            } else {
                writeLong(ctx, record.getLong(aeroCtx.bin))
            }
            ctx.flush()
        } catch (e: Exception) {
            closeCtx(e)
        }
    }
}
