package com.aerospike.skyhook.listener.map

import com.aerospike.client.AerospikeException
import com.aerospike.client.Key
import com.aerospike.client.Record
import com.aerospike.client.Value
import com.aerospike.client.cdt.MapOperation
import com.aerospike.client.cdt.MapOrder
import com.aerospike.client.cdt.MapPolicy
import com.aerospike.client.cdt.MapWriteFlags
import com.aerospike.client.listener.RecordListener
import com.aerospike.skyhook.command.RequestCommand
import com.aerospike.skyhook.config.AerospikeContext
import com.aerospike.skyhook.listener.BaseListener
import com.aerospike.skyhook.util.Typed
import io.netty.channel.ChannelHandlerContext

class SaddCommandListener(
    aeroCtx: AerospikeContext,
    ctx: ChannelHandlerContext
) : BaseListener(aeroCtx, ctx), RecordListener {

    @Volatile
    private var size: Long = 0L

    override fun handle(cmd: RequestCommand) {
        require(cmd.argCount >= 3) { argValidationErrorMsg(cmd) }

        val key = createKey(cmd.key)

        val getSize = MapOperation.size(aeroCtx.bin)
        size = aeroCtx.client.operate(defaultWritePolicy, key, getSize)
            ?.getLong(aeroCtx.bin) ?: 0L

        val operation = MapOperation.putItems(
            MapPolicy(MapOrder.UNORDERED, MapWriteFlags.CREATE_ONLY),
            aeroCtx.bin,
            getValues(cmd)
        )
        aeroCtx.client.operate(
            null, this, defaultWritePolicy,
            key, setTypeOp(), operation
        )
    }

    private fun getValues(cmd: RequestCommand): Map<Value, Value> {
        return cmd.args.drop(2)
            .map { Typed.getValue(it) to Value.getAsNull() }
            .toMap()
    }

    override fun writeError(e: AerospikeException?) {
        writeLong(ctx, 0L)
    }

    override fun onSuccess(key: Key?, record: Record?) {
        if (record == null) {
            writeLong(ctx, 0L)
            ctx.flush()
        } else {
            try {
                val added = record.getLong(aeroCtx.bin) - size
                writeLong(ctx, added)
                ctx.flush()
            } catch (e: Exception) {
                closeCtx(e)
            }
        }
    }
}
