package com.aerospike.redispike.command

import com.aerospike.redispike.util.RedisCommandsDetails.appendCommand
import com.aerospike.redispike.util.RedisCommandsDetails.commandCommand
import com.aerospike.redispike.util.RedisCommandsDetails.delCommand
import com.aerospike.redispike.util.RedisCommandsDetails.echoCommand
import com.aerospike.redispike.util.RedisCommandsDetails.existsCommand
import com.aerospike.redispike.util.RedisCommandsDetails.expireCommand
import com.aerospike.redispike.util.RedisCommandsDetails.flushallCommand
import com.aerospike.redispike.util.RedisCommandsDetails.flushdbCommand
import com.aerospike.redispike.util.RedisCommandsDetails.getCommand
import com.aerospike.redispike.util.RedisCommandsDetails.getsetCommand
import com.aerospike.redispike.util.RedisCommandsDetails.hdelCommand
import com.aerospike.redispike.util.RedisCommandsDetails.hexistsCommand
import com.aerospike.redispike.util.RedisCommandsDetails.hgetCommand
import com.aerospike.redispike.util.RedisCommandsDetails.hgetallCommand
import com.aerospike.redispike.util.RedisCommandsDetails.hincrbyCommand
import com.aerospike.redispike.util.RedisCommandsDetails.hincrbyfloatCommand
import com.aerospike.redispike.util.RedisCommandsDetails.hkeysCommand
import com.aerospike.redispike.util.RedisCommandsDetails.hlenCommand
import com.aerospike.redispike.util.RedisCommandsDetails.hmgetCommand
import com.aerospike.redispike.util.RedisCommandsDetails.hmsetCommand
import com.aerospike.redispike.util.RedisCommandsDetails.hsetCommand
import com.aerospike.redispike.util.RedisCommandsDetails.hsetnxCommand
import com.aerospike.redispike.util.RedisCommandsDetails.hstrlenCommand
import com.aerospike.redispike.util.RedisCommandsDetails.hvalsCommand
import com.aerospike.redispike.util.RedisCommandsDetails.incrCommand
import com.aerospike.redispike.util.RedisCommandsDetails.incrbyCommand
import com.aerospike.redispike.util.RedisCommandsDetails.incrbyfloatCommand
import com.aerospike.redispike.util.RedisCommandsDetails.lindexCommand
import com.aerospike.redispike.util.RedisCommandsDetails.llenCommand
import com.aerospike.redispike.util.RedisCommandsDetails.lpopCommand
import com.aerospike.redispike.util.RedisCommandsDetails.lpushCommand
import com.aerospike.redispike.util.RedisCommandsDetails.lpushxCommand
import com.aerospike.redispike.util.RedisCommandsDetails.lrangeCommand
import com.aerospike.redispike.util.RedisCommandsDetails.mgetCommand
import com.aerospike.redispike.util.RedisCommandsDetails.msetCommand
import com.aerospike.redispike.util.RedisCommandsDetails.msetnxCommand
import com.aerospike.redispike.util.RedisCommandsDetails.pexpireCommand
import com.aerospike.redispike.util.RedisCommandsDetails.pingCommand
import com.aerospike.redispike.util.RedisCommandsDetails.psetexCommand
import com.aerospike.redispike.util.RedisCommandsDetails.pttlCommand
import com.aerospike.redispike.util.RedisCommandsDetails.randomkeyCommand
import com.aerospike.redispike.util.RedisCommandsDetails.rpopCommand
import com.aerospike.redispike.util.RedisCommandsDetails.rpushCommand
import com.aerospike.redispike.util.RedisCommandsDetails.rpushxCommand
import com.aerospike.redispike.util.RedisCommandsDetails.saddCommand
import com.aerospike.redispike.util.RedisCommandsDetails.scardCommand
import com.aerospike.redispike.util.RedisCommandsDetails.setCommand
import com.aerospike.redispike.util.RedisCommandsDetails.setexCommand
import com.aerospike.redispike.util.RedisCommandsDetails.setnxCommand
import com.aerospike.redispike.util.RedisCommandsDetails.sismemberCommand
import com.aerospike.redispike.util.RedisCommandsDetails.smembersCommand
import com.aerospike.redispike.util.RedisCommandsDetails.sremCommand
import com.aerospike.redispike.util.RedisCommandsDetails.strlenCommand
import com.aerospike.redispike.util.RedisCommandsDetails.timeCommand
import com.aerospike.redispike.util.RedisCommandsDetails.ttlCommand
import com.aerospike.redispike.util.RedisCommandsDetails.zcardCommand
import com.aerospike.redispike.util.RedisCommandsDetails.zremCommand
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
    APPEND(appendCommand),
    INCR(incrCommand),
    INCRBY(incrbyCommand),
    INCRBYFLOAT(incrbyfloatCommand),
    STRLEN(strlenCommand),
    TTL(ttlCommand),
    PTTL(pttlCommand),
    DEL(delCommand),
    RANDOMKEY(randomkeyCommand),

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

    FLUSHDB(flushdbCommand),
    FLUSHALL(flushallCommand),

    PING(pingCommand),
    ECHO(echoCommand),
    TIME(timeCommand),

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
