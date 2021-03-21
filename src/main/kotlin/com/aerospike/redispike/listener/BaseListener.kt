package com.aerospike.redispike.listener

import com.aerospike.client.AerospikeException
import com.aerospike.client.Key
import com.aerospike.client.Record
import com.aerospike.client.listener.RecordListener
import com.aerospike.redispike.config.AerospikeContext
import com.aerospike.redispike.handler.CommandHandler
import com.aerospike.redispike.handler.NettyResponseWriter
import io.netty.channel.ChannelHandlerContext
import mu.KotlinLogging
import java.io.IOException

abstract class BaseListener(
    private val aeroCtx: AerospikeContext,
    private val ctx: ChannelHandlerContext
) : NettyResponseWriter(), RecordListener, CommandHandler {

    companion object {
        private val log = KotlinLogging.logger(this::class.java.name)
    }

    @Throws(IOException::class)
    open fun writeResponse(mapped: Any?) {
        writeObject(ctx, mapped)
    }

    @Throws(IOException::class)
    open fun writeFailedMapping(rec: Record?, e: Exception?) {
        writeErrorString(ctx, "Incorrect bin type")
    }

    @Throws(IOException::class)
    open fun writeError(e: AerospikeException?) {
        writeErrorString(ctx, "ERROR")
    }

    override fun onSuccess(key: Key?, record: Record?) {
        if (record == null) {
            writeNullString(ctx)
            ctx.flush()
        } else {
            try {
                writeResponse(record.bins[aeroCtx.bin]!!)
                ctx.flush()
            } catch (e: Exception) {
                try {
                    writeFailedMapping(record, e)
                    ctx.flush()
                } catch (ioe: IOException) {
                    ctx.flush()
                    ctx.close()
                }
            }
        }
    }

    override fun onFailure(exception: AerospikeException?) {
        try {
            writeError(exception)
            ctx.flush()
        } catch (e: IOException) {
            ctx.close()
            log.error(e) { "Exception at onFailure" }
        }
    }
}
