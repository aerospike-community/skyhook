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

open class SaddCommandListener(
    ctx: ChannelHandlerContext
) : BaseListener(ctx), RecordListener {

    @Volatile
    protected open var size: Long = 0L
    protected open val systemOps: Array<Operation> = systemOps(ValueType.SET)
    protected open val mapPolicy = MapPolicy(MapOrder.UNORDERED, MapWriteFlags.CREATE_ONLY)

    override fun handle(cmd: RequestCommand) {
        validate(cmd)

        val key = createKey(cmd.key)
        setSize(key)

        val operation = MapOperation.putItems(
            mapPolicy,
            aeroCtx.bin,
            getValues(cmd)
        )
        client.operate(
            null, this, defaultWritePolicy,
            key, *systemOps, operation
        )
    }

    protected open fun validate(cmd: RequestCommand) {
        require(cmd.argCount >= 3) { argValidationErrorMsg(cmd) }
    }

    protected open fun setSize(key: Key) {
        val getSize = MapOperation.size(aeroCtx.bin)
        size = client.operate(defaultWritePolicy, key, getSize)
            ?.getLong(aeroCtx.bin) ?: 0L
    }

    protected open fun getValues(cmd: RequestCommand): Map<Value, Value> {
        return cmd.args.drop(2)
            .map { Typed.getValue(it) to Value.getAsNull() }
            .toMap()
    }

    override fun writeError(e: AerospikeException?) {
        writeLong(0L)
    }

    override fun onSuccess(key: Key?, record: Record?) {
        if (record == null) {
            writeLong(0L)
            flushCtxTransactionAware()
        } else {
            try {
                val added = record.getLong(aeroCtx.bin) - size
                writeLong(added)
                flushCtxTransactionAware()
            } catch (e: Exception) {
                closeCtx(e)
            }
        }
    }
}
