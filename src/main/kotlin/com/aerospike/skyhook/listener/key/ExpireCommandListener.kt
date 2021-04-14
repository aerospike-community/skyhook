package com.aerospike.skyhook.listener.key

import com.aerospike.client.AerospikeException
import com.aerospike.client.Key
import com.aerospike.client.listener.WriteListener
import com.aerospike.client.policy.WritePolicy
import com.aerospike.skyhook.command.RedisCommand
import com.aerospike.skyhook.command.RequestCommand
import com.aerospike.skyhook.config.AerospikeContext
import com.aerospike.skyhook.listener.BaseListener
import com.aerospike.skyhook.util.Typed
import io.netty.channel.ChannelHandlerContext

class ExpireCommandListener(
    aeroCtx: AerospikeContext,
    ctx: ChannelHandlerContext
) : BaseListener(aeroCtx, ctx), WriteListener {

    override fun handle(cmd: RequestCommand) {
        val key = createKey(cmd.key)
        val writePolicy = getPolicy(cmd)
        aeroCtx.client.touch(null, this, writePolicy, key)
    }

    override fun onSuccess(key: Key?) {
        try {
            writeLong(ctx, 1L)
            ctx.flush()
        } catch (e: Exception) {
            closeCtx(e)
        }
    }

    override fun writeError(e: AerospikeException?) {
        writeLong(ctx, 0L)
    }

    private fun getPolicy(cmd: RequestCommand): WritePolicy {
        val writePolicy = getWritePolicy()
        when (cmd.command) {
            RedisCommand.EXPIRE -> {
                require(cmd.argCount == 3) { argValidationErrorMsg(cmd) }

                writePolicy.expiration = Typed.getInteger(cmd.args[2])
            }
            RedisCommand.PEXPIRE -> {
                require(cmd.argCount == 3) { argValidationErrorMsg(cmd) }

                writePolicy.expiration = Typed.getInteger(cmd.args[2]) / 1000
            }
            RedisCommand.PERSIST -> {
                require(cmd.argCount == 2) { argValidationErrorMsg(cmd) }

                writePolicy.expiration = -1
            }
            else -> {
                throw IllegalArgumentException(cmd.command.toString())
            }
        }
        return writePolicy
    }
}
