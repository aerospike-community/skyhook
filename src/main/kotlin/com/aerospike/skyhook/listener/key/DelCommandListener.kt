package com.aerospike.skyhook.listener.key

import com.aerospike.client.Key
import com.aerospike.client.listener.DeleteListener
import com.aerospike.skyhook.command.RequestCommand
import com.aerospike.skyhook.listener.BaseListener
import io.netty.channel.ChannelHandlerContext

class DelCommandListener(
    ctx: ChannelHandlerContext
) : BaseListener(ctx), DeleteListener {

    override fun handle(cmd: RequestCommand) {
        require(cmd.argCount == 2) { argValidationErrorMsg(cmd) }

        val key = createKey(cmd.key)
        client.delete(null, this, defaultWritePolicy, key)
    }

    override fun onSuccess(key: Key?, existed: Boolean) {
        try {
            if (existed) {
                writeLong(1L)
            } else {
                writeLong(0L)
            }
            flushCtxTransactionAware()
        } catch (e: Exception) {
            closeCtx(e)
        }
    }
}
