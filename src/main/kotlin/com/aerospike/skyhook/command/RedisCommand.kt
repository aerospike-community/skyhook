package com.aerospike.skyhook.command

import com.aerospike.skyhook.handler.CommandHandler
import com.aerospike.skyhook.handler.aerospike.*
import com.aerospike.skyhook.handler.redis.*
import com.aerospike.skyhook.listener.key.*
import com.aerospike.skyhook.listener.list.*
import com.aerospike.skyhook.listener.map.*
import com.aerospike.skyhook.listener.scan.HscanCommandListener
import com.aerospike.skyhook.listener.scan.ScanCommandListener
import com.aerospike.skyhook.listener.scan.SscanCommandListener
import com.aerospike.skyhook.listener.scan.ZscanCommandListener
import com.aerospike.skyhook.util.RedisCommandsDetails.appendCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.authCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.bgsaveCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.commandCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.dbsizeCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.decrCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.decrbyCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.delCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.discardCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.echoCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.execCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.existsCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.expireCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.expireatCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.flushallCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.flushdbCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.getCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.getsetCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.hdelCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.hexistsCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.hgetCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.hgetallCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.hincrbyCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.hincrbyfloatCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.hkeysCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.hlenCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.hmgetCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.hmsetCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.hscanCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.hsetCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.hsetnxCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.hstrlenCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.hvalsCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.incrCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.incrbyCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.incrbyfloatCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.lindexCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.llenCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.lolwutCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.lpopCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.lpushCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.lpushxCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.lrangeCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.mgetCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.msetCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.msetnxCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.multiCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.persistCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.pexpireCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.pexpireatCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.pingCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.psetexCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.pttlCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.randomkeyCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.resetCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.rpopCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.rpushCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.rpushxCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.saddCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.saveCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.scanCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.scardCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.setCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.setexCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.setnxCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.sinterCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.sinterstoreCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.sismemberCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.smembersCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.sremCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.sscanCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.strlenCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.sunionCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.sunionstoreCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.timeCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.touchCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.ttlCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.typeCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.unlinkCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.zaddCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.zcardCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.zcountCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.zincrbyCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.zlexcountCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.zmscoreCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.zpopmaxCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.zpopminCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.zrandmemberCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.zrangeCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.zrangebylexCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.zrangebyscoreCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.zrangestoreCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.zrankCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.zremCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.zremrangebylexCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.zremrangebyrankCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.zremrangebyscoreCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.zrevrangeCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.zrevrangebylexCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.zrevrangebyscoreCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.zscanCommand
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.redis.ArrayHeaderRedisMessage
import mu.KotlinLogging
import java.util.*
import kotlin.reflect.KFunction1

enum class RedisCommand(
    val details: RedisCommandDetails?,
    val newHandler: KFunction1<ChannelHandlerContext, CommandHandler>
) {
    GET(getCommand, ::GetCommandListener),
    MGET(mgetCommand, ::MgetCommandListener),
    GETSET(getsetCommand, ::GetsetCommandListener),
    SET(setCommand, ::SetCommandListener),
    SETEX(setexCommand, ::SetCommandListener),
    PSETEX(psetexCommand, ::SetCommandListener),
    SETNX(setnxCommand, ::SetCommandListener),
    MSET(msetCommand, ::MsetCommandListener),
    MSETNX(msetnxCommand, ::MsetCommandListener),
    EXISTS(existsCommand, ::ExistsCommandListener),
    EXPIRE(expireCommand, ::ExpireCommandListener),
    PEXPIRE(pexpireCommand, ::ExpireCommandListener),
    EXPIREAT(expireatCommand, ::ExpireCommandListener),
    PEXPIREAT(pexpireatCommand, ::ExpireCommandListener),
    PERSIST(persistCommand, ::ExpireCommandListener),
    APPEND(appendCommand, ::AppendCommandListener),
    INCR(incrCommand, ::IncrCommandListener),
    INCRBY(incrbyCommand, ::IncrCommandListener),
    INCRBYFLOAT(incrbyfloatCommand, ::IncrCommandListener),
    DECR(decrCommand, ::DecrCommandListener),
    DECRBY(decrbyCommand, ::DecrCommandListener),
    STRLEN(strlenCommand, ::StrlenCommandListener),
    TTL(ttlCommand, ::TtlCommandListener),
    PTTL(pttlCommand, ::TtlCommandListener),
    DEL(delCommand, ::DelCommandListener),
    UNLINK(unlinkCommand, ::DelCommandListener),
    RANDOMKEY(randomkeyCommand, ::RandomkeyCommandListener),

    /**
     * The Redis TOUCH command changes the last_access_time of the key (used for LRU eviction).
     * Aerospike (currently) doesn't have such metadata on the record.
     * The actual implementation returns the number of records that were 'touched' using
     * [com.aerospike.client.AerospikeClient.exists].
     */
    TOUCH(touchCommand, ::ExistsCommandListener),
    TYPE(typeCommand, ::TypeCommandListener),

    LPUSH(lpushCommand, ::ListPushCommandListener),
    LPUSHX(lpushxCommand, ::ListPushCommandListener),
    RPUSH(rpushCommand, ::ListPushCommandListener),
    RPUSHX(rpushxCommand, ::ListPushCommandListener),
    LINDEX(lindexCommand, ::LindexCommandListener),
    LLEN(llenCommand, ::LlenCommandListener),
    LPOP(lpopCommand, ::ListPopCommandListener),
    RPOP(rpopCommand, ::ListPopCommandListener),
    LRANGE(lrangeCommand, ::LrangeCommandListener),

    HSET(hsetCommand, ::HsetCommandListener),
    HSETNX(hsetnxCommand, ::HsetnxCommandListener),
    HMSET(hmsetCommand, ::HmsetCommandListener),
    SADD(saddCommand, ::SaddCommandListener),
    HEXISTS(hexistsCommand, ::HexistsCommandListener),
    SISMEMBER(sismemberCommand, ::HexistsCommandListener),
    HGET(hgetCommand, ::MapGetCommandListener),
    HMGET(hmgetCommand, ::MapGetCommandListener),
    HGETALL(hgetallCommand, ::MapGetCommandListener),
    HVALS(hvalsCommand, ::MapGetCommandListener),
    HKEYS(hkeysCommand, ::MapGetCommandListener),
    ZMSCORE(zmscoreCommand, ::MapGetCommandListener),
    ZRANK(zrankCommand, ::MapGetCommandListener),
    SMEMBERS(smembersCommand, ::MapGetCommandListener),
    HINCRBY(hincrbyCommand, ::HincrbyCommandListener),
    HINCRBYFLOAT(hincrbyfloatCommand, ::HincrbyCommandListener),
    ZINCRBY(zincrbyCommand, ::HincrbyCommandListener),
    HSTRLEN(hstrlenCommand, ::HstrlenCommandListener),
    HLEN(hlenCommand, ::MapSizeCommandListener),
    SCARD(scardCommand, ::MapSizeCommandListener),
    ZCARD(zcardCommand, ::MapSizeCommandListener),
    HDEL(hdelCommand, ::MapDelCommandListener),
    SREM(sremCommand, ::MapDelCommandListener),
    ZREM(zremCommand, ::MapDelCommandListener),
    SUNION(sunionCommand, ::SunionCommandListener),
    SINTER(sinterCommand, ::SinterCommandListener),
    SUNIONSTORE(sunionstoreCommand, ::SunionstoreCommandListener),
    SINTERSTORE(sinterstoreCommand, ::SinterstoreCommandListener),
    ZADD(zaddCommand, ::ZaddCommandListener),
    ZPOPMAX(zpopmaxCommand, ::ZpopmaxCommandListener),
    ZPOPMIN(zpopminCommand, ::ZpopminCommandListener),
    ZRANDMEMBER(zrandmemberCommand, ::ZrandmemberCommandListener),
    ZCOUNT(zcountCommand, ::ZcountCommandListener),
    ZLEXCOUNT(zlexcountCommand, ::ZlexcountCommandListener),
    ZREMRANGEBYSCORE(zremrangebyscoreCommand, ::ZremrangebyscoreCommandListener),
    ZREMRANGEBYRANK(zremrangebyrankCommand, ::ZremrangebyrankCommandListener),
    ZREMRANGEBYLEX(zremrangebylexCommand, ::ZremrangebylexCommandListener),
    ZRANGE(zrangeCommand, ::ZrangeCommandListener),
    ZRANGESTORE(zrangestoreCommand, ::ZrangestoreCommandListener),
    ZREVRANGE(zrevrangeCommand, ::ZrevrangeCommandListener),
    ZRANGEBYSCORE(zrangebyscoreCommand, ::ZrangebyscoreCommandListener),
    ZREVRANGEBYSCORE(zrevrangebyscoreCommand, ::ZrevrangebyscoreCommandListener),
    ZRANGEBYLEX(zrangebylexCommand, ::ZrangebylexCommandListener),
    ZREVRANGEBYLEX(zrevrangebylexCommand, ::ZrevrangebylexCommandListener),

    SCAN(scanCommand, ::ScanCommandListener),
    HSCAN(hscanCommand, ::HscanCommandListener),
    SSCAN(sscanCommand, ::SscanCommandListener),
    ZSCAN(zscanCommand, ::ZscanCommandListener),

    FLUSHDB(flushdbCommand, ::FlushCommandHandler),
    FLUSHALL(flushallCommand, ::FlushCommandHandler),
    DBSIZE(dbsizeCommand, ::DbsizeCommandHandler),

    PING(pingCommand, ::PingCommandHandler),
    ECHO(echoCommand, ::EchoCommandHandler),
    LOLWUT(lolwutCommand, ::LolwutCommandHandler),
    TIME(timeCommand, ::TimeCommandHandler),
    QUIT(null, ::MockCommandHandler),
    RESET(resetCommand, ::MockCommandHandler),
    SAVE(saveCommand, ::MockCommandHandler),
    BGSAVE(bgsaveCommand, ::MockCommandHandler),
    AUTH(authCommand, ::AuthCommandHandler),

    MULTI(multiCommand, ::MultiCommandHandler),
    DISCARD(discardCommand, ::DiscardCommandHandler),
    EXEC(execCommand, ::ExecCommandHandler),

    COMMAND(commandCommand, ::CommandCommandHandler);

    companion object {
        private val log = KotlinLogging.logger {}

        fun getValue(stringValue: String): RedisCommand {
            return try {
                valueOf(stringValue.toUpperCase(Locale.ENGLISH))
            } catch (e: IllegalArgumentException) {
                val msg = "ERR $stringValue unsupported command"
                log.warn { msg }
                throw UnsupportedOperationException(msg)
            }
        }

        val totalCommands: Long by lazy {
            enumValues<RedisCommand>().filter { it.details != null }.size.toLong()
        }

        fun writeCommand(ctx: ChannelHandlerContext) {
            ctx.write(ArrayHeaderRedisMessage(totalCommands))
            enumValues<RedisCommand>().filter { it.details != null }
                .forEach { it.details!!.write(ctx) }
        }

        fun writeCommandInfo(ctx: ChannelHandlerContext, commands: List<String>) {
            val list = enumValues<RedisCommand>().filter { it.details != null }
                .filter { commands.contains(it.details!!.commandName) }
            ctx.write(ArrayHeaderRedisMessage(list.size.toLong()))
            list.forEach { it.details!!.write(ctx) }
        }
    }
}
