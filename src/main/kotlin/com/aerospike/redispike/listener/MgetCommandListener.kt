package com.aerospike.redispike.listener

import com.aerospike.client.BatchRead
import com.aerospike.client.Key
import com.aerospike.client.Value
import com.aerospike.client.listener.BatchListListener
import com.aerospike.redispike.command.RequestCommand
import com.aerospike.redispike.config.AerospikeContext
import io.netty.channel.ChannelHandlerContext

class MgetCommandListener(
    aeroCtx: AerospikeContext,
    ctx: ChannelHandlerContext
) : BaseListener(aeroCtx, ctx), BatchListListener {

    override fun handle(cmd: RequestCommand) {
        require(cmd.argCount >= 2) { "${this.javaClass.simpleName} argCount" }

        val keys = cmd.args!!.drop(1)
            .map { Key(aeroCtx.namespace, aeroCtx.set, Value.get(it)) }
            .map { BatchRead(it, true) }
        aeroCtx.client.get(null, this, null, keys)
    }

    override fun onSuccess(records: MutableList<BatchRead>?) {
        if (records == null) {
            writeNullString(ctx)
            ctx.flush()
        } else {
            try {
                writeObjectListStr(ctx, records.mapNotNull {
                    if (it.record != null) {
                        it.record.bins[aeroCtx.bin]
                    } else null
                })
                ctx.flush()
            } catch (e: Exception) {
                closeCtx(e)
            }
        }
    }
}
