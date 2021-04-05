package com.aerospike.redispike.listener.map

import com.aerospike.client.Key
import com.aerospike.client.Record
import com.aerospike.client.Value
import com.aerospike.client.cdt.MapOperation
import com.aerospike.client.cdt.MapPolicy
import com.aerospike.client.listener.RecordListener
import com.aerospike.redispike.command.RequestCommand
import com.aerospike.redispike.config.AerospikeContext
import com.aerospike.redispike.listener.BaseListener
import com.aerospike.redispike.util.Typed
import io.netty.channel.ChannelHandlerContext

class HmsetCommandListener(
    aeroCtx: AerospikeContext,
    ctx: ChannelHandlerContext
) : BaseListener(aeroCtx, ctx), RecordListener {

    override fun handle(cmd: RequestCommand) {
        require(cmd.argCount >= 4 && cmd.argCount % 2 == 0) {
            argValidationErrorMsg(cmd)
        }

        val key = createKey(cmd.key)
        val operation = MapOperation.putItems(
            MapPolicy(),
            aeroCtx.bin,
            getValues(cmd)
        )
        aeroCtx.client.operate(
            null, this, defaultWritePolicy,
            key, operation
        )
    }

    private fun getValues(cmd: RequestCommand): Map<Value, Value> {
        return cmd.args.drop(2).chunked(2)
            .map { (it1, it2) -> Typed.getValue(it1) to Typed.getValue(it2) }
            .toMap()
    }

    override fun onSuccess(key: Key?, record: Record?) {
        if (record == null) {
            writeNullString(ctx)
            ctx.flush()
        } else {
            try {
                writeOK(ctx)
                ctx.flush()
            } catch (e: Exception) {
                closeCtx(e)
            }
        }
    }
}
