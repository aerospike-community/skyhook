package com.aerospike.redispike.command

import java.util.*

enum class RedisCommand {
    GET,
    MGET,
    GETSET,
    SET,
    SETEX,
    PSETEX,
    SETNX,
    EXISTS,
    EXPIRE,
    PEXPIRE,
    APPEND,
    INCR,
    INCRBY,
    INCRBYFLOAT,
    STRLEN,
    TTL,
    PTTL,
    DEL,

    LPUSH,
    LPUSHX,
    RPUSH,
    RPUSHX,
    LINDEX,
    LLEN,
    LPOP,
    RPOP,
    LRANGE,

    UNKNOWN;

    companion object {
        fun getValue(stringValue: String): RedisCommand {
            return try {
                valueOf(stringValue.toUpperCase(Locale.ENGLISH))
            } catch (ignore: IllegalArgumentException) {
                UNKNOWN
            }
        }
    }
}
