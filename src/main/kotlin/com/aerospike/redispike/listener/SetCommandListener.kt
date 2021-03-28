package com.aerospike.redispike.listener

import com.aerospike.client.Bin
import com.aerospike.client.Key
import com.aerospike.client.Value
import com.aerospike.client.listener.WriteListener
import com.aerospike.client.policy.RecordExistsAction
import com.aerospike.client.policy.WritePolicy
import com.aerospike.redispike.command.RedisCommand
import com.aerospike.redispike.command.RequestCommand
import com.aerospike.redispike.config.AerospikeContext
import com.aerospike.redispike.util.Typed
import io.netty.channel.ChannelHandlerContext

class SetCommandListener(
    aeroCtx: AerospikeContext,
    ctx: ChannelHandlerContext
) : BaseListener(aeroCtx, ctx), WriteListener {

    private data class Params(val writePolicy: WritePolicy, val value: Value)

    override fun handle(cmd: RequestCommand) {
        val key = createKey(cmd.key)
        val params = parse(cmd)
        aeroCtx.client.put(
            null, this, params.writePolicy, key,
            Bin(aeroCtx.bin, params.value)
        )
    }

    override fun onSuccess(key: Key?) {
        try {
            writeOK(ctx)
            ctx.flush()
        } catch (e: Exception) {
            closeCtx(e)
        }
    }

    private fun parse(cmd: RequestCommand): Params {
        val writePolicy = getWritePolicy()
        return when (cmd.command) {
            RedisCommand.SET -> {
                require(cmd.argCount == 3) { argValidationErrorMsg(cmd) }

                Params(writePolicy, Typed.getValue(cmd.args!![2]))
            }
            RedisCommand.SETNX -> {
                require(cmd.argCount == 3) { argValidationErrorMsg(cmd) }

                writePolicy.recordExistsAction = RecordExistsAction.CREATE_ONLY
                Params(writePolicy, Typed.getValue(cmd.args!![2]))
            }
            RedisCommand.SETEX -> {
                require(cmd.argCount == 4) { argValidationErrorMsg(cmd) }

                writePolicy.expiration = Typed.getInteger(cmd.args!![2])
                Params(writePolicy, Typed.getValue(cmd.args[3]))
            }
            RedisCommand.PSETEX -> {
                require(cmd.argCount == 4) { argValidationErrorMsg(cmd) }

                writePolicy.expiration = Typed.getInteger(cmd.args!![2]) / 1000
                Params(writePolicy, Typed.getValue(cmd.args[3]))
            }
            else -> {
                throw IllegalArgumentException(cmd.command.toString())
            }
        }
    }
}
