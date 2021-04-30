package com.aerospike.skyhook.listener.map

import com.aerospike.client.*
import com.aerospike.client.cdt.MapOperation
import com.aerospike.client.cdt.MapOrder
import com.aerospike.client.cdt.MapPolicy
import com.aerospike.client.cdt.MapWriteFlags
import com.aerospike.client.listener.RecordListener
import com.aerospike.skyhook.command.RequestCommand
import com.aerospike.skyhook.config.AerospikeContext
import com.aerospike.skyhook.listener.BaseListener
import com.aerospike.skyhook.util.Typed
import io.netty.channel.ChannelHandlerContext

class HsetnxCommandListener(
    aeroCtx: AerospikeContext,
    ctx: ChannelHandlerContext
) : BaseListener(aeroCtx, ctx), RecordListener {

    override fun handle(cmd: RequestCommand) {
        require(cmd.argCount == 4) { argValidationErrorMsg(cmd) }

        val key = createKey(cmd.key)
        val operation = MapOperation.put(
            MapPolicy(MapOrder.UNORDERED, MapWriteFlags.CREATE_ONLY),
            aeroCtx.bin,
            Typed.getValue(cmd.args[2]),
            Typed.getValue(cmd.args[3])
        )
        aeroCtx.client.operate(
            null, this, defaultWritePolicy,
            key, hashTypeOp(), operation
        )
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

open class HsetCommandListener(
    aeroCtx: AerospikeContext,
    ctx: ChannelHandlerContext
) : SaddCommandListener(aeroCtx, ctx) {

    override val typeOperation: Operation = hashTypeOp()
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
    aeroCtx: AerospikeContext,
    ctx: ChannelHandlerContext
) : HsetCommandListener(aeroCtx, ctx) {

    override fun setSize(key: Key) {}

    override fun onSuccess(key: Key?, record: Record?) {
        if (record == null) {
            writeNullString(ctx)
            ctx.flush()
        } else {
            try {
                writeOK(ctx)
                ctx.flush()
            } catch (e: Exception) {
                closeCtx(e)
            }
        }
    }
}
