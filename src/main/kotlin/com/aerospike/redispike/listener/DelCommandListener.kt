package com.aerospike.redispike.listener

import com.aerospike.client.Key
import com.aerospike.client.listener.DeleteListener
import com.aerospike.redispike.command.RequestCommand
import com.aerospike.redispike.config.AerospikeContext
import io.netty.channel.ChannelHandlerContext

class DelCommandListener(
    aeroCtx: AerospikeContext,
    ctx: ChannelHandlerContext
) : BaseListener(aeroCtx, ctx), DeleteListener {

    override fun handle(cmd: RequestCommand) {
        require(cmd.argCount == 2) { argValidationErrorMsg(cmd) }

        val key = createKey(cmd.key)
        aeroCtx.client.delete(null, this, null, key)
    }

    override fun onSuccess(key: Key?, existed: Boolean) {
        try {
            if (existed) {
                writeLong(ctx, 1L)
            } else {
                writeLong(ctx, 0L)
            }
            ctx.flush()
        } catch (e: Exception) {
            closeCtx(e)
        }
    }
}
