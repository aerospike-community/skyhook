package com.aerospike.skyhook.listener.key

import com.aerospike.client.*
import com.aerospike.client.listener.RecordListener
import com.aerospike.skyhook.command.RequestCommand
import com.aerospike.skyhook.config.AerospikeContext
import com.aerospike.skyhook.listener.BaseListener
import io.netty.channel.ChannelHandlerContext

class AppendCommandListener(
    aeroCtx: AerospikeContext,
    ctx: ChannelHandlerContext
) : BaseListener(aeroCtx, ctx), RecordListener {

    override fun handle(cmd: RequestCommand) {
        require(cmd.argCount == 3) { argValidationErrorMsg(cmd) }

        val key = createKey(cmd.key)
        val ops = arrayOf(
            stringTypeOp(),
            Operation.append(Bin(aeroCtx.bin, Value.StringValue(String(cmd.args[2])))),
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
