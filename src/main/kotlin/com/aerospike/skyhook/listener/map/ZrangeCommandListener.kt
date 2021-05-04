package com.aerospike.skyhook.listener.map

import com.aerospike.client.Key
import com.aerospike.client.Operation
import com.aerospike.client.Record
import com.aerospike.client.Value
import com.aerospike.client.cdt.MapOperation
import com.aerospike.client.cdt.MapReturnType
import com.aerospike.client.listener.RecordListener
import com.aerospike.skyhook.command.RequestCommand
import com.aerospike.skyhook.listener.BaseListener
import com.aerospike.skyhook.util.Intervals
import io.netty.channel.ChannelHandlerContext

open class ZrangeCommandListener(
    ctx: ChannelHandlerContext
) : BaseListener(ctx), RecordListener {

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

        val minScore: Int by lazy {
            Intervals.fromScore(String(cmd.args[flagIndex - 2]))
        }

        val maxScore: Int by lazy {
            Intervals.upScore(String(cmd.args[flagIndex - 1]))
        }

        val minLex: String by lazy {
            Intervals.fromLex(String(cmd.args[flagIndex - 2]))
        }

        val maxLex: String by lazy {
            Intervals.upLex(String(cmd.args[flagIndex - 1]))
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
        validateAndSet()

        client.operate(
            null, this, defaultWritePolicy,
            key, getMapOperation()
        )
    }

    protected open fun validateAndSet() {
        require(!(rangeCommand.BYSCORE && rangeCommand.BYLEX)) { "[BYSCORE|BYLEX]" }
    }

    protected open fun getMapReturnType(): Int {
        return if (rangeCommand.WITHSCORES) {
            MapReturnType.KEY_VALUE
        } else {
            MapReturnType.KEY
        }
    }

    protected open fun getMapOperation(): Operation {
        return when {
            rangeCommand.BYSCORE -> {
                MapOperation.getByValueRange(
                    aeroCtx.bin,
                    Value.get(rangeCommand.minScore),
                    Value.get(rangeCommand.maxScore), getMapReturnType()
                )
            }
            rangeCommand.BYLEX -> {
                MapOperation.getByKeyRange(
                    aeroCtx.bin,
                    Value.get(rangeCommand.minLex),
                    Value.get(rangeCommand.maxLex), getMapReturnType()
                )
            }
            else -> {
                val from = if (rangeCommand.minScore == Int.MIN_VALUE) 0 else rangeCommand.minScore
                val count = rangeCommand.maxScore - from
                MapOperation.getByIndexRange(
                    aeroCtx.bin,
                    from,
                    count, getMapReturnType()
                )
            }
        }
    }

    override fun onSuccess(key: Key?, record: Record?) {
        try {
            if (record == null) {
                writeEmptyList()
            } else {
                writeResponse(record.bins[aeroCtx.bin])
            }
            flushCtxTransactionAware()
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
            writeArrayHeader(it.size * rangeCommand.factor)
        }
    }

    protected fun validateCommon() {
        require(!rangeCommand.BYSCORE) { "BYSCORE flag not supported" }
        require(!rangeCommand.BYLEX) { "BYLEX flag not supported" }
        require(!rangeCommand.REV) { "REV flag not supported" }
    }
}

class ZrevrangeCommandListener(
    ctx: ChannelHandlerContext
) : ZrangeCommandListener(ctx) {

    override fun validateAndSet() {
        validateCommon()
        require(rangeCommand.LIMIT?.let { false } ?: true) { "LIMIT flag not supported" }

        rangeCommand.REV = true
    }
}

open class ZrangebyscoreCommandListener(
    ctx: ChannelHandlerContext
) : ZrangeCommandListener(ctx) {

    override fun validateAndSet() {
        validateCommon()

        rangeCommand.BYSCORE = true
    }
}

class ZrevrangebyscoreCommandListener(
    ctx: ChannelHandlerContext
) : ZrangebyscoreCommandListener(ctx) {

    override fun validateAndSet() {
        super.validateAndSet()

        rangeCommand.REV = true
    }
}

open class ZrangebylexCommandListener(
    ctx: ChannelHandlerContext
) : ZrangeCommandListener(ctx) {

    override fun validateAndSet() {
        validateCommon()
        require(!rangeCommand.WITHSCORES) { "WITHSCORES flag not supported" }

        rangeCommand.BYLEX = true
    }
}

class ZrevrangebylexCommandListener(
    ctx: ChannelHandlerContext
) : ZrangebylexCommandListener(ctx) {

    override fun validateAndSet() {
        super.validateAndSet()

        rangeCommand.REV = true
    }
}
