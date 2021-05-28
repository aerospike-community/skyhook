package com.aerospike.skyhook.listener.list

import com.aerospike.client.Key
import com.aerospike.client.Operation
import com.aerospike.client.Record
import com.aerospike.client.cdt.ListOperation
import com.aerospike.client.listener.RecordListener
import com.aerospike.skyhook.command.RedisCommand
import com.aerospike.skyhook.command.RequestCommand
import com.aerospike.skyhook.listener.BaseListener
import com.aerospike.skyhook.util.Typed
import io.netty.channel.ChannelHandlerContext

class ListPopCommandListener(
    ctx: ChannelHandlerContext
) : BaseListener(ctx), RecordListener {

    override fun handle(cmd: RequestCommand) {
        require(cmd.argCount == 2 || cmd.argCount == 3) {
            argValidationErrorMsg(cmd)
        }

        val key = createKey(cmd.key)
        client.operate(
            null, this, defaultWritePolicy,
            key, getListOperation(cmd), *systemOps()
        )
    }

    private fun getListOperation(cmd: RequestCommand): Operation {
        val count = if (cmd.argCount == 2) 1 else Typed.getInteger(cmd.args[2])
        return when (cmd.command) {
            RedisCommand.LPOP -> {
                ListOperation.popRange(aeroCtx.bin, 0, count)
            }
            RedisCommand.RPOP -> {
                ListOperation.popRange(aeroCtx.bin, -1 * count, count)
            }
            else -> {
                throw IllegalArgumentException(cmd.command.toString())
            }
        }
    }

    override fun onSuccess(key: Key?, record: Record?) {
        if (record == null) {
            writeNullArray()
            flushCtxTransactionAware()
        } else {
            try {
                writeResponse(record.bins[aeroCtx.bin])
                flushCtxTransactionAware()
            } catch (e: Exception) {
                closeCtx(e)
            }
        }
    }
}
