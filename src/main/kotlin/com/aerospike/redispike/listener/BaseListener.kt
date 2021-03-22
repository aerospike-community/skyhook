package com.aerospike.redispike.listener

import com.aerospike.client.AerospikeException
import com.aerospike.client.Key
import com.aerospike.client.Record
import com.aerospike.client.Value
import com.aerospike.client.policy.RecordExistsAction
import com.aerospike.client.policy.WritePolicy
import com.aerospike.redispike.config.AerospikeContext
import com.aerospike.redispike.handler.CommandHandler
import com.aerospike.redispike.handler.NettyResponseWriter
import io.netty.channel.ChannelHandlerContext
import mu.KotlinLogging
import java.io.IOException

abstract class BaseListener(
    protected val aeroCtx: AerospikeContext,
    protected val ctx: ChannelHandlerContext
) : NettyResponseWriter(), CommandHandler {

    companion object {
        protected val log = KotlinLogging.logger {}

        @JvmStatic
        protected val updateOnlyPolicy = run {
            val updateOnlyPolicy = WritePolicy()
            updateOnlyPolicy.recordExistsAction = RecordExistsAction.UPDATE_ONLY
            updateOnlyPolicy
        }
    }

    @Throws(IOException::class)
    protected open fun writeResponse(mapped: Any?) {
        writeObject(ctx, mapped)
    }

    @Throws(IOException::class)
    protected open fun writeFailedMapping(rec: Record?, e: Exception?) {
        writeErrorString(ctx, "Incorrect bin type")
    }

    @Throws(IOException::class)
    protected open fun writeError(e: AerospikeException?) {
        writeErrorString(ctx, e?.message)
    }

    open fun onFailure(exception: AerospikeException?) {
        try {
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
