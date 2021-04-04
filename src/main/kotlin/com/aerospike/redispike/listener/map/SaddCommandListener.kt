package com.aerospike.redispike.listener.map

import com.aerospike.client.AerospikeException
import com.aerospike.client.Key
import com.aerospike.client.Record
import com.aerospike.client.Value
import com.aerospike.client.cdt.MapOperation
import com.aerospike.client.cdt.MapOrder
import com.aerospike.client.cdt.MapPolicy
import com.aerospike.client.cdt.MapWriteFlags
import com.aerospike.client.listener.RecordListener
import com.aerospike.redispike.command.RequestCommand
import com.aerospike.redispike.config.AerospikeContext
import com.aerospike.redispike.listener.BaseListener
import com.aerospike.redispike.util.Typed
import io.netty.channel.ChannelHandlerContext

class SaddCommandListener(
    aeroCtx: AerospikeContext,
    ctx: ChannelHandlerContext
) : BaseListener(aeroCtx, ctx), RecordListener {

    override fun handle(cmd: RequestCommand) {
        require(cmd.argCount >= 3) { argValidationErrorMsg(cmd) }

        val key = createKey(cmd.key)
        val operation = MapOperation.putItems(
            MapPolicy(MapOrder.UNORDERED, MapWriteFlags.CREATE_ONLY),
            aeroCtx.bin,
            getValues(cmd)
        )
        aeroCtx.client.operate(
            null, this, defaultWritePolicy,
            key, operation
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
            writeNullString(ctx)
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
