package com.aerospike.skyhook.listener.map

import com.aerospike.client.*
import com.aerospike.client.cdt.MapOperation
import com.aerospike.client.cdt.MapReturnType
import com.aerospike.client.listener.RecordListener
import com.aerospike.skyhook.command.RequestCommand
import com.aerospike.skyhook.listener.BaseListener
import com.aerospike.skyhook.util.Intervals
import com.aerospike.skyhook.util.Typed
import io.netty.channel.ChannelHandlerContext

open class ZcountCommandListener(
    ctx: ChannelHandlerContext
) : BaseListener(ctx), RecordListener {

    override fun handle(cmd: RequestCommand) {
        require(cmd.argCount == 4) { argValidationErrorMsg(cmd) }

        val key = createKey(cmd.key)
        client.operate(
            null, this, defaultWritePolicy,
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
        writeLong(0L)
    }

    override fun onSuccess(key: Key?, record: Record?) {
        if (record == null) {
            writeLong(0L)
            flushCtxTransactionAware()
        } else {
            try {
                writeLong(record.getLong(aeroCtx.bin))
                flushCtxTransactionAware()
            } catch (e: Exception) {
                closeCtx(e)
            }
        }
    }
}

class ZlexcountCommandListener(
    ctx: ChannelHandlerContext
) : ZcountCommandListener(ctx) {

    override fun getOperation(cmd: RequestCommand): Operation {
        return MapOperation.getByKeyRange(
            aeroCtx.bin,
            Value.get(Intervals.fromLex(String(cmd.args[2]))),
            Value.get(Intervals.upLex(String(cmd.args[3]))),
            MapReturnType.COUNT
        )
    }
}

class ZremrangebyscoreCommandListener(
    ctx: ChannelHandlerContext
) : ZcountCommandListener(ctx) {

    override fun getOperation(cmd: RequestCommand): Operation {
        return MapOperation.removeByValueRange(
            aeroCtx.bin,
            Value.get(Intervals.fromScore(String(cmd.args[2]))),
            Value.get(Intervals.upScore(String(cmd.args[3]))),
            MapReturnType.COUNT
        )
    }
}

class ZremrangebyrankCommandListener(
    ctx: ChannelHandlerContext
) : ZcountCommandListener(ctx) {

    override fun getOperation(cmd: RequestCommand): Operation {
        val from = Typed.getInteger(cmd.args[2])
        val count = getCount(from, cmd)
        return MapOperation.removeByRankRange(
            aeroCtx.bin,
            from,
            count,
            MapReturnType.COUNT
        )
    }

    private fun getCount(from: Int, cmd: RequestCommand): Int {
        val to = Typed.getInteger(cmd.args[3])
        return maxOf(
            if (to < 0) {
                val key = createKey(cmd.key)
                val mapSize = client.operate(
                    null,
                    key, MapOperation.size(aeroCtx.bin)
                ).getInt(aeroCtx.bin)
                (mapSize + to + 1) - from
            } else {
                (to - from) + 1
            }, 0
        )
    }
}

class ZremrangebylexCommandListener(
    ctx: ChannelHandlerContext
) : ZcountCommandListener(ctx) {

    override fun getOperation(cmd: RequestCommand): Operation {
        return MapOperation.removeByKeyRange(
            aeroCtx.bin,
            Value.get(Intervals.fromLex(String(cmd.args[2]))),
            Value.get(Intervals.upLex(String(cmd.args[3]))),
            MapReturnType.COUNT
        )
    }
}
