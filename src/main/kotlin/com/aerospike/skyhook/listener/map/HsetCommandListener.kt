package com.aerospike.skyhook.listener.map

import com.aerospike.client.*
import com.aerospike.client.cdt.MapOperation
import com.aerospike.client.cdt.MapOrder
import com.aerospike.client.cdt.MapPolicy
import com.aerospike.client.cdt.MapWriteFlags
import com.aerospike.client.listener.RecordListener
import com.aerospike.skyhook.command.RequestCommand
import com.aerospike.skyhook.listener.BaseListener
import com.aerospike.skyhook.listener.ValueType
import com.aerospike.skyhook.util.Typed
import io.netty.channel.ChannelHandlerContext

class HsetnxCommandListener(
    ctx: ChannelHandlerContext
) : BaseListener(ctx), RecordListener {

    override fun handle(cmd: RequestCommand) {
        require(cmd.argCount == 4) { argValidationErrorMsg(cmd) }

        val key = createKey(cmd.key)
        val operation = MapOperation.put(
            MapPolicy(MapOrder.UNORDERED, MapWriteFlags.CREATE_ONLY),
            aeroCtx.bin,
            Typed.getValue(cmd.args[2]),
            Typed.getValue(cmd.args[3])
        )
        client.operate(
            null, this, defaultWritePolicy,
            key, *systemOps(ValueType.HASH), operation
        )
    }

    override fun writeError(e: AerospikeException?) {
        writeLong(0L)
    }

    override fun onSuccess(key: Key?, record: Record?) {
        if (record == null) {
            writeNullString()
            flushCtxTransactionAware()
        } else {
            try {
                writeLong(1L)
                flushCtxTransactionAware()
            } catch (e: Exception) {
                closeCtx(e)
            }
        }
    }
}

open class HsetCommandListener(
    ctx: ChannelHandlerContext
) : SaddCommandListener(ctx) {

    override val systemOps: Array<Operation> = systemOps(ValueType.HASH)
    override val mapPolicy = MapPolicy()

    override fun validate(cmd: RequestCommand) {
        require(cmd.argCount >= 4 && cmd.argCount % 2 == 0) {
            argValidationErrorMsg(cmd)
        }
    }

    override fun getValues(cmd: RequestCommand): Map<Value, Value> {
        return cmd.args.drop(2).chunked(2)
            .map { (it1, it2) -> Typed.getValue(it1) to Typed.getValue(it2) }
            .toMap()
    }
}

class HmsetCommandListener(
    ctx: ChannelHandlerContext
) : HsetCommandListener(ctx) {

    override fun setSize(key: Key) {}

    override fun onSuccess(key: Key?, record: Record?) {
        if (record == null) {
            writeNullString()
            flushCtxTransactionAware()
        } else {
            try {
                writeOK()
                flushCtxTransactionAware()
            } catch (e: Exception) {
                closeCtx(e)
            }
        }
    }
}
