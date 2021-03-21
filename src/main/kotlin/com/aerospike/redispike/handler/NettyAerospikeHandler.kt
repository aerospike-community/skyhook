package com.aerospike.redispike.handler

import com.aerospike.client.IAerospikeClient
import com.aerospike.redispike.command.RedisCommand
import com.aerospike.redispike.command.RequestCommand
import com.aerospike.redispike.config.AerospikeContext
import com.aerospike.redispike.config.ServerConfiguration
import com.aerospike.redispike.listener.DelCommandListener
import com.aerospike.redispike.listener.GetCommandListener
import com.aerospike.redispike.listener.SetCommandListener
import io.netty.channel.ChannelHandlerContext
import mu.KotlinLogging
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NettyAerospikeHandler @Inject constructor(
    client: IAerospikeClient,
    config: ServerConfiguration
) : NettyResponseWriter() {

    companion object {
        private val log = KotlinLogging.logger(this::class.java.name)
    }

    private val aerospikeCtx: AerospikeContext = AerospikeContext(
        client,
        config.namespase,
        config.set,
        config.bin
    )

    /**
     * Handle the input command. Listeners are responsible to send the response
     * to the client.
     */
    fun handleCommand(cmd: RequestCommand, ctx: ChannelHandlerContext) {

        when (cmd.getCommand()) {
            RedisCommand.GET -> GetCommandListener(aerospikeCtx, ctx).handle(cmd)
            RedisCommand.SET -> SetCommandListener(aerospikeCtx, ctx).handle(cmd)
            RedisCommand.DEL -> DelCommandListener(aerospikeCtx, ctx).handle(cmd)

            else -> {
                val errorString = "Unknown RedisCommand: " + cmd.getCommand()
                log.warn { errorString }
                writeErrorString(ctx, errorString)
                ctx.flush()
            }
        }
    }
}
