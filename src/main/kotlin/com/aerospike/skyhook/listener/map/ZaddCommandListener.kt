package com.aerospike.skyhook.listener.map

import com.aerospike.client.Key
import com.aerospike.client.Operation
import com.aerospike.client.Record
import com.aerospike.client.Value
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

class ZaddCommandListener(
    aeroCtx: AerospikeContext,
    ctx: ChannelHandlerContext
) : BaseListener(aeroCtx, ctx), RecordListener {

    private class ZaddCommand(val cmd: RequestCommand) {
        var XX: Boolean = false
            private set
        var NX: Boolean = false
            private set
        var LT: Boolean = false
            private set
        var GT: Boolean = false
            private set
        var CH: Boolean = false
            private set
        var INCR: Boolean = false
            private set

        lateinit var values: Map<Value, Value>
            private set

        init {
            for (i in 2 until cmd.args.size) {
                if (!setFlag(String(cmd.args[i]))) {
                    setSortedSetValues(i)
                    break
                }
            }
            validate()
        }

        private fun setFlag(flagStr: String): Boolean {
            when (flagStr.toUpperCase()) {
                "XX" -> XX = true
                "NX" -> NX = true
                "LT" -> LT = true
                "GT" -> GT = true
                "CH" -> CH = true
                "INCR" -> INCR = true
                else -> return false
            }
            return true
        }

        private fun setSortedSetValues(from: Int) {
            values = cmd.args.drop(from).chunked(2)
                .map { (it1, it2) ->
                    Typed.getStringValue(it2) to
                            Value.LongValue(Typed.getLong(it1))
                }
                .toMap()
        }

        private fun validate() {
            require(!(NX && XX)) { "[NX|XX]" }
            require(!(GT && LT)) { "[GT|LT]" }
            require(!LT) { "LT flag not supported" }
            require(!GT) { "GT flag not supported" }
            require(!CH) { "CH flag not supported" }
        }
    }

    @Volatile
    private var size: Long = 0L

    @Volatile
    private lateinit var zaddCommand: ZaddCommand

    override fun handle(cmd: RequestCommand) {
        require(cmd.argCount >= 4) { argValidationErrorMsg(cmd) }

        val key = createKey(cmd.key)
        zaddCommand = ZaddCommand(cmd)

        val getSize = MapOperation.size(aeroCtx.bin)
        size = aeroCtx.client.operate(defaultWritePolicy, key, getSize)
            ?.getLong(aeroCtx.bin) ?: 0L

        aeroCtx.client.operate(
            null, this, defaultWritePolicy,
            key, getMapOperation()
        )
    }

    private fun getMapOperation(): Operation {
        return when {
            zaddCommand.INCR -> {
                require(zaddCommand.values.size == 1) { "INCR params" }
                MapOperation.increment(
                    getMapPolicy(),
                    aeroCtx.bin,
                    zaddCommand.values.keys.first(),
                    zaddCommand.values.values.first()
                )
            }
            else -> {
                MapOperation.putItems(
                    getMapPolicy(),
                    aeroCtx.bin,
                    zaddCommand.values
                )
            }
        }
    }

    private fun getMapPolicy(): MapPolicy {
        val writeFlag = when {
            zaddCommand.XX -> {
                MapWriteFlags.UPDATE_ONLY
            }
            zaddCommand.NX -> {
                MapWriteFlags.CREATE_ONLY
            }
            else -> {
                MapWriteFlags.DEFAULT
            }
        }
        return MapPolicy(MapOrder.KEY_VALUE_ORDERED, writeFlag)
    }

    override fun onSuccess(key: Key?, record: Record?) {
        if (record == null) {
            writeLong(ctx, 0L)
            ctx.flush()
        } else {
            try {
                if (zaddCommand.INCR) {
                    writeResponse(record.getString(aeroCtx.bin))
                } else {
                    val added = record.getLong(aeroCtx.bin) - size
                    writeLong(ctx, added)
                }
                ctx.flush()
            } catch (e: Exception) {
                closeCtx(e)
            }
        }
    }
}
