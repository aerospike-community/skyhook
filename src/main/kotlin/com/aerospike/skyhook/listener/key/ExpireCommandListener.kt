package com.aerospike.skyhook.listener.key

import com.aerospike.client.AerospikeException
import com.aerospike.client.Key
import com.aerospike.client.listener.DeleteListener
import com.aerospike.client.listener.WriteListener
import com.aerospike.client.policy.WritePolicy
import com.aerospike.skyhook.command.RedisCommand
import com.aerospike.skyhook.command.RequestCommand
import com.aerospike.skyhook.listener.BaseListener
import com.aerospike.skyhook.util.Typed
import io.netty.channel.ChannelHandlerContext

class ExpireCommandListener(
    ctx: ChannelHandlerContext
) : BaseListener(ctx), WriteListener, DeleteListener {

    private var del: Boolean = false

    override fun handle(cmd: RequestCommand) {
        val key = createKey(cmd.key)
        val writePolicy = getPolicy(cmd)
        if (del) {
            client.delete(null, this, writePolicy, key)
        } else {
            client.touch(null, this, writePolicy, key)
        }
    }

    override fun onSuccess(key: Key?) {
        try {
            writeLong(1L)
            flushCtxTransactionAware()
        } catch (e: Exception) {
            closeCtx(e)
        }
    }

    override fun onSuccess(key: Key?, existed: Boolean) {
        try {
            val returnValue = if (existed) 1L else 0L
            writeLong(returnValue)
            flushCtxTransactionAware()
        } catch (e: Exception) {
            closeCtx(e)
        }
    }

    override fun writeError(e: AerospikeException?) {
        writeLong(0L)
    }

    private fun getPolicy(cmd: RequestCommand): WritePolicy {
        val writePolicy = getWritePolicy()
        when (cmd.command) {
            RedisCommand.EXPIRE -> {
                require(cmd.argCount == 3) { argValidationErrorMsg(cmd) }

                writePolicy.expiration = fromMillis(Typed.getLong(cmd.args[2]) * 1000)
            }
            RedisCommand.PEXPIRE -> {
                require(cmd.argCount == 3) { argValidationErrorMsg(cmd) }

                writePolicy.expiration = fromMillis(Typed.getLong(cmd.args[2]))
            }
            RedisCommand.EXPIREAT -> {
                require(cmd.argCount == 3) { argValidationErrorMsg(cmd) }

                writePolicy.expiration = fromTimestamp(Typed.getLong(cmd.args[2]) * 1000)
            }
            RedisCommand.PEXPIREAT -> {
                require(cmd.argCount == 3) { argValidationErrorMsg(cmd) }

                writePolicy.expiration = fromTimestamp(Typed.getLong(cmd.args[2]))
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

    private fun fromTimestamp(ts: Long): Int {
        return fromMillis(ts - System.currentTimeMillis())
    }

    private fun fromMillis(millis: Long): Int {
        val expireSeconds = millis / 1000
        if (expireSeconds < 1) {
            del = true
            return 0
        }
        return expireSeconds.toInt()
    }
}
