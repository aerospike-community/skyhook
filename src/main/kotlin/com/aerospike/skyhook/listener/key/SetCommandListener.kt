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
import com.aerospike.skyhook.listener.ValueType
import com.aerospike.skyhook.util.Typed
import io.netty.channel.ChannelHandlerContext
import java.lang.Integer.max

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
            Bin(aeroCtx.bin, params.value), *systemBins(ValueType.STRING)
        )
    }

    override fun onSuccess(key: Key?) {
        try {
            if (command == RedisCommand.SETNX) {
                writeLong(1L)
            } else {
                writeOK()
            }
            flushCtxTransactionAware()
        } catch (e: Exception) {
            closeCtx(e)
        }
    }

    override fun writeError(e: AerospikeException?) {
        if (command == RedisCommand.SETNX) {
            writeLong(0L)
        } else {
            writeErrorString("Internal error")
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

                val expSeconds = Typed.getInteger(cmd.args[2])
                require(expSeconds > 0) { "invalid expiration" }
                writePolicy.expiration = expSeconds
                Params(writePolicy, Typed.getValue(cmd.args[3]))
            }
            RedisCommand.PSETEX -> {
                require(cmd.argCount == 4) { argValidationErrorMsg(cmd) }

                val expMillis = Typed.getInteger(cmd.args[2])
                require(expMillis > 0) { "invalid expiration" }
                writePolicy.expiration = max(expMillis / 1000, 1)
                Params(writePolicy, Typed.getValue(cmd.args[3]))
            }
            else -> {
                throw IllegalArgumentException(cmd.command.toString())
            }
        }
    }
}
