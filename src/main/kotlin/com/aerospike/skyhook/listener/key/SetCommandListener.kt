package com.aerospike.skyhook.listener.key

import com.aerospike.client.AerospikeException
import com.aerospike.client.Bin
import com.aerospike.client.Key
import com.aerospike.client.Value
import com.aerospike.client.listener.WriteListener
import com.aerospike.client.policy.RecordExistsAction
import com.aerospike.client.policy.WritePolicy
import com.aerospike.skyhook.command.RedisCommand
import com.aerospike.skyhook.command.RequestCommand
import com.aerospike.skyhook.listener.BaseListener
import com.aerospike.skyhook.util.Typed
import io.netty.channel.ChannelHandlerContext

class SetCommandListener(
    ctx: ChannelHandlerContext
) : BaseListener(ctx), WriteListener {

    @Volatile
    private lateinit var command: RedisCommand

    private data class Params(val writePolicy: WritePolicy, val value: Value)

    override fun handle(cmd: RequestCommand) {
        command = cmd.command
        val key = createKey(cmd.key)
        val params = parse(cmd)
        client.put(
            null, this, params.writePolicy, key,
            Bin(aeroCtx.bin, params.value), stringTypeBin()
        )
    }

    override fun onSuccess(key: Key?) {
        try {
            if (command == RedisCommand.SETNX) {
                writeLong(ctx, 1L)
            } else {
                writeOK(ctx)
            }
            ctx.flush()
        } catch (e: Exception) {
            closeCtx(e)
        }
    }

    override fun writeError(e: AerospikeException?) {
        if (command == RedisCommand.SETNX) {
            writeLong(ctx, 0L)
        } else {
            writeErrorString(ctx, "internal error")
        }
    }

    private fun parse(cmd: RequestCommand): Params {
        val writePolicy = getWritePolicy()
        return when (cmd.command) {
            RedisCommand.SET -> {
                require(cmd.argCount == 3) { argValidationErrorMsg(cmd) }

                Params(writePolicy, Typed.getValue(cmd.args[2]))
            }
            RedisCommand.SETNX -> {
                require(cmd.argCount == 3) { argValidationErrorMsg(cmd) }

                writePolicy.recordExistsAction = RecordExistsAction.CREATE_ONLY
                Params(writePolicy, Typed.getValue(cmd.args[2]))
            }
            RedisCommand.SETEX -> {
                require(cmd.argCount == 4) { argValidationErrorMsg(cmd) }

                writePolicy.expiration = Typed.getInteger(cmd.args[2])
                Params(writePolicy, Typed.getValue(cmd.args[3]))
            }
            RedisCommand.PSETEX -> {
                require(cmd.argCount == 4) { argValidationErrorMsg(cmd) }

                writePolicy.expiration = Typed.getInteger(cmd.args[2]) / 1000
                Params(writePolicy, Typed.getValue(cmd.args[3]))
            }
            else -> {
                throw IllegalArgumentException(cmd.command.toString())
            }
        }
    }
}
