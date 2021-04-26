package com.aerospike.skyhook.listener.map

import com.aerospike.client.*
import com.aerospike.client.cdt.MapOperation
import com.aerospike.client.cdt.MapReturnType
import com.aerospike.client.listener.RecordListener
import com.aerospike.skyhook.command.RequestCommand
import com.aerospike.skyhook.config.AerospikeContext
import com.aerospike.skyhook.listener.BaseListener
import com.aerospike.skyhook.util.Intervals
import io.netty.channel.ChannelHandlerContext

open class ZcountCommandListener(
    aeroCtx: AerospikeContext,
    ctx: ChannelHandlerContext
) : BaseListener(aeroCtx, ctx), RecordListener {

    override fun handle(cmd: RequestCommand) {
        require(cmd.argCount == 4) { argValidationErrorMsg(cmd) }

        val key = createKey(cmd.key)
        aeroCtx.client.operate(
            null, this, null,
            key, getOperation(cmd)
        )
    }

    protected open fun getOperation(cmd: RequestCommand): Operation {
        return MapOperation.getByValueRange(
            aeroCtx.bin,
            Value.get(Intervals.fromScore(String(cmd.args[2]))),
            Value.get(Intervals.upScore(String(cmd.args[3]))),
            MapReturnType.COUNT
        )
    }

    override fun writeError(e: AerospikeException?) {
        writeLong(ctx, 0L)
    }

    override fun onSuccess(key: Key?, record: Record?) {
        if (record == null) {
            writeLong(ctx, 0L)
            ctx.flush()
        } else {
            try {
                writeLong(ctx, record.getLong(aeroCtx.bin))
                ctx.flush()
            } catch (e: Exception) {
                closeCtx(e)
            }
        }
    }
}

class ZremrangebyscoreCommandListener(
    aeroCtx: AerospikeContext,
    ctx: ChannelHandlerContext
) : ZcountCommandListener(aeroCtx, ctx) {

    override fun getOperation(cmd: RequestCommand): Operation {
        return MapOperation.removeByValueRange(
            aeroCtx.bin,
            Value.get(Intervals.fromScore(String(cmd.args[2]))),
            Value.get(Intervals.upScore(String(cmd.args[3]))),
            MapReturnType.COUNT
        )
    }
}
