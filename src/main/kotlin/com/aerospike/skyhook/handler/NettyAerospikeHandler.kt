package com.aerospike.skyhook.handler

import com.aerospike.client.AerospikeException
import com.aerospike.client.IAerospikeClient
import com.aerospike.skyhook.command.RedisCommand
import com.aerospike.skyhook.command.RequestCommand
import com.aerospike.skyhook.config.AerospikeContext
import com.aerospike.skyhook.config.ServerConfiguration
import com.aerospike.skyhook.handler.aerospike.DbsizeCommandHandler
import com.aerospike.skyhook.handler.aerospike.FlushCommandHandler
import com.aerospike.skyhook.handler.redis.*
import com.aerospike.skyhook.listener.key.*
import com.aerospike.skyhook.listener.list.*
import com.aerospike.skyhook.listener.map.*
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
                RedisCommand.MSET,
                RedisCommand.MSETNX -> MsetCommandListener(aerospikeCtx, ctx).handle(cmd)
                RedisCommand.DEL,
                RedisCommand.UNLINK -> DelCommandListener(aerospikeCtx, ctx).handle(cmd)
                RedisCommand.MGET -> MgetCommandListener(aerospikeCtx, ctx).handle(cmd)
                RedisCommand.GETSET -> GetsetCommandListener(aerospikeCtx, ctx).handle(cmd)
                RedisCommand.EXISTS,
                RedisCommand.TOUCH -> ExistsCommandListener(aerospikeCtx, ctx).handle(cmd)
                RedisCommand.EXPIRE,
                RedisCommand.PEXPIRE,
                RedisCommand.EXPIREAT,
                RedisCommand.PEXPIREAT,
                RedisCommand.PERSIST -> ExpireCommandListener(aerospikeCtx, ctx).handle(cmd)
                RedisCommand.APPEND -> AppendCommandListener(aerospikeCtx, ctx).handle(cmd)
                RedisCommand.INCR,
                RedisCommand.INCRBY,
                RedisCommand.INCRBYFLOAT -> IncrCommandListener(aerospikeCtx, ctx).handle(cmd)
                RedisCommand.STRLEN -> StrlenCommandListener(aerospikeCtx, ctx).handle(cmd)
                RedisCommand.TTL,
                RedisCommand.PTTL -> TtlCommandListener(aerospikeCtx, ctx).handle(cmd)
                RedisCommand.RANDOMKEY -> RandomkeyCommandListener(aerospikeCtx, ctx).handle(cmd)

                RedisCommand.LPUSH,
                RedisCommand.LPUSHX,
                RedisCommand.RPUSH,
                RedisCommand.RPUSHX -> ListPushCommandListener(aerospikeCtx, ctx).handle(cmd)
                RedisCommand.LINDEX -> LindexCommandListener(aerospikeCtx, ctx).handle(cmd)
                RedisCommand.LLEN -> LlenCommandListener(aerospikeCtx, ctx).handle(cmd)
                RedisCommand.LPOP,
                RedisCommand.RPOP -> ListPopCommandListener(aerospikeCtx, ctx).handle(cmd)
                RedisCommand.LRANGE -> LrangeCommandListener(aerospikeCtx, ctx).handle(cmd)

                RedisCommand.HSET,
                RedisCommand.HSETNX -> HsetCommandListener(aerospikeCtx, ctx).handle(cmd)
                RedisCommand.HMSET -> HmsetCommandListener(aerospikeCtx, ctx).handle(cmd)
                RedisCommand.SADD -> SaddCommandListener(aerospikeCtx, ctx).handle(cmd)
                RedisCommand.HEXISTS,
                RedisCommand.SISMEMBER -> HexistsCommandListener(aerospikeCtx, ctx).handle(cmd)
                RedisCommand.HGET,
                RedisCommand.HMGET,
                RedisCommand.HGETALL,
                RedisCommand.HVALS,
                RedisCommand.HKEYS,
                RedisCommand.SMEMBERS -> MapGetCommandListener(aerospikeCtx, ctx).handle(cmd)
                RedisCommand.HINCRBY,
                RedisCommand.HINCRBYFLOAT -> HincrbyCommandListener(aerospikeCtx, ctx).handle(cmd)
                RedisCommand.HSTRLEN -> HstrlenCommandListener(aerospikeCtx, ctx).handle(cmd)
                RedisCommand.HLEN,
                RedisCommand.SCARD,
                RedisCommand.ZCARD -> MapSizeCommandListener(aerospikeCtx, ctx).handle(cmd)
                RedisCommand.HDEL,
                RedisCommand.SREM,
                RedisCommand.ZREM -> MapDelCommandListener(aerospikeCtx, ctx).handle(cmd)
                RedisCommand.SUNION -> SunionCommandListener(aerospikeCtx, ctx).handle(cmd)
                RedisCommand.SINTER -> SinterCommandListener(aerospikeCtx, ctx).handle(cmd)
                RedisCommand.SUNIONSTORE -> SunionstoreCommandListener(aerospikeCtx, ctx).handle(cmd)
                RedisCommand.SINTERSTORE -> SinterstoreCommandListener(aerospikeCtx, ctx).handle(cmd)

                RedisCommand.FLUSHDB,
                RedisCommand.FLUSHALL -> FlushCommandHandler(aerospikeCtx, ctx).handle(cmd)
                RedisCommand.DBSIZE -> DbsizeCommandHandler(aerospikeCtx, ctx).handle(cmd)

                RedisCommand.PING -> PingCommandHandler(ctx).handle(cmd)
                RedisCommand.ECHO -> EchoCommandHandler(ctx).handle(cmd)
                RedisCommand.TIME -> TimeCommandHandler(ctx).handle(cmd)
                RedisCommand.QUIT,
                RedisCommand.RESET,
                RedisCommand.SAVE,
                RedisCommand.BGSAVE -> MockCommandHandler(ctx).handle(cmd)
                RedisCommand.COMMAND -> CommandCommandHandler(ctx).handle(cmd)

                else -> {
                    writeErrorString(ctx, "${cmd.command} unsupported command")
                    ctx.flush()
                }
            }
        } catch (e: Exception) {
            val msg = when (e) {
                is AerospikeException -> "internal error"
                else -> e.message
            }
            log.warn { e }
            writeErrorString(ctx, msg)
            ctx.flush()
        }
    }
}
