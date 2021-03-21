package com.aerospike.redispike.listener

import com.aerospike.client.AerospikeException
import com.aerospike.client.Key
import com.aerospike.client.listener.DeleteListener
import com.aerospike.redispike.command.RequestCommand
import com.aerospike.redispike.config.AerospikeContext
import com.aerospike.redispike.handler.CommandHandler
import com.aerospike.redispike.handler.NettyResponseWriter
import io.netty.channel.ChannelHandlerContext
import java.io.IOException

class DelCommandListener(
    private val aeroCtx: AerospikeContext,
    private val ctx: ChannelHandlerContext
) : NettyResponseWriter(), DeleteListener, CommandHandler {

    override fun onSuccess(key: Key?, existed: Boolean) {
        try {
            if (existed) {
                writeLong(ctx, 1L)
            } else {
                writeLong(ctx, 0L)
            }
        } catch (e: IOException) {
            ctx.close()
            e.printStackTrace()
        }
        ctx.flush()
    }

    override fun onFailure(exception: AerospikeException?) {
        try {
            writeErrorString(ctx, "ERROR")
            ctx.flush()
        } catch (e: IOException) {
            ctx.close()
        }
    }

    override fun handle(cmd: RequestCommand) {
        require(cmd.argCount == 2)

        val key = Key(aeroCtx.namespace, aeroCtx.set, cmd.args!![1])

        aeroCtx.client.delete(null, this, null, key)
    }
}
