package com.aerospike.redispike.handler

import com.aerospike.client.IAerospikeClient
import com.aerospike.redispike.command.RedisCommand
import com.aerospike.redispike.command.RequestCommand
import com.aerospike.redispike.config.AerospikeContext
import com.aerospike.redispike.config.ServerConfiguration
import com.aerospike.redispike.listener.*
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
        try {
            when (cmd.command) {
                RedisCommand.GET -> GetCommandListener(aerospikeCtx, ctx).handle(cmd)
                RedisCommand.SET,
                RedisCommand.SETNX,
                RedisCommand.SETEX,
                RedisCommand.PSETEX -> SetCommandListener(aerospikeCtx, ctx).handle(cmd)
                RedisCommand.DEL -> DelCommandListener(aerospikeCtx, ctx).handle(cmd)
                RedisCommand.MGET -> MgetCommandListener(aerospikeCtx, ctx).handle(cmd)
                RedisCommand.GETSET -> GetsetCommandListener(aerospikeCtx, ctx).handle(cmd)
                RedisCommand.EXISTS -> ExistsCommandListener(aerospikeCtx, ctx).handle(cmd)
                RedisCommand.EXPIRE,
                RedisCommand.PEXPIRE -> ExpireCommandListener(aerospikeCtx, ctx).handle(cmd)
                RedisCommand.APPEND -> AppendCommandListener(aerospikeCtx, ctx).handle(cmd)
                RedisCommand.INCR,
                RedisCommand.INCRBY,
                RedisCommand.INCRBYFLOAT -> IncrCommandListener(aerospikeCtx, ctx).handle(cmd)
                RedisCommand.STRLEN -> StrlenCommandListener(aerospikeCtx, ctx).handle(cmd)
                RedisCommand.TTL,
                RedisCommand.PTTL -> TtlCommandListener(aerospikeCtx, ctx).handle(cmd)

                else -> {
                    val errorString = "Unsupported RedisCommand: " + cmd.command
                    log.warn { errorString }
                    writeErrorString(ctx, errorString)
                    ctx.flush()
                }
            }
        } catch (e: Exception) {
            log.warn(e) { "Exception on handleCommand" }
            writeErrorString(ctx, e.message)
            ctx.flush()
        }
    }
}
