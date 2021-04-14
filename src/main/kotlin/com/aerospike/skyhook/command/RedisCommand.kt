package com.aerospike.skyhook.command

import com.aerospike.skyhook.util.RedisCommandsDetails.appendCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.bgsaveCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.commandCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.dbsizeCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.delCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.echoCommand
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
import com.aerospike.skyhook.util.RedisCommandsDetails.hsetCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.hsetnxCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.hstrlenCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.hvalsCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.incrCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.incrbyCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.incrbyfloatCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.lindexCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.llenCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.lpopCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.lpushCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.lpushxCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.lrangeCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.mgetCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.msetCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.msetnxCommand
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
import com.aerospike.skyhook.util.RedisCommandsDetails.scardCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.setCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.setexCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.setnxCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.sinterCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.sinterstoreCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.sismemberCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.smembersCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.sremCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.strlenCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.sunionCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.sunionstoreCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.timeCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.touchCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.ttlCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.unlinkCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.zcardCommand
import com.aerospike.skyhook.util.RedisCommandsDetails.zremCommand
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.redis.ArrayHeaderRedisMessage
import mu.KotlinLogging
import java.util.*

enum class RedisCommand(private val details: RedisCommandDetails?) {
    GET(getCommand),
    MGET(mgetCommand),
    GETSET(getsetCommand),
    SET(setCommand),
    SETEX(setexCommand),
    PSETEX(psetexCommand),
    SETNX(setnxCommand),
    MSET(msetCommand),
    MSETNX(msetnxCommand),
    EXISTS(existsCommand),
    EXPIRE(expireCommand),
    PEXPIRE(pexpireCommand),
    EXPIREAT(expireatCommand),
    PEXPIREAT(pexpireatCommand),
    PERSIST(persistCommand),
    APPEND(appendCommand),
    INCR(incrCommand),
    INCRBY(incrbyCommand),
    INCRBYFLOAT(incrbyfloatCommand),
    STRLEN(strlenCommand),
    TTL(ttlCommand),
    PTTL(pttlCommand),
    DEL(delCommand),
    UNLINK(unlinkCommand),
    RANDOMKEY(randomkeyCommand),

    /**
     * The Redis TOUCH command changes the last_access_time of the key (used for LRU eviction).
     * Aerospike (currently) doesn't have such metadata on the record.
     * The actual implementation returns the number of records that were 'touched' using
     * [com.aerospike.client.AerospikeClient.exists].
     */
    TOUCH(touchCommand),

    LPUSH(lpushCommand),
    LPUSHX(lpushxCommand),
    RPUSH(rpushCommand),
    RPUSHX(rpushxCommand),
    LINDEX(lindexCommand),
    LLEN(llenCommand),
    LPOP(lpopCommand),
    RPOP(rpopCommand),
    LRANGE(lrangeCommand),

    HSET(hsetCommand),
    HSETNX(hsetnxCommand),
    HMSET(hmsetCommand),
    SADD(saddCommand),
    HEXISTS(hexistsCommand),
    SISMEMBER(sismemberCommand),
    HGET(hgetCommand),
    HMGET(hmgetCommand),
    HGETALL(hgetallCommand),
    HVALS(hvalsCommand),
    HKEYS(hkeysCommand),
    SMEMBERS(smembersCommand),
    HINCRBY(hincrbyCommand),
    HINCRBYFLOAT(hincrbyfloatCommand),
    HSTRLEN(hstrlenCommand),
    HLEN(hlenCommand),
    SCARD(scardCommand),
    ZCARD(zcardCommand),
    HDEL(hdelCommand),
    SREM(sremCommand),
    ZREM(zremCommand),
    SUNION(sunionCommand),
    SINTER(sinterCommand),
    SUNIONSTORE(sunionstoreCommand),
    SINTERSTORE(sinterstoreCommand),

    FLUSHDB(flushdbCommand),
    FLUSHALL(flushallCommand),
    DBSIZE(dbsizeCommand),

    PING(pingCommand),
    ECHO(echoCommand),
    TIME(timeCommand),
    QUIT(null),
    RESET(resetCommand),
    SAVE(saveCommand),
    BGSAVE(bgsaveCommand),

    COMMAND(commandCommand),

    UNKNOWN(null);

    companion object {
        private val log = KotlinLogging.logger {}

        fun getValue(stringValue: String): RedisCommand {
            return try {
                valueOf(stringValue.toUpperCase(Locale.ENGLISH))
            } catch (ignore: IllegalArgumentException) {
                log.warn { "$stringValue unsupported command" }
                UNKNOWN
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
