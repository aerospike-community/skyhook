package com.aerospike.skyhook.listener.map

import com.aerospike.client.Key
import com.aerospike.client.Record
import com.aerospike.client.Value
import com.aerospike.client.cdt.MapOperation
import com.aerospike.client.cdt.MapReturnType
import com.aerospike.client.listener.RecordListener
import com.aerospike.skyhook.command.RequestCommand
import com.aerospike.skyhook.listener.BaseListener
import com.aerospike.skyhook.util.Typed
import io.netty.channel.ChannelHandlerContext

class HexistsCommandListener(
    ctx: ChannelHandlerContext
) : BaseListener(ctx), RecordListener {

    override fun handle(cmd: RequestCommand) {
        require(cmd.argCount == 3) { argValidationErrorMsg(cmd) }

        val key = createKey(cmd.key)
        val operation = MapOperation.getByKey(
            aeroCtx.bin, Typed.getValue(cmd.args[2]),
            MapReturnType.COUNT
        )
        client.operate(
            null, this, defaultWritePolicy,
            key, operation
        )
    }

    override fun onSuccess(key: Key?, record: Record?) {
        if (record == null) {
            writeLong(0L)
        } else {
            writeResponse(record.bins[aeroCtx.bin])
        }
        flushCtxTransactionAware()
    }
}

class SmismemberCommandListener(
    ctx: ChannelHandlerContext
) : BaseListener(ctx) {

    override fun handle(cmd: RequestCommand) {
        require(cmd.argCount >= 3) { argValidationErrorMsg(cmd) }

        val key = createKey(cmd.key)
        val values = getValues(cmd)
        writeArrayHeader(values.size.toLong())

        values.forEach { v ->
            val operation = MapOperation.getByKey(
                aeroCtx.bin, v, MapReturnType.COUNT
            )
            val exists = client.operate(
                defaultWritePolicy,
                key, operation
            )?.getLong(aeroCtx.bin) ?: 0L
            writeLong(exists)
        }
        flushCtxTransactionAware()
    }

    private fun getValues(cmd: RequestCommand): List<Value> {
        return cmd.args.drop(2)
            .map { Typed.getValue(it) }
    }
}
