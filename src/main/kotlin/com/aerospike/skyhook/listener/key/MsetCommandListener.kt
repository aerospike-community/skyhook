package com.aerospike.skyhook.listener.key

import com.aerospike.client.Bin
import com.aerospike.client.Key
import com.aerospike.client.Value
import com.aerospike.client.listener.WriteListener
import com.aerospike.skyhook.command.RedisCommand
import com.aerospike.skyhook.command.RequestCommand
import com.aerospike.skyhook.config.AerospikeContext
import com.aerospike.skyhook.listener.BaseListener
import com.aerospike.skyhook.util.Typed
import io.netty.channel.ChannelHandlerContext

class MsetCommandListener(
    aeroCtx: AerospikeContext,
    ctx: ChannelHandlerContext
) : BaseListener(aeroCtx, ctx), WriteListener {

    @Volatile
    private lateinit var command: RedisCommand

    @Volatile
    private var total: Int = 0

    private val lock = Any()

    /**
     * The commands implementation is not atomic.
     */
    override fun handle(cmd: RequestCommand) {
        require(cmd.argCount >= 3 && cmd.argCount % 2 == 1) {
            argValidationErrorMsg(cmd)
        }

        command = cmd.command
        val values = getValues(cmd)
        if (!handleNX(cmd, values.keys.toTypedArray())) return

        total = values.size
        values.forEach { (k, v) ->
            aeroCtx.client.put(
                null, this, defaultWritePolicy, k,
                Bin(aeroCtx.bin, v), stringTypeBin()
            )
        }
    }

    private fun handleNX(cmd: RequestCommand, keys: Array<Key>): Boolean {
        if (cmd.command == RedisCommand.MSETNX) {
            if (!aeroCtx.client.exists(null, keys).all { !it }) {
                writeLong(ctx, 0L)
                ctx.flush()
                return false
            }
        }
        return true
    }

    private fun getValues(cmd: RequestCommand): Map<Key, Value> {
        return cmd.args.drop(1).chunked(2)
            .map { (it1, it2) -> createKey(it1) to Typed.getValue(it2) }
            .toMap()
    }

    override fun onSuccess(key: Key?) {
        try {
            synchronized(lock) {
                total--
                if (total == 0) {
                    if (command == RedisCommand.MSETNX) {
                        writeLong(ctx, 1L)
                    } else {
                        writeOK(ctx)
                    }
                    ctx.flush()
                }
            }
        } catch (e: Exception) {
            closeCtx(e)
        }
    }
}
