package com.aerospike.redispike.listener.map

import com.aerospike.client.Key
import com.aerospike.client.Operation
import com.aerospike.client.Record
import com.aerospike.client.Value
import com.aerospike.client.cdt.MapOperation
import com.aerospike.client.cdt.MapReturnType
import com.aerospike.client.listener.RecordListener
import com.aerospike.redispike.command.RedisCommand
import com.aerospike.redispike.command.RequestCommand
import com.aerospike.redispike.config.AerospikeContext
import com.aerospike.redispike.listener.BaseListener
import com.aerospike.redispike.util.Typed
import io.netty.channel.ChannelHandlerContext

class MapGetCommandListener(
    aeroCtx: AerospikeContext,
    ctx: ChannelHandlerContext
) : BaseListener(aeroCtx, ctx), RecordListener {

    override fun handle(cmd: RequestCommand) {
        val key = createKey(cmd.key)

        aeroCtx.client.operate(
            null, this, null,
            key, getOperation(cmd)
        )
    }

    private fun getValues(cmd: RequestCommand): List<Value> {
        return cmd.args.drop(2)
            .map { Typed.getValue(it) }
    }

    private fun getOperation(cmd: RequestCommand): Operation {
        return when (cmd.command) {
            RedisCommand.HGET -> {
                require(cmd.argCount == 3) { argValidationErrorMsg(cmd) }

                val mapKey = Typed.getValue(cmd.args[2])
                MapOperation.getByKey(
                    aeroCtx.bin, mapKey,
                    MapReturnType.VALUE
                )
            }
            RedisCommand.HMGET -> {
                require(cmd.argCount >= 3) { argValidationErrorMsg(cmd) }

                val mapKeys = getValues(cmd)
                MapOperation.getByKeyList(
                    aeroCtx.bin, mapKeys,
                    MapReturnType.VALUE
                )
            }
            RedisCommand.HGETALL -> {
                require(cmd.argCount == 2) { argValidationErrorMsg(cmd) }

                MapOperation.getByKeyRange(
                    aeroCtx.bin, null, null,
                    MapReturnType.KEY_VALUE
                )
            }
            RedisCommand.HVALS -> {
                require(cmd.argCount == 2) { argValidationErrorMsg(cmd) }

                MapOperation.getByKeyRange(
                    aeroCtx.bin, null, null,
                    MapReturnType.VALUE
                )
            }
            RedisCommand.HKEYS, RedisCommand.SMEMBERS -> {
                require(cmd.argCount == 2) { argValidationErrorMsg(cmd) }

                MapOperation.getByKeyRange(
                    aeroCtx.bin, null, null,
                    MapReturnType.KEY
                )
            }
            else -> {
                throw IllegalArgumentException(cmd.command.toString())
            }
        }
    }

    override fun onSuccess(key: Key?, record: Record?) {
        if (record == null) {
            writeNullString(ctx)
            ctx.flush()
        } else {
            try {
                writeResponse(marshalOutput(record.bins[aeroCtx.bin]))
                ctx.flush()
            } catch (e: Exception) {
                closeCtx(e)
            }
        }
    }

    private fun marshalOutput(data: Any?): Any? {
        return when (data) {
            is Map<*, *> -> data.toList()
            is List<*> -> {
                when (data.firstOrNull()) {
                    is Map.Entry<*, *> -> data.map { it as Map.Entry<*, *> }
                        .map { it.toPair().toList() }.flatten()
                    else -> data
                }
            }
            else -> data
        }
    }
}
