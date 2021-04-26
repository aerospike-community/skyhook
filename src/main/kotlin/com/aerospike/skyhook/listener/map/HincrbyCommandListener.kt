package com.aerospike.skyhook.listener.map

import com.aerospike.client.Key
import com.aerospike.client.Record
import com.aerospike.client.Value
import com.aerospike.client.cdt.MapOperation
import com.aerospike.client.cdt.MapPolicy
import com.aerospike.client.listener.RecordListener
import com.aerospike.skyhook.command.RedisCommand
import com.aerospike.skyhook.command.RequestCommand
import com.aerospike.skyhook.config.AerospikeContext
import com.aerospike.skyhook.listener.BaseListener
import com.aerospike.skyhook.util.Typed
import io.netty.channel.ChannelHandlerContext

class HincrbyCommandListener(
    aeroCtx: AerospikeContext,
    ctx: ChannelHandlerContext
) : BaseListener(aeroCtx, ctx), RecordListener {

    @Volatile
    private lateinit var command: RedisCommand

    override fun handle(cmd: RequestCommand) {
        require(cmd.argCount == 4) { argValidationErrorMsg(cmd) }

        command = cmd.command
        val key = createKey(cmd.key)
        val operation = MapOperation.increment(
            MapPolicy(), aeroCtx.bin,
            getMapKey(cmd), getIncrValue(cmd)
        )
        aeroCtx.client.operate(
            null, this, defaultWritePolicy,
            key, operation
        )
    }

    private fun getMapKey(cmd: RequestCommand): Value {
        return when (cmd.command) {
            RedisCommand.ZINCRBY -> Typed.getValue(cmd.args[3])
            else -> Typed.getValue(cmd.args[2])
        }
    }

    private fun getIncrValue(cmd: RequestCommand): Value {
        return when (cmd.command) {
            RedisCommand.ZINCRBY -> Typed.getValue(cmd.args[2])
            else -> Typed.getValue(cmd.args[3])
        }
    }

    override fun onSuccess(key: Key?, record: Record?) {
        if (record == null) {
            writeErrorString(ctx, "failed to create a record")
            ctx.flush()
        } else {
            try {
                when (command) {
                    RedisCommand.ZINCRBY -> writeResponse(record.getLong(aeroCtx.bin).toString())
                    else -> writeResponse(record.bins[aeroCtx.bin])
                }
                ctx.flush()
            } catch (e: Exception) {
                closeCtx(e)
            }
        }
    }
}
