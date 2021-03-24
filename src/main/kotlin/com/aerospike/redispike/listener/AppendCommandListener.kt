package com.aerospike.redispike.listener

import com.aerospike.client.*
import com.aerospike.client.listener.RecordListener
import com.aerospike.redispike.command.RequestCommand
import com.aerospike.redispike.config.AerospikeContext
import io.netty.channel.ChannelHandlerContext

class AppendCommandListener(
    aeroCtx: AerospikeContext,
    ctx: ChannelHandlerContext
) : BaseListener(aeroCtx, ctx), RecordListener {

    override fun handle(cmd: RequestCommand) {
        require(cmd.argCount == 3) { "${this.javaClass.simpleName} argCount" }

        val key = createKey(cmd.key)
        val ops = arrayOf(
            Operation.append(Bin(aeroCtx.bin, Value.StringValue(String(cmd.args!![2])))),
            Operation.get(aeroCtx.bin)
        )
        aeroCtx.client.operate(null, this, defaultWritePolicy, key, *ops)
    }

    override fun onSuccess(key: Key?, record: Record?) {
        if (record == null) {
            writeNullString(ctx)
            ctx.flush()
        } else {
            try {
                val value: String = (record.bins[aeroCtx.bin] as String)
                writeLong(ctx, value.length)
                ctx.flush()
            } catch (e: Exception) {
                closeCtx(e)
            }
        }
    }
}
