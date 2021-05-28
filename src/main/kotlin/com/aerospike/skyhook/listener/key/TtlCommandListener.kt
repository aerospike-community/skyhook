package com.aerospike.skyhook.listener.key

import com.aerospike.client.Key
import com.aerospike.client.Record
import com.aerospike.client.listener.RecordListener
import com.aerospike.skyhook.command.RedisCommand
import com.aerospike.skyhook.command.RequestCommand
import com.aerospike.skyhook.listener.BaseListener
import io.netty.channel.ChannelHandlerContext

class TtlCommandListener(
    ctx: ChannelHandlerContext
) : BaseListener(ctx), RecordListener {

    @Volatile
    private var m: Long = 1L

    override fun handle(cmd: RequestCommand) {
        require(cmd.argCount == 2) { argValidationErrorMsg(cmd) }

        val key = createKey(cmd.key)
        if (cmd.command == RedisCommand.PTTL) m = 1000L
        client.getHeader(null, this, defaultWritePolicy, key)
    }

    override fun onSuccess(key: Key?, record: Record?) {
        if (record == null) {
            writeLong(-2L)
            flushCtxTransactionAware()
        } else {
            try {
                val ttl = if (record.timeToLive == -1) -1L else record.timeToLive * m
                writeLong(ttl)
                flushCtxTransactionAware()
            } catch (e: Exception) {
                closeCtx(e)
            }
        }
    }
}
