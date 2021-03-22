package com.aerospike.redispike.listener

import com.aerospike.client.Bin
import com.aerospike.client.Key
import com.aerospike.client.Operation
import com.aerospike.client.Record
import com.aerospike.client.listener.RecordListener
import com.aerospike.redispike.command.RequestCommand
import com.aerospike.redispike.config.AerospikeContext
import com.aerospike.redispike.util.Typed
import io.netty.channel.ChannelHandlerContext

class GetsetCommandListener(
    aeroCtx: AerospikeContext,
    ctx: ChannelHandlerContext
) : BaseListener(aeroCtx, ctx), RecordListener {

    override fun handle(cmd: RequestCommand) {
        require(cmd.argCount == 3) { "${this.javaClass.simpleName} argCount" }

        val key = createKey(cmd.key)
        val value = Typed.getValue(cmd.args!![2])
        val ops = arrayOf(
            Operation.get(aeroCtx.bin),
            Operation.put(Bin(aeroCtx.bin, value))
        )

        aeroCtx.client.operate(null, this, updateOnlyPolicy, key, *ops)
    }

    override fun onSuccess(key: Key?, record: Record?) {
        if (record == null) {
            writeNullString(ctx)
            ctx.flush()
        } else {
            try {
                writeResponse(record.bins[aeroCtx.bin]!!)
                ctx.flush()
            } catch (e: Exception) {
                closeCtx(e)
            }
        }
    }
}
