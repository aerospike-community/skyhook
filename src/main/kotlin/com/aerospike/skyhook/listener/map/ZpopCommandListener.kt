package com.aerospike.skyhook.listener.map

import com.aerospike.client.Key
import com.aerospike.client.Record
import com.aerospike.client.Value
import com.aerospike.client.cdt.MapOperation
import com.aerospike.client.cdt.MapReturnType
import com.aerospike.client.listener.RecordListener
import com.aerospike.skyhook.command.RequestCommand
import com.aerospike.skyhook.config.AerospikeContext
import com.aerospike.skyhook.listener.BaseListener
import io.netty.channel.ChannelHandlerContext
import java.util.*

abstract class ZpopCommandListener(
    aeroCtx: AerospikeContext,
    ctx: ChannelHandlerContext
) : BaseListener(aeroCtx, ctx), RecordListener {

    protected var count: Int = 0

    override fun handle(cmd: RequestCommand) {
        require(cmd.argCount == 2 || cmd.argCount == 3) {
            argValidationErrorMsg(cmd)
        }

        val key = createKey(cmd.key)
        setCount(cmd)
        val record = aeroCtx.client.get(defaultWritePolicy, key).bins[aeroCtx.bin]

        aeroCtx.client.operate(
            null, this, defaultWritePolicy, key,
            MapOperation.removeByKeyList(aeroCtx.bin, getKeysToPop(record), MapReturnType.KEY_VALUE)
        )
    }

    private fun setCount(cmd: RequestCommand) {
        count = if (cmd.argCount == 3) String(cmd.args[2]).toInt() else 1
    }

    @Suppress("UNCHECKED_CAST")
    private fun getKeysToPop(data: Any?): List<Value> {
        val sorted = (data as TreeMap<*, Long>).toList()
            .sortedBy { it.second }.map { it.first }
        return take(sorted).map { Value.get(it) }
    }

    protected abstract fun take(sorted: List<Any>): List<Any>

    override fun onSuccess(key: Key?, record: Record?) {
        if (record == null) {
            writeEmptyList(ctx)
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

    @Suppress("UNCHECKED_CAST")
    private fun marshalOutput(data: Any?): List<String> {
        return sortOutput(data as List<Map.Entry<*, Long>>)
            .map { it.toPair().toList() }.flatten().map { it.toString() }
    }

    protected abstract fun sortOutput(list: List<Map.Entry<*, Long>>): List<Map.Entry<*, Long>>
}

class ZpopmaxCommandListener(
    aeroCtx: AerospikeContext,
    ctx: ChannelHandlerContext
) : ZpopCommandListener(aeroCtx, ctx) {

    override fun take(sorted: List<Any>): List<Any> {
        return sorted.takeLast(count)
    }

    override fun sortOutput(list: List<Map.Entry<*, Long>>): List<Map.Entry<*, Long>> {
        return list.sortedBy { it.value }.reversed()
    }
}

class ZpopminCommandListener(
    aeroCtx: AerospikeContext,
    ctx: ChannelHandlerContext
) : ZpopCommandListener(aeroCtx, ctx) {

    override fun take(sorted: List<Any>): List<Any> {
        return sorted.take(count)
    }

    override fun sortOutput(list: List<Map.Entry<*, Long>>): List<Map.Entry<*, Long>> {
        return list.sortedBy { it.value }
    }
}
