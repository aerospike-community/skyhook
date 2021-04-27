package com.aerospike.skyhook.listener.map

import com.aerospike.client.Key
import com.aerospike.client.Operation
import com.aerospike.client.Record
import com.aerospike.client.cdt.MapOperation
import com.aerospike.client.cdt.MapReturnType
import com.aerospike.client.listener.RecordListener
import com.aerospike.skyhook.command.RequestCommand
import com.aerospike.skyhook.config.AerospikeContext
import com.aerospike.skyhook.listener.BaseListener
import io.netty.channel.ChannelHandlerContext
import java.util.concurrent.atomic.AtomicInteger

class ZrandmemberCommandListener(
    aeroCtx: AerospikeContext,
    ctx: ChannelHandlerContext
) : BaseListener(aeroCtx, ctx), RecordListener {

    @Volatile
    private lateinit var zrandmemberCommand: ZrandmemberCommand

    private class ZrandmemberCommand(val cmd: RequestCommand) {

        private val total: AtomicInteger = AtomicInteger()

        val count by lazy {
            if (cmd.argCount == 2) 1 else String(cmd.args[2]).toInt()
        }

        val withScores by lazy {
            if (cmd.argCount < 4) {
                false
            } else {
                require(String(cmd.args[3]) == "WITHSCORES") {
                    argValidationErrorMsg(cmd)
                }
                true
            }
        }

        val step by lazy {
            if (withScores) 2 else 1
        }

        fun set(size: Int) {
            total.set(size * step)
        }

        fun get(): Int {
            return total.get()
        }

        fun decrementAndGet(): Int {
            return total.addAndGet(-step)
        }
    }

    override fun handle(cmd: RequestCommand) {
        require(cmd.argCount >= 2 || cmd.argCount <= 4) {
            argValidationErrorMsg(cmd)
        }

        zrandmemberCommand = ZrandmemberCommand(cmd)
        val key = createKey(cmd.key)
        val indexList = getRandomList(key)

        zrandmemberCommand.set(indexList.size)
        if (zrandmemberCommand.get() > 1) {
            writeArrayHeader(ctx, zrandmemberCommand.get().toLong())
        }
        for (i: Int in indexList) {
            aeroCtx.client.operate(
                null, this, defaultWritePolicy,
                key, getMapOperation(i)
            )
        }
    }

    private fun getRandomList(key: Key): List<Int> {
        val setSize = getSetSize(key)
        return if (zrandmemberCommand.count >= 0) {
            val count = minOf(zrandmemberCommand.count, setSize)
            val mutableSet: MutableSet<Int> = mutableSetOf()
            while (mutableSet.size < count) {
                mutableSet.add((0 until setSize).random())
            }
            mutableSet.toList()
        } else {
            val count = kotlin.math.abs(zrandmemberCommand.count)
            val mutableList: MutableList<Int> = mutableListOf()
            while (mutableList.size < count) {
                mutableList.add((0 until setSize).random())
            }
            mutableList.toList()
        }
    }

    private fun getMapOperation(i: Int): Operation {
        val returnType = if (zrandmemberCommand.withScores) {
            MapReturnType.KEY_VALUE
        } else {
            MapReturnType.KEY
        }
        return MapOperation.getByIndex(aeroCtx.bin, i, returnType)
    }

    private fun getSetSize(key: Key): Int {
        return aeroCtx.client.operate(
            defaultWritePolicy, key,
            MapOperation.size(aeroCtx.bin)
        )?.getInt(aeroCtx.bin) ?: 0
    }

    override fun onSuccess(key: Key?, record: Record?) {
        try {
            if (record == null) {
                writeNullString(ctx)
            } else {
                synchronized(zrandmemberCommand) {
                    writeResponse(record.bins[aeroCtx.bin])
                }
            }
            if (zrandmemberCommand.decrementAndGet() == 0) {
                ctx.flush()
            }
        } catch (e: Exception) {
            closeCtx(e)
        }
    }

    override fun writeResponse(mapped: Any?) {
        when (mapped) {
            is Map<*, *> -> mapped.toList().forEach {
                super.writeResponse(it.first.toString())
                super.writeResponse(it.second.toString())
            }
            is List<*> -> when (mapped.firstOrNull()) {
                is Map.Entry<*, *> -> mapped.map { it as Map.Entry<*, *> }.map { it.toPair() }
                    .forEach {
                        super.writeResponse(it.first.toString())
                        super.writeResponse(it.second.toString())
                    }
                else -> mapped.forEach {
                    super.writeResponse(it.toString())
                }
            }
            else -> {
                super.writeResponse(mapped)
            }
        }
    }
}
