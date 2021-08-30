package com.aerospike.skyhook.command

import com.aerospike.skyhook.command.CommandsDetails.appendCommandDetails
import com.aerospike.skyhook.command.CommandsDetails.authCommandDetails
import com.aerospike.skyhook.command.CommandsDetails.bgsaveCommandDetails
import com.aerospike.skyhook.command.CommandsDetails.commandCommandDetails
import com.aerospike.skyhook.command.CommandsDetails.dbsizeCommandDetails
import com.aerospike.skyhook.command.CommandsDetails.decrCommandDetails
import com.aerospike.skyhook.command.CommandsDetails.decrbyCommandDetails
import com.aerospike.skyhook.command.CommandsDetails.delCommandDetails
import com.aerospike.skyhook.command.CommandsDetails.discardCommandDetails
import com.aerospike.skyhook.command.CommandsDetails.echoCommandDetails
import com.aerospike.skyhook.command.CommandsDetails.execCommandDetails
import com.aerospike.skyhook.command.CommandsDetails.existsCommandDetails
import com.aerospike.skyhook.command.CommandsDetails.expireCommandDetails
import com.aerospike.skyhook.command.CommandsDetails.expireatCommandDetails
import com.aerospike.skyhook.command.CommandsDetails.flushallCommandDetails
import com.aerospike.skyhook.command.CommandsDetails.flushdbCommandDetails
import com.aerospike.skyhook.command.CommandsDetails.getCommandDetails
import com.aerospike.skyhook.command.CommandsDetails.getdelCommandDetails
import com.aerospike.skyhook.command.CommandsDetails.getsetCommandDetails
import com.aerospike.skyhook.command.CommandsDetails.hdelCommandDetails
import com.aerospike.skyhook.command.CommandsDetails.hexistsCommandDetails
import com.aerospike.skyhook.command.CommandsDetails.hgetCommandDetails
import com.aerospike.skyhook.command.CommandsDetails.hgetallCommandDetails
import com.aerospike.skyhook.command.CommandsDetails.hincrbyCommandDetails
import com.aerospike.skyhook.command.CommandsDetails.hincrbyfloatCommandDetails
import com.aerospike.skyhook.command.CommandsDetails.hkeysCommandDetails
import com.aerospike.skyhook.command.CommandsDetails.hlenCommandDetails
import com.aerospike.skyhook.command.CommandsDetails.hmgetCommandDetails
import com.aerospike.skyhook.command.CommandsDetails.hmsetCommandDetails
import com.aerospike.skyhook.command.CommandsDetails.hrandfieldCommandDetails
import com.aerospike.skyhook.command.CommandsDetails.hscanCommandDetails
import com.aerospike.skyhook.command.CommandsDetails.hsetCommandDetails
import com.aerospike.skyhook.command.CommandsDetails.hsetnxCommandDetails
import com.aerospike.skyhook.command.CommandsDetails.hstrlenCommandDetails
import com.aerospike.skyhook.command.CommandsDetails.hvalsCommandDetails
import com.aerospike.skyhook.command.CommandsDetails.incrCommandDetails
import com.aerospike.skyhook.command.CommandsDetails.incrbyCommandDetails
import com.aerospike.skyhook.command.CommandsDetails.incrbyfloatCommandDetails
import com.aerospike.skyhook.command.CommandsDetails.keysCommandDetails
import com.aerospike.skyhook.command.CommandsDetails.lindexCommandDetails
import com.aerospike.skyhook.command.CommandsDetails.llenCommandDetails
import com.aerospike.skyhook.command.CommandsDetails.lolwutCommandDetails
import com.aerospike.skyhook.command.CommandsDetails.lpopCommandDetails
import com.aerospike.skyhook.command.CommandsDetails.lpushCommandDetails
import com.aerospike.skyhook.command.CommandsDetails.lpushxCommandDetails
import com.aerospike.skyhook.command.CommandsDetails.lrangeCommandDetails
import com.aerospike.skyhook.command.CommandsDetails.mgetCommandDetails
import com.aerospike.skyhook.command.CommandsDetails.msetCommandDetails
import com.aerospike.skyhook.command.CommandsDetails.msetnxCommandDetails
import com.aerospike.skyhook.command.CommandsDetails.multiCommandDetails
import com.aerospike.skyhook.command.CommandsDetails.persistCommandDetails
import com.aerospike.skyhook.command.CommandsDetails.pexpireCommandDetails
import com.aerospike.skyhook.command.CommandsDetails.pexpireatCommandDetails
import com.aerospike.skyhook.command.CommandsDetails.pingCommandDetails
import com.aerospike.skyhook.command.CommandsDetails.psetexCommandDetails
import com.aerospike.skyhook.command.CommandsDetails.pttlCommandDetails
import com.aerospike.skyhook.command.CommandsDetails.randomkeyCommandDetails
import com.aerospike.skyhook.command.CommandsDetails.resetCommandDetails
import com.aerospike.skyhook.command.CommandsDetails.rpopCommandDetails
import com.aerospike.skyhook.command.CommandsDetails.rpushCommandDetails
import com.aerospike.skyhook.command.CommandsDetails.rpushxCommandDetails
import com.aerospike.skyhook.command.CommandsDetails.saddCommandDetails
import com.aerospike.skyhook.command.CommandsDetails.saveCommandDetails
import com.aerospike.skyhook.command.CommandsDetails.scanCommandDetails
import com.aerospike.skyhook.command.CommandsDetails.scardCommandDetails
import com.aerospike.skyhook.command.CommandsDetails.setCommandDetails
import com.aerospike.skyhook.command.CommandsDetails.setexCommandDetails
import com.aerospike.skyhook.command.CommandsDetails.setnxCommandDetails
import com.aerospike.skyhook.command.CommandsDetails.sinterCommandDetails
import com.aerospike.skyhook.command.CommandsDetails.sinterstoreCommandDetails
import com.aerospike.skyhook.command.CommandsDetails.sismemberCommandDetails
import com.aerospike.skyhook.command.CommandsDetails.smembersCommandDetails
import com.aerospike.skyhook.command.CommandsDetails.smismemberCommandDetails
import com.aerospike.skyhook.command.CommandsDetails.srandmemberCommandDetails
import com.aerospike.skyhook.command.CommandsDetails.sremCommandDetails
import com.aerospike.skyhook.command.CommandsDetails.sscanCommandDetails
import com.aerospike.skyhook.command.CommandsDetails.strlenCommandDetails
import com.aerospike.skyhook.command.CommandsDetails.sunionCommandDetails
import com.aerospike.skyhook.command.CommandsDetails.sunionstoreCommandDetails
import com.aerospike.skyhook.command.CommandsDetails.timeCommandDetails
import com.aerospike.skyhook.command.CommandsDetails.touchCommandDetails
import com.aerospike.skyhook.command.CommandsDetails.ttlCommandDetails
import com.aerospike.skyhook.command.CommandsDetails.typeCommandDetails
import com.aerospike.skyhook.command.CommandsDetails.unlinkCommandDetails
import com.aerospike.skyhook.command.CommandsDetails.zaddCommandDetails
import com.aerospike.skyhook.command.CommandsDetails.zcardCommandDetails
import com.aerospike.skyhook.command.CommandsDetails.zcountCommandDetails
import com.aerospike.skyhook.command.CommandsDetails.zincrbyCommandDetails
import com.aerospike.skyhook.command.CommandsDetails.zlexcountCommandDetails
import com.aerospike.skyhook.command.CommandsDetails.zmscoreCommandDetails
import com.aerospike.skyhook.command.CommandsDetails.zpopmaxCommandDetails
import com.aerospike.skyhook.command.CommandsDetails.zpopminCommandDetails
import com.aerospike.skyhook.command.CommandsDetails.zrandmemberCommandDetails
import com.aerospike.skyhook.command.CommandsDetails.zrangeCommandDetails
import com.aerospike.skyhook.command.CommandsDetails.zrangebylexCommandDetails
import com.aerospike.skyhook.command.CommandsDetails.zrangebyscoreCommandDetails
import com.aerospike.skyhook.command.CommandsDetails.zrangestoreCommandDetails
import com.aerospike.skyhook.command.CommandsDetails.zrankCommandDetails
import com.aerospike.skyhook.command.CommandsDetails.zremCommandDetails
import com.aerospike.skyhook.command.CommandsDetails.zremrangebylexCommandDetails
import com.aerospike.skyhook.command.CommandsDetails.zremrangebyrankCommandDetails
import com.aerospike.skyhook.command.CommandsDetails.zremrangebyscoreCommandDetails
import com.aerospike.skyhook.command.CommandsDetails.zrevrangeCommandDetails
import com.aerospike.skyhook.command.CommandsDetails.zrevrangebylexCommandDetails
import com.aerospike.skyhook.command.CommandsDetails.zrevrangebyscoreCommandDetails
import com.aerospike.skyhook.command.CommandsDetails.zscanCommandDetails
import com.aerospike.skyhook.handler.CommandHandler
import com.aerospike.skyhook.handler.aerospike.*
import com.aerospike.skyhook.handler.redis.*
import com.aerospike.skyhook.listener.key.*
import com.aerospike.skyhook.listener.list.*
import com.aerospike.skyhook.listener.map.*
import com.aerospike.skyhook.listener.scan.*
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.redis.ArrayHeaderRedisMessage
import mu.KotlinLogging
import java.util.*
import kotlin.reflect.KFunction1

enum class RedisCommand(
    val details: RedisCommandDetails?,
    val newHandler: KFunction1<ChannelHandlerContext, CommandHandler>
) {
    GET(getCommandDetails, ::GetCommandListener),
    MGET(mgetCommandDetails, ::MgetCommandListener),
    GETSET(getsetCommandDetails, ::GetsetCommandListener),
    GETDEL(getdelCommandDetails, ::GetdelCommandListener),
    SET(setCommandDetails, ::SetCommandListener),
    SETEX(setexCommandDetails, ::SetCommandListener),
    PSETEX(psetexCommandDetails, ::SetCommandListener),
    SETNX(setnxCommandDetails, ::SetCommandListener),
    MSET(msetCommandDetails, ::MsetCommandListener),
    MSETNX(msetnxCommandDetails, ::MsetCommandListener),
    EXISTS(existsCommandDetails, ::ExistsCommandListener),
    EXPIRE(expireCommandDetails, ::ExpireCommandListener),
    PEXPIRE(pexpireCommandDetails, ::ExpireCommandListener),
    EXPIREAT(expireatCommandDetails, ::ExpireCommandListener),
    PEXPIREAT(pexpireatCommandDetails, ::ExpireCommandListener),
    PERSIST(persistCommandDetails, ::ExpireCommandListener),
    APPEND(appendCommandDetails, ::AppendCommandListener),
    INCR(incrCommandDetails, ::IncrCommandListener),
    INCRBY(incrbyCommandDetails, ::IncrCommandListener),
    INCRBYFLOAT(incrbyfloatCommandDetails, ::IncrCommandListener),
    DECR(decrCommandDetails, ::DecrCommandListener),
    DECRBY(decrbyCommandDetails, ::DecrCommandListener),
    STRLEN(strlenCommandDetails, ::StrlenCommandListener),
    TTL(ttlCommandDetails, ::TtlCommandListener),
    PTTL(pttlCommandDetails, ::TtlCommandListener),
    DEL(delCommandDetails, ::DelCommandListener),
    UNLINK(unlinkCommandDetails, ::DelCommandListener),
    RANDOMKEY(randomkeyCommandDetails, ::RandomkeyCommandListener),

    /**
     * The Redis TOUCH command changes the last_access_time of the key (used for LRU eviction).
     * Aerospike (currently) doesn't have such metadata on the record.
     * The actual implementation returns the number of records that were 'touched' using
     * [com.aerospike.client.AerospikeClient.exists].
     */
    TOUCH(touchCommandDetails, ::ExistsCommandListener),
    TYPE(typeCommandDetails, ::TypeCommandListener),

    LPUSH(lpushCommandDetails, ::ListPushCommandListener),
    LPUSHX(lpushxCommandDetails, ::ListPushCommandListener),
    RPUSH(rpushCommandDetails, ::ListPushCommandListener),
    RPUSHX(rpushxCommandDetails, ::ListPushCommandListener),
    LINDEX(lindexCommandDetails, ::LindexCommandListener),
    LLEN(llenCommandDetails, ::LlenCommandListener),
    LPOP(lpopCommandDetails, ::ListPopCommandListener),
    RPOP(rpopCommandDetails, ::ListPopCommandListener),
    LRANGE(lrangeCommandDetails, ::LrangeCommandListener),

    HSET(hsetCommandDetails, ::HsetCommandListener),
    HSETNX(hsetnxCommandDetails, ::HsetnxCommandListener),
    HMSET(hmsetCommandDetails, ::HmsetCommandListener),
    SADD(saddCommandDetails, ::SaddCommandListener),
    HEXISTS(hexistsCommandDetails, ::HexistsCommandListener),
    SISMEMBER(sismemberCommandDetails, ::HexistsCommandListener),
    SMISMEMBER(smismemberCommandDetails, ::SmismemberCommandListener),
    HGET(hgetCommandDetails, ::MapGetCommandListener),
    HMGET(hmgetCommandDetails, ::MapGetCommandListener),
    HGETALL(hgetallCommandDetails, ::MapGetCommandListener),
    HVALS(hvalsCommandDetails, ::MapGetCommandListener),
    HKEYS(hkeysCommandDetails, ::MapGetCommandListener),
    ZMSCORE(zmscoreCommandDetails, ::MapGetCommandListener),
    ZRANK(zrankCommandDetails, ::MapGetCommandListener),
    SMEMBERS(smembersCommandDetails, ::MapGetCommandListener),
    HINCRBY(hincrbyCommandDetails, ::HincrbyCommandListener),
    HINCRBYFLOAT(hincrbyfloatCommandDetails, ::HincrbyCommandListener),
    ZINCRBY(zincrbyCommandDetails, ::HincrbyCommandListener),
    HSTRLEN(hstrlenCommandDetails, ::HstrlenCommandListener),
    HLEN(hlenCommandDetails, ::MapSizeCommandListener),
    SCARD(scardCommandDetails, ::MapSizeCommandListener),
    ZCARD(zcardCommandDetails, ::MapSizeCommandListener),
    HDEL(hdelCommandDetails, ::MapDelCommandListener),
    SREM(sremCommandDetails, ::MapDelCommandListener),
    ZREM(zremCommandDetails, ::MapDelCommandListener),
    SUNION(sunionCommandDetails, ::SunionCommandListener),
    SINTER(sinterCommandDetails, ::SinterCommandListener),
    SUNIONSTORE(sunionstoreCommandDetails, ::SunionstoreCommandListener),
    SINTERSTORE(sinterstoreCommandDetails, ::SinterstoreCommandListener),
    ZADD(zaddCommandDetails, ::ZaddCommandListener),
    ZPOPMAX(zpopmaxCommandDetails, ::ZpopmaxCommandListener),
    ZPOPMIN(zpopminCommandDetails, ::ZpopminCommandListener),
    ZRANDMEMBER(zrandmemberCommandDetails, ::RandmemberCommandListener),
    SRANDMEMBER(srandmemberCommandDetails, ::RandmemberCommandListener),
    HRANDFIELD(hrandfieldCommandDetails, ::RandmemberCommandListener),
    ZCOUNT(zcountCommandDetails, ::ZcountCommandListener),
    ZLEXCOUNT(zlexcountCommandDetails, ::ZlexcountCommandListener),
    ZREMRANGEBYSCORE(zremrangebyscoreCommandDetails, ::ZremrangebyscoreCommandListener),
    ZREMRANGEBYRANK(zremrangebyrankCommandDetails, ::ZremrangebyrankCommandListener),
    ZREMRANGEBYLEX(zremrangebylexCommandDetails, ::ZremrangebylexCommandListener),
    ZRANGE(zrangeCommandDetails, ::ZrangeCommandListener),
    ZRANGESTORE(zrangestoreCommandDetails, ::ZrangestoreCommandListener),
    ZREVRANGE(zrevrangeCommandDetails, ::ZrevrangeCommandListener),
    ZRANGEBYSCORE(zrangebyscoreCommandDetails, ::ZrangebyscoreCommandListener),
    ZREVRANGEBYSCORE(zrevrangebyscoreCommandDetails, ::ZrevrangebyscoreCommandListener),
    ZRANGEBYLEX(zrangebylexCommandDetails, ::ZrangebylexCommandListener),
    ZREVRANGEBYLEX(zrevrangebylexCommandDetails, ::ZrevrangebylexCommandListener),

    SCAN(scanCommandDetails, ::ScanCommandListener),
    HSCAN(hscanCommandDetails, ::HscanCommandListener),
    SSCAN(sscanCommandDetails, ::SscanCommandListener),
    ZSCAN(zscanCommandDetails, ::ZscanCommandListener),
    KEYS(keysCommandDetails, ::KeysCommandListener),

    FLUSHDB(flushdbCommandDetails, ::FlushCommandHandler),
    FLUSHALL(flushallCommandDetails, ::FlushCommandHandler),
    DBSIZE(dbsizeCommandDetails, ::DbsizeCommandHandler),

    PING(pingCommandDetails, ::PingCommandHandler),
    ECHO(echoCommandDetails, ::EchoCommandHandler),
    LOLWUT(lolwutCommandDetails, ::LolwutCommandHandler),
    TIME(timeCommandDetails, ::TimeCommandHandler),
    QUIT(null, ::MockCommandHandler),
    RESET(resetCommandDetails, ::MockCommandHandler),
    SAVE(saveCommandDetails, ::MockCommandHandler),
    BGSAVE(bgsaveCommandDetails, ::MockCommandHandler),
    AUTH(authCommandDetails, ::AuthCommandHandler),

    MULTI(multiCommandDetails, ::MultiCommandHandler),
    DISCARD(discardCommandDetails, ::DiscardCommandHandler),
    EXEC(execCommandDetails, ::ExecCommandHandler),

    COMMAND(commandCommandDetails, ::CommandCommandHandler);

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

object CommandsDetails {

    val getCommandDetails = RedisCommandDetails("get", 2, arrayListOf("readonly", "fast"), 1, 1, 1)
    val mgetCommandDetails = RedisCommandDetails("mget", -2, arrayListOf("readonly", "fast"), 1, -1, 1)
    val getsetCommandDetails = RedisCommandDetails("getset", 3, arrayListOf("write", "denyoom", "fast"), 1, 1, 1)
    val getdelCommandDetails = RedisCommandDetails("getdel", 2, arrayListOf("write", "fast"), 1, 1, 1)
    val setCommandDetails = RedisCommandDetails("set", -3, arrayListOf("write", "denyoom"), 1, 1, 1)
    val setexCommandDetails = RedisCommandDetails("setex", 4, arrayListOf("write", "denyoom"), 1, 1, 1)
    val psetexCommandDetails = RedisCommandDetails("psetex", 4, arrayListOf("write", "denyoom"), 1, 1, 1)
    val setnxCommandDetails = RedisCommandDetails("setnx", 3, arrayListOf("write", "denyoom", "fast"), 1, 1, 1)
    val msetCommandDetails = RedisCommandDetails("mset", -3, arrayListOf("write", "denyoom"), 1, -1, 2)
    val msetnxCommandDetails = RedisCommandDetails("msetnx", -3, arrayListOf("write", "denyoom"), 1, -1, 2)
    val existsCommandDetails = RedisCommandDetails("exists", -2, arrayListOf("readonly", "fast"), 1, -1, 1)
    val expireCommandDetails = RedisCommandDetails("expire", 3, arrayListOf("write", "fast"), 1, 1, 1)
    val pexpireCommandDetails = RedisCommandDetails("pexpire", 3, arrayListOf("write", "fast"), 1, 1, 1)
    val expireatCommandDetails = RedisCommandDetails("expireat", 3, arrayListOf("write", "fast"), 1, 1, 1)
    val pexpireatCommandDetails = RedisCommandDetails("pexpireat", 3, arrayListOf("write", "fast"), 1, 1, 1)
    val persistCommandDetails = RedisCommandDetails("persist", 2, arrayListOf("write", "fast"), 1, 1, 1)
    val appendCommandDetails = RedisCommandDetails("append", 3, arrayListOf("write", "denyoom", "fast"), 1, 1, 1)
    val incrCommandDetails = RedisCommandDetails("incr", 2, arrayListOf("write", "denyoom", "fast"), 1, 1, 1)
    val incrbyCommandDetails = RedisCommandDetails("incrby", 3, arrayListOf("write", "denyoom", "fast"), 1, 1, 1)
    val incrbyfloatCommandDetails =
        RedisCommandDetails("incrbyfloat", 3, arrayListOf("write", "denyoom", "fast"), 1, 1, 1)
    val decrCommandDetails = RedisCommandDetails("decr", 2, arrayListOf("write", "denyoom", "fast"), 1, 1, 1)
    val decrbyCommandDetails = RedisCommandDetails("decrby", 3, arrayListOf("write", "denyoom", "fast"), 1, 1, 1)
    val strlenCommandDetails = RedisCommandDetails("strlen", 2, arrayListOf("readonly", "fast"), 1, 1, 1)
    val ttlCommandDetails = RedisCommandDetails("ttl", 2, arrayListOf("readonly", "random", "fast"), 1, 1, 1)
    val pttlCommandDetails = RedisCommandDetails("pttl", 2, arrayListOf("readonly", "random", "fast"), 1, 1, 1)
    val delCommandDetails = RedisCommandDetails("del", -2, arrayListOf("write"), 1, -1, 1)
    val unlinkCommandDetails = RedisCommandDetails("unlink", -2, arrayListOf("write", "fast"), 1, -1, 1)
    val randomkeyCommandDetails = RedisCommandDetails("randomkey", 1, arrayListOf("readonly", "random"), 0, 0, 0)
    val touchCommandDetails = RedisCommandDetails("touch", -2, arrayListOf("readonly", "fast"), 1, -1, 1)
    val typeCommandDetails = RedisCommandDetails("type", 2, arrayListOf("readonly", "fast"), 1, 1, 1)

    val lpushCommandDetails = RedisCommandDetails("lpush", -3, arrayListOf("write", "denyoom", "fast"), 1, 1, 1)
    val lpushxCommandDetails = RedisCommandDetails("lpushx", -3, arrayListOf("write", "denyoom", "fast"), 1, 1, 1)
    val rpushCommandDetails = RedisCommandDetails("rpush", -3, arrayListOf("write", "denyoom", "fast"), 1, 1, 1)
    val rpushxCommandDetails = RedisCommandDetails("rpushx", -3, arrayListOf("write", "denyoom", "fast"), 1, 1, 1)
    val lindexCommandDetails = RedisCommandDetails("lindex", 3, arrayListOf("readonly"), 1, 1, 1)
    val llenCommandDetails = RedisCommandDetails("llen", 2, arrayListOf("readonly", "fast"), 1, 1, 1)
    val lpopCommandDetails = RedisCommandDetails("lpop", -2, arrayListOf("write", "fast"), 1, 1, 1)
    val rpopCommandDetails = RedisCommandDetails("rpop", -2, arrayListOf("write", "fast"), 1, 1, 1)
    val lrangeCommandDetails = RedisCommandDetails("lrange", 4, arrayListOf("readonly"), 1, 1, 1)

    val hsetCommandDetails = RedisCommandDetails("hset", -4, arrayListOf("write", "denyoom", "fast"), 1, 1, 1)
    val hsetnxCommandDetails = RedisCommandDetails("hsetnx", 4, arrayListOf("write", "denyoom", "fast"), 1, 1, 1)
    val hmsetCommandDetails = RedisCommandDetails("hmset", -4, arrayListOf("write", "denyoom", "fast"), 1, 1, 1)
    val saddCommandDetails = RedisCommandDetails("sadd", -3, arrayListOf("write", "denyoom", "fast"), 1, 1, 1)
    val hexistsCommandDetails = RedisCommandDetails("hexists", 3, arrayListOf("readonly", "fast"), 1, 1, 1)
    val sismemberCommandDetails = RedisCommandDetails("sismember", 3, arrayListOf("readonly", "fast"), 1, 1, 1)
    val smismemberCommandDetails = RedisCommandDetails("smismember", -3, arrayListOf("readonly", "fast"), 1, 1, 1)
    val hgetCommandDetails = RedisCommandDetails("hget", 3, arrayListOf("readonly", "fast"), 1, 1, 1)
    val hmgetCommandDetails = RedisCommandDetails("hmget", -3, arrayListOf("readonly", "fast"), 1, 1, 1)
    val hgetallCommandDetails = RedisCommandDetails("hgetall", 2, arrayListOf("readonly", "random"), 1, 1, 1)
    val hvalsCommandDetails = RedisCommandDetails("hvals", 2, arrayListOf("readonly", "sort_for_script"), 1, 1, 1)
    val hkeysCommandDetails = RedisCommandDetails("hkeys", 2, arrayListOf("readonly", "sort_for_script"), 1, 1, 1)
    val smembersCommandDetails = RedisCommandDetails("smembers", 2, arrayListOf("readonly", "sort_for_script"), 1, 1, 1)
    val hincrbyCommandDetails = RedisCommandDetails("hincrby", 4, arrayListOf("write", "denyoom", "fast"), 1, 1, 1)
    val hincrbyfloatCommandDetails =
        RedisCommandDetails("hincrbyfloat", 4, arrayListOf("write", "denyoom", "fast"), 1, 1, 1)
    val hstrlenCommandDetails = RedisCommandDetails("hstrlen", 3, arrayListOf("readonly", "fast"), 1, 1, 1)
    val hlenCommandDetails = RedisCommandDetails("hlen", 2, arrayListOf("readonly", "fast"), 1, 1, 1)
    val scardCommandDetails = RedisCommandDetails("scard", 2, arrayListOf("readonly", "fast"), 1, 1, 1)
    val zcardCommandDetails = RedisCommandDetails("zcard", 2, arrayListOf("readonly", "fast"), 1, 1, 1)
    val hdelCommandDetails = RedisCommandDetails("hdel", -3, arrayListOf("write", "fast"), 1, 1, 1)
    val sremCommandDetails = RedisCommandDetails("srem", -3, arrayListOf("write", "fast"), 1, 1, 1)
    val zremCommandDetails = RedisCommandDetails("zrem", -3, arrayListOf("write", "fast"), 1, 1, 1)
    val sunionCommandDetails = RedisCommandDetails("sunion", -2, arrayListOf("readonly", "sort_for_script"), 1, -1, 1)
    val sinterCommandDetails = RedisCommandDetails("sinter", -2, arrayListOf("readonly", "sort_for_script"), 1, -1, 1)
    val sunionstoreCommandDetails = RedisCommandDetails("sunionstore", -3, arrayListOf("write", "denyoom"), 1, -1, 1)
    val sinterstoreCommandDetails = RedisCommandDetails("sinterstore", -3, arrayListOf("write", "denyoom"), 1, -1, 1)
    val zmscoreCommandDetails = RedisCommandDetails("zmscore", -3, arrayListOf("readonly", "fast"), 1, 1, 1)
    val zrankCommandDetails = RedisCommandDetails("zrank", 3, arrayListOf("readonly", "fast"), 1, 1, 1)
    val zincrbyCommandDetails = RedisCommandDetails("zincrby", 4, arrayListOf("write", "denyoom", "fast"), 1, 1, 1)
    val zaddCommandDetails = RedisCommandDetails("zadd", -4, arrayListOf("write", "denyoom", "fast"), 1, 1, 1)
    val zpopmaxCommandDetails = RedisCommandDetails("zpopmax", -2, arrayListOf("write", "fast"), 1, 1, 1)
    val zpopminCommandDetails = RedisCommandDetails("zpopmin", -2, arrayListOf("write", "fast"), 1, 1, 1)
    val zrandmemberCommandDetails = RedisCommandDetails("zrandmember", -2, arrayListOf("readonly", "random"), 1, 1, 1)
    val srandmemberCommandDetails = RedisCommandDetails("srandmember", -2, arrayListOf("readonly", "random"), 1, 1, 1)
    val hrandfieldCommandDetails = RedisCommandDetails("hrandfield", -2, arrayListOf("readonly", "random"), 1, 1, 1)
    val zcountCommandDetails = RedisCommandDetails("zcount", 4, arrayListOf("readonly", "fast"), 1, 1, 1)
    val zlexcountCommandDetails = RedisCommandDetails("zlexcount", 4, arrayListOf("readonly", "fast"), 1, 1, 1)
    val zremrangebyscoreCommandDetails = RedisCommandDetails("zremrangebyscore", 4, arrayListOf("write"), 1, 1, 1)
    val zremrangebyrankCommandDetails = RedisCommandDetails("zremrangebyrank", 4, arrayListOf("write"), 1, 1, 1)
    val zremrangebylexCommandDetails = RedisCommandDetails("zremrangebylex", 4, arrayListOf("write"), 1, 1, 1)
    val zrangeCommandDetails = RedisCommandDetails("zrange", -4, arrayListOf("readonly"), 1, 1, 1)
    val zrangestoreCommandDetails = RedisCommandDetails("zrangestore", -5, arrayListOf("write", "denyoom"), 1, 2, 1)
    val zrevrangeCommandDetails = RedisCommandDetails("zrevrange", -4, arrayListOf("readonly"), 1, 1, 1)
    val zrangebyscoreCommandDetails = RedisCommandDetails("zrangebyscore", -4, arrayListOf("readonly"), 1, 1, 1)
    val zrevrangebyscoreCommandDetails = RedisCommandDetails("zrevrangebyscore", -4, arrayListOf("readonly"), 1, 1, 1)
    val zrangebylexCommandDetails = RedisCommandDetails("zrangebylex", -4, arrayListOf("readonly"), 1, 1, 1)
    val zrevrangebylexCommandDetails = RedisCommandDetails("zrevrangebylex", -4, arrayListOf("readonly"), 1, 1, 1)

    val scanCommandDetails = RedisCommandDetails("scan", -2, arrayListOf("readonly", "random"), 0, 0, 0)
    val hscanCommandDetails = RedisCommandDetails("hscan", -3, arrayListOf("readonly", "random"), 1, 1, 1)
    val sscanCommandDetails = RedisCommandDetails("sscan", -3, arrayListOf("readonly", "random"), 1, 1, 1)
    val zscanCommandDetails = RedisCommandDetails("zscan", -3, arrayListOf("readonly", "random"), 1, 1, 1)
    val keysCommandDetails = RedisCommandDetails("keys", 2, arrayListOf("readonly", "sort_for_script"), 0, 0, 0)

    val flushdbCommandDetails = RedisCommandDetails("flushdb", -1, arrayListOf("write"), 0, 0, 0)
    val flushallCommandDetails = RedisCommandDetails("flushall", -1, arrayListOf("write"), 0, 0, 0)
    val dbsizeCommandDetails = RedisCommandDetails("dbsize", 1, arrayListOf("readonly", "fast"), 0, 0, 0)

    val pingCommandDetails = RedisCommandDetails("ping", -1, arrayListOf("stale", "fast"), 0, 0, 0)
    val echoCommandDetails = RedisCommandDetails("echo", 2, arrayListOf("fast"), 0, 0, 0)
    val lolwutCommandDetails = RedisCommandDetails("lolwut", -1, arrayListOf("readonly", "fast"), 0, 0, 0)
    val timeCommandDetails = RedisCommandDetails("time", 1, arrayListOf("random", "loading", "stale", "fast"), 0, 0, 0)
    val resetCommandDetails =
        RedisCommandDetails("reset", 1, arrayListOf("noscript", "loading", "stale", "fast"), 0, 0, 0)
    val saveCommandDetails = RedisCommandDetails("save", 1, arrayListOf("admin", "noscript"), 0, 0, 0)
    val bgsaveCommandDetails = RedisCommandDetails("bgsave", -1, arrayListOf("admin", "noscript"), 0, 0, 0)
    val commandCommandDetails = RedisCommandDetails("command", -1, arrayListOf("random", "loading", "stale"), 0, 0, 0)

    val authCommandDetails = RedisCommandDetails(
        "auth",
        -2,
        arrayListOf("noscript", "loading", "stale", "skip_monitor", "skip_slowlog", "fast", "no_auth"),
        0,
        0,
        0
    )

    val multiCommandDetails = RedisCommandDetails(
        "multi",
        1,
        arrayListOf("noscript", "loading", "stale", "fast"),
        0,
        0,
        0
    )

    val discardCommandDetails = RedisCommandDetails(
        "discard",
        1,
        arrayListOf("noscript", "loading", "stale", "fast"),
        0,
        0,
        0
    )

    val execCommandDetails = RedisCommandDetails(
        "exec",
        1,
        arrayListOf("noscript", "loading", "stale", "skip_monitor", "skip_slowlog"),
        0,
        0,
        0
    )
}
