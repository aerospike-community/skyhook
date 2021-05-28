package com.aerospike.skyhook.listener.key

import com.aerospike.client.Bin
import com.aerospike.client.Key
import com.aerospike.client.Operation
import com.aerospike.client.Record
import com.aerospike.client.listener.RecordListener
import com.aerospike.skyhook.command.RedisCommand
import com.aerospike.skyhook.command.RequestCommand
import com.aerospike.skyhook.listener.BaseListener
import com.aerospike.skyhook.listener.ValueType
import com.aerospike.skyhook.util.Typed
import io.netty.channel.ChannelHandlerContext

abstract class UnaryCommandListener(
    ctx: ChannelHandlerContext
) : BaseListener(ctx), RecordListener {

    override fun handle(cmd: RequestCommand) {
        require(cmd.argCount == 2 || cmd.argCount == 3) {
            argValidationErrorMsg(cmd)
        }

        val key = createKey(cmd.key)
        val ops = arrayOf(
            *systemOps(ValueType.STRING),
            getUnaryOperation(cmd),
            Operation.get(aeroCtx.bin)
        )
        client.operate(
            null, this,
            defaultWritePolicy, key, *ops
        )
    }

    override fun onSuccess(key: Key?, record: Record?) {
        if (record == null) {
            writeNullString()
            flushCtxTransactionAware()
        } else {
            try {
                writeObject(record.bins[aeroCtx.bin])
                flushCtxTransactionAware()
            } catch (e: Exception) {
                closeCtx(e)
            }
        }
    }

    protected abstract fun getUnaryOperation(cmd: RequestCommand): Operation
}

class IncrCommandListener(
    ctx: ChannelHandlerContext
) : UnaryCommandListener(ctx) {

    override fun getUnaryOperation(cmd: RequestCommand): Operation {
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

class DecrCommandListener(
    ctx: ChannelHandlerContext
) : UnaryCommandListener(ctx) {

    override fun getUnaryOperation(cmd: RequestCommand): Operation {
        return when (cmd.command) {
            RedisCommand.DECR -> {
                Operation.add(Bin(aeroCtx.bin, -1))
            }
            RedisCommand.DECRBY -> {
                Operation.add(Bin(aeroCtx.bin, -Typed.getInteger(cmd.args[2])))
            }
            else -> {
                throw IllegalArgumentException(cmd.command.toString())
            }
        }
    }
}
