package com.aerospike.redispike.listener

import com.aerospike.client.Key
import com.aerospike.client.Record
import com.aerospike.client.listener.RecordListener
import com.aerospike.redispike.command.RedisCommand
import com.aerospike.redispike.command.RequestCommand
import com.aerospike.redispike.config.AerospikeContext
import io.netty.channel.ChannelHandlerContext

class TtlCommandListener(
    aeroCtx: AerospikeContext,
    ctx: ChannelHandlerContext
) : BaseListener(aeroCtx, ctx), RecordListener {

    @Volatile
    private var m: Long = 1L

    override fun handle(cmd: RequestCommand) {
        require(cmd.argCount == 2) { "${this.javaClass.simpleName} argCount" }

        val key = createKey(cmd.key)
        if (cmd.command == RedisCommand.PTTL) m = 1000L
        aeroCtx.client.getHeader(null, this, null, key)
    }

    override fun onSuccess(key: Key?, record: Record?) {
        if (record == null) {
            writeLong(ctx, -2L)
            ctx.flush()
        } else {
            try {
                val ttl = if (record.timeToLive == -1) -1L else record.timeToLive * m
                writeLong(ctx, ttl)
                ctx.flush()
            } catch (e: Exception) {
                closeCtx(e)
            }
        }
    }
}
