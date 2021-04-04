package com.aerospike.redispike.listener.key

import com.aerospike.client.Bin
import com.aerospike.client.Key
import com.aerospike.client.Operation
import com.aerospike.client.Record
import com.aerospike.client.listener.RecordListener
import com.aerospike.redispike.command.RedisCommand
import com.aerospike.redispike.command.RequestCommand
import com.aerospike.redispike.config.AerospikeContext
import com.aerospike.redispike.listener.BaseListener
import com.aerospike.redispike.util.Typed
import io.netty.channel.ChannelHandlerContext

class IncrCommandListener(
    aeroCtx: AerospikeContext,
    ctx: ChannelHandlerContext
) : BaseListener(aeroCtx, ctx), RecordListener {

    override fun handle(cmd: RequestCommand) {
        require(cmd.argCount == 2 || cmd.argCount == 3) {
            argValidationErrorMsg(cmd)
        }

        val key = createKey(cmd.key)
        val ops = arrayOf(
            getIncrOperation(cmd),
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
                writeObject(ctx, record.bins[aeroCtx.bin])
                ctx.flush()
            } catch (e: Exception) {
                closeCtx(e)
            }
        }
    }

    private fun getIncrOperation(cmd: RequestCommand): Operation {
        return when (cmd.command) {
            RedisCommand.INCR -> {
                Operation.add(Bin(aeroCtx.bin, 1))
            }
            RedisCommand.INCRBY -> {
                Operation.add(Bin(aeroCtx.bin, Typed.getInteger(cmd.args[2])))
            }
            RedisCommand.INCRBYFLOAT -> {
                Operation.add(Bin(aeroCtx.bin, Typed.getDouble(cmd.args[2])))
            }
            else -> {
                throw IllegalArgumentException(cmd.command.toString())
            }
        }
    }
}
