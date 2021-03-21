package com.aerospike.redispike.listener

import com.aerospike.client.AerospikeException
import com.aerospike.client.Bin
import com.aerospike.client.Key
import com.aerospike.client.Value
import com.aerospike.client.listener.WriteListener
import com.aerospike.redispike.command.RequestCommand
import com.aerospike.redispike.config.AerospikeContext
import com.aerospike.redispike.handler.CommandHandler
import com.aerospike.redispike.handler.NettyResponseWriter
import com.aerospike.redispike.util.Typed
import io.netty.channel.ChannelHandlerContext
import mu.KotlinLogging
import java.io.IOException

class SetCommandListener(
    private val aeroCtx: AerospikeContext,
    private val ctx: ChannelHandlerContext
) : NettyResponseWriter(), WriteListener, CommandHandler {

    companion object {
        private val log = KotlinLogging.logger(this::class.java.name)
    }

    @Throws(IOException::class)
    private fun writeResponse() {
        writeOK(ctx)
        ctx.flush()
    }

    override fun onSuccess(key: Key?) {
        try {
            writeResponse()
        } catch (e: IOException) {
            log.error(e) { "Error on onSuccess" }
        }
    }

    override fun onFailure(exception: AerospikeException?) {
        try {
            writeErrorString(ctx, "ERROR")
            ctx.flush()
        } catch (e: IOException) {
            log.error(e) { "Error on onFailure" }
        }
    }

    override fun handle(cmd: RequestCommand) {
        require(cmd.argCount == 3)

        val key = Key(aeroCtx.namespace, aeroCtx.set, cmd.args!![1])
        val value: Value = Typed.getValue(cmd.args[2])

        aeroCtx.client.put(null, this, null, key, Bin(aeroCtx.bin, value))
    }
}
