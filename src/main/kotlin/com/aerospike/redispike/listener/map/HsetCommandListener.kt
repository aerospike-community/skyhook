package com.aerospike.redispike.listener.map

import com.aerospike.client.AerospikeException
import com.aerospike.client.Key
import com.aerospike.client.Record
import com.aerospike.client.cdt.MapOperation
import com.aerospike.client.cdt.MapOrder
import com.aerospike.client.cdt.MapPolicy
import com.aerospike.client.cdt.MapWriteFlags
import com.aerospike.client.listener.RecordListener
import com.aerospike.redispike.command.RedisCommand
import com.aerospike.redispike.command.RequestCommand
import com.aerospike.redispike.config.AerospikeContext
import com.aerospike.redispike.listener.BaseListener
import com.aerospike.redispike.util.Typed
import io.netty.channel.ChannelHandlerContext

class HsetCommandListener(
    aeroCtx: AerospikeContext,
    ctx: ChannelHandlerContext
) : BaseListener(aeroCtx, ctx), RecordListener {

    override fun handle(cmd: RequestCommand) {
        require(cmd.argCount == 4) { argValidationErrorMsg(cmd) }

        val key = createKey(cmd.key)
        val operation = MapOperation.put(
            getMapPolicy(cmd),
            aeroCtx.bin,
            Typed.getValue(cmd.args!![2]),
            Typed.getValue(cmd.args[3])
        )
        aeroCtx.client.operate(
            null, this, defaultWritePolicy,
            key, operation
        )
    }

    private fun getMapPolicy(cmd: RequestCommand): MapPolicy {
        return when (cmd.command) {
            RedisCommand.HSET -> {
                MapPolicy()
            }
            RedisCommand.HSETNX -> {
                MapPolicy(MapOrder.UNORDERED, MapWriteFlags.CREATE_ONLY)
            }
            else -> {
                throw IllegalArgumentException(cmd.command.toString())
            }
        }
    }

    override fun writeError(e: AerospikeException?) {
        writeLong(ctx, 0L)
    }

    override fun onSuccess(key: Key?, record: Record?) {
        if (record == null) {
            writeNullString(ctx)
            ctx.flush()
        } else {
            try {
                writeLong(ctx, 1L)
                ctx.flush()
            } catch (e: Exception) {
                closeCtx(e)
            }
        }
    }
}
