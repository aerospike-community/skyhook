package com.aerospike.skyhook.listener.map

import com.aerospike.client.Key
import com.aerospike.client.Operation
import com.aerospike.client.Record
import com.aerospike.client.Value
import com.aerospike.client.cdt.MapOperation
import com.aerospike.client.cdt.MapReturnType
import com.aerospike.client.listener.RecordListener
import com.aerospike.skyhook.command.RequestCommand
import com.aerospike.skyhook.config.AerospikeContext
import com.aerospike.skyhook.listener.BaseListener
import com.aerospike.skyhook.util.Intervals
import io.netty.channel.ChannelHandlerContext

open class ZrangeCommandListener(
    aeroCtx: AerospikeContext,
    ctx: ChannelHandlerContext
) : BaseListener(aeroCtx, ctx), RecordListener {

    protected data class LimitArgument(
        val offset: Int,
        val count: Int,
    )

    protected class RangeCommand(val cmd: RequestCommand, flagIndex: Int) {
        var BYSCORE: Boolean = false
        var BYLEX: Boolean = false
        var REV: Boolean = false
        var LIMIT: LimitArgument? = null
            private set
        var WITHSCORES: Boolean = false

        val min: Int by lazy {
            Intervals.fromScore(String(cmd.args[flagIndex - 2]))
        }

        val max: Int by lazy {
            Intervals.upScore(String(cmd.args[flagIndex - 1]))
        }

        val factor: Long by lazy {
            if (WITHSCORES) 2L else 1L
        }

        init {
            var i = flagIndex
            while (i < cmd.args.size) {
                i += setFlag(i)
            }
        }

        private fun setFlag(i: Int): Int {
            val flagStr = String(cmd.args[i])
            when (flagStr.toUpperCase()) {
                "BYSCORE" -> BYSCORE = true
                "BYLEX" -> BYLEX = true
                "REV" -> REV = true
                "WITHSCORES" -> WITHSCORES = true
                "LIMIT" -> {
                    LIMIT = LimitArgument(
                        String(cmd.args[i + 1]).toInt(),
                        String(cmd.args[i + 2]).toInt(),
                    )
                    return 3
                }
                else -> throw IllegalArgumentException(flagStr)
            }
            return 1
        }
    }

    @Volatile
    protected lateinit var rangeCommand: RangeCommand

    override fun handle(cmd: RequestCommand) {
        require(cmd.argCount >= 4) { argValidationErrorMsg(cmd) }

        val key = createKey(cmd.key)
        rangeCommand = RangeCommand(cmd, 4)
        validateRangeCommand()

        aeroCtx.client.operate(
            null, this, defaultWritePolicy,
            key, getMapOperation()
        )
    }

    protected open fun validateRangeCommand() {
        require(!(rangeCommand.BYSCORE && rangeCommand.BYLEX)) { "[BYSCORE|BYLEX]" }
        require(!rangeCommand.BYLEX) { "BYLEX flag not supported" }
    }

    protected open fun getMapReturnType(): Int {
        return if (rangeCommand.WITHSCORES) {
            MapReturnType.KEY_VALUE
        } else {
            MapReturnType.KEY
        }
    }

    protected open fun getMapOperation(): Operation {
        return if (rangeCommand.BYSCORE) {
            MapOperation.getByValueRange(
                aeroCtx.bin,
                Value.get(rangeCommand.min),
                Value.get(rangeCommand.max), getMapReturnType()
            )
        } else {
            val from = if (rangeCommand.min == Int.MIN_VALUE) 0 else rangeCommand.min
            val count = rangeCommand.max - from
            MapOperation.getByIndexRange(
                aeroCtx.bin,
                from,
                count, getMapReturnType()
            )
        }
    }

    override fun onSuccess(key: Key?, record: Record?) {
        try {
            if (record == null) {
                writeEmptyList(ctx)
            } else {
                writeResponse(record.bins[aeroCtx.bin])
            }
            ctx.flush()
        } catch (e: Exception) {
            closeCtx(e)
        }
    }

    override fun writeResponse(mapped: Any?) {
        when (mapped) {
            is Map<*, *> -> applyFilters(mapped.toList()).forEach {
                super.writeResponse(it.first.toString())
                super.writeResponse(it.second.toString())
            }
            is List<*> -> when (mapped.firstOrNull()) {
                is Map.Entry<*, *> -> applyFilters(mapped)
                    .map { it as Map.Entry<*, *> }.map { it.toPair() }
                    .forEach {
                        super.writeResponse(it.first.toString())
                        super.writeResponse(it.second.toString())
                    }
                else -> applyFilters(mapped).forEach {
                    super.writeResponse(it.toString())
                }
            }
        }
    }

    private fun <T> applyFilters(list: List<T>): List<T> {
        return list.let {
            if (rangeCommand.REV) {
                it.reversed()
            } else {
                it
            }
        }.let { l ->
            rangeCommand.LIMIT?.let {
                l.subList(it.offset, it.offset + it.count)
            } ?: l
        }.also {
            writeArrayHeader(ctx, it.size * rangeCommand.factor)
        }
    }
}

class ZrevrangeCommandListener(
    aeroCtx: AerospikeContext,
    ctx: ChannelHandlerContext
) : ZrangeCommandListener(aeroCtx, ctx) {

    override fun handle(cmd: RequestCommand) {
        require(cmd.argCount >= 4) { argValidationErrorMsg(cmd) }

        val key = createKey(cmd.key)
        rangeCommand = RangeCommand(cmd, 4)
        rangeCommand.REV = true
        validateRangeCommand()

        aeroCtx.client.operate(
            null, this, defaultWritePolicy,
            key, getMapOperation()
        )
    }

    override fun validateRangeCommand() {
        require(!rangeCommand.BYSCORE) { "BYSCORE flag not supported" }
        require(!rangeCommand.BYLEX) { "BYLEX flag not supported" }
        require(rangeCommand.LIMIT?.let { false } ?: true) { "LIMIT flag not supported" }
    }
}

class ZrangebyscoreCommandListener(
    aeroCtx: AerospikeContext,
    ctx: ChannelHandlerContext
) : ZrangeCommandListener(aeroCtx, ctx) {

    override fun handle(cmd: RequestCommand) {
        require(cmd.argCount >= 4) { argValidationErrorMsg(cmd) }

        val key = createKey(cmd.key)
        rangeCommand = RangeCommand(cmd, 4)
        rangeCommand.BYSCORE = true
        validateRangeCommand()

        aeroCtx.client.operate(
            null, this, defaultWritePolicy,
            key, getMapOperation()
        )
    }

    override fun validateRangeCommand() {
        require(!rangeCommand.REV) { "REV flag not supported" }
        require(!rangeCommand.BYLEX) { "BYLEX flag not supported" }
    }
}

class ZrevrangebyscoreCommandListener(
    aeroCtx: AerospikeContext,
    ctx: ChannelHandlerContext
) : ZrangeCommandListener(aeroCtx, ctx) {

    override fun handle(cmd: RequestCommand) {
        require(cmd.argCount >= 4) { argValidationErrorMsg(cmd) }

        val key = createKey(cmd.key)
        rangeCommand = RangeCommand(cmd, 4)
        rangeCommand.REV = true
        rangeCommand.BYSCORE = true
        validateRangeCommand()

        aeroCtx.client.operate(
            null, this, defaultWritePolicy,
            key, getMapOperation()
        )
    }

    override fun validateRangeCommand() {
        require(!rangeCommand.BYLEX) { "BYLEX flag not supported" }
    }
}
