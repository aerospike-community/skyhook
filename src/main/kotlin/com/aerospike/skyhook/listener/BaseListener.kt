package com.aerospike.skyhook.listener

import com.aerospike.client.AerospikeException
import com.aerospike.client.Key
import com.aerospike.client.Value
import com.aerospike.client.policy.RecordExistsAction
import com.aerospike.client.policy.WritePolicy
import com.aerospike.skyhook.command.RequestCommand
import com.aerospike.skyhook.config.AerospikeContext
import com.aerospike.skyhook.handler.CommandHandler
import com.aerospike.skyhook.handler.NettyResponseWriter
import io.netty.channel.ChannelHandlerContext
import mu.KotlinLogging
import java.io.IOException

abstract class BaseListener(
    protected val aeroCtx: AerospikeContext,
    protected val ctx: ChannelHandlerContext
) : NettyResponseWriter(), CommandHandler {

    companion object {

        @JvmStatic
        val log = KotlinLogging.logger {}

        @JvmStatic
        fun argValidationErrorMsg(cmd: RequestCommand): String {
            return "${cmd.command} arguments"
        }

        @JvmStatic
        internal val updateOnlyPolicy = run {
            val updateOnlyPolicy = getWritePolicy()
            updateOnlyPolicy.recordExistsAction = RecordExistsAction.UPDATE_ONLY
            updateOnlyPolicy
        }

        @JvmStatic
        internal val createOnlyPolicy = run {
            val updateOnlyPolicy = getWritePolicy()
            updateOnlyPolicy.recordExistsAction = RecordExistsAction.CREATE_ONLY
            updateOnlyPolicy
        }

        @JvmStatic
        internal val defaultWritePolicy: WritePolicy = getWritePolicy()

        @JvmStatic
        internal fun getWritePolicy(): WritePolicy {
            val writePolicy = WritePolicy()
            writePolicy.sendKey = true
            return writePolicy
        }
    }

    @Throws(IOException::class)
    protected open fun writeResponse(mapped: Any?) {
        writeObject(ctx, mapped)
    }

    @Throws(IOException::class)
    protected open fun writeError(e: AerospikeException?) {
        writeErrorString(ctx, "internal error")
    }

    open fun onFailure(exception: AerospikeException?) {
        try {
            log.debug { exception }
            writeError(exception)
            ctx.flush()
        } catch (e: IOException) {
            ctx.close()
            log.error(e) { "Exception at onFailure" }
        }
    }

    protected fun createKey(key: Value): Key {
        return Key(aeroCtx.namespace, aeroCtx.set, key)
    }

    protected fun closeCtx(e: Exception?) {
        log.error(e) { "${this.javaClass.simpleName} error" }
        ctx.close()
    }
}
