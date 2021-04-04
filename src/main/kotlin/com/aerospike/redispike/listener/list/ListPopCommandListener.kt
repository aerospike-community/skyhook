package com.aerospike.redispike.listener.list

import com.aerospike.client.Key
import com.aerospike.client.Operation
import com.aerospike.client.Record
import com.aerospike.client.cdt.ListOperation
import com.aerospike.client.listener.RecordListener
import com.aerospike.redispike.command.RedisCommand
import com.aerospike.redispike.command.RequestCommand
import com.aerospike.redispike.config.AerospikeContext
import com.aerospike.redispike.listener.BaseListener
import com.aerospike.redispike.util.Typed
import io.netty.channel.ChannelHandlerContext

class ListPopCommandListener(
    aeroCtx: AerospikeContext,
    ctx: ChannelHandlerContext
) : BaseListener(aeroCtx, ctx), RecordListener {

    override fun handle(cmd: RequestCommand) {
        require(cmd.argCount == 2 || cmd.argCount == 3) {
            argValidationErrorMsg(cmd)
        }

        val key = createKey(cmd.key)
        aeroCtx.client.operate(
            null, this, defaultWritePolicy,
            key, getListOperation(cmd)
        )
    }

    private fun getListOperation(cmd: RequestCommand): Operation {
        val count = if (cmd.argCount == 2) 1 else Typed.getInteger(cmd.args[2])
        return when (cmd.command) {
            RedisCommand.LPOP -> {
                ListOperation.popRange(aeroCtx.bin, 0, count)
            }
            RedisCommand.RPOP -> {
                ListOperation.popRange(aeroCtx.bin, -1, count)
            }
            else -> {
                throw IllegalArgumentException(cmd.command.toString())
            }
        }
    }

    override fun onSuccess(key: Key?, record: Record?) {
        if (record == null) {
            writeNullArray(ctx)
            ctx.flush()
        } else {
            try {
                writeResponse(record.bins[aeroCtx.bin])
                ctx.flush()
            } catch (e: Exception) {
                closeCtx(e)
            }
        }
    }
}
