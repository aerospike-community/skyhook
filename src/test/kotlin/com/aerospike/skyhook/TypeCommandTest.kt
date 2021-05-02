package com.aerospike.skyhook

import com.aerospike.skyhook.command.RedisCommand
import com.aerospike.skyhook.listener.ValueType
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TypeCommandTest() : SkyhookIntegrationTestBase() {

    @Test
    fun testStringType() {
        writeCommand("${RedisCommand.SET.name} set abc")
        assertEquals(ok, readString())
        writeCommand("${RedisCommand.TYPE.name} set")
        assertEquals(ValueType.STRING.str, readFullBulkString())

        writeCommand("${RedisCommand.APPEND.name} append abc")
        assertEquals(3L, readLong())
        writeCommand("${RedisCommand.TYPE.name} append")
        assertEquals(ValueType.STRING.str, readFullBulkString())

        writeCommand("${RedisCommand.SETEX.name} setex 10 abc")
        assertEquals(ok, readString())
        writeCommand("${RedisCommand.TYPE.name} setex")
        assertEquals(ValueType.STRING.str, readFullBulkString())

        writeCommand("${RedisCommand.PSETEX.name} psetex 10 abc")
        assertEquals(ok, readString())
        writeCommand("${RedisCommand.TYPE.name} psetex")
        assertEquals(ValueType.STRING.str, readFullBulkString())

        writeCommand("${RedisCommand.SETNX.name} setnx abc")
        assertEquals(1L, readLong())
        writeCommand("${RedisCommand.TYPE.name} setnx")
        assertEquals(ValueType.STRING.str, readFullBulkString())

        writeCommand("${RedisCommand.MSET.name} mset abc")
        assertEquals(ok, readString())
        writeCommand("${RedisCommand.TYPE.name} mset")
        assertEquals(ValueType.STRING.str, readFullBulkString())

        writeCommand("${RedisCommand.MSETNX.name} msetnx abc")
        assertEquals(1L, readLong())
        writeCommand("${RedisCommand.TYPE.name} msetnx")
        assertEquals(ValueType.STRING.str, readFullBulkString())

        writeCommand("${RedisCommand.INCR.name} incr")
        assertEquals(1L, readLong())
        writeCommand("${RedisCommand.TYPE.name} incr")
        assertEquals(ValueType.STRING.str, readFullBulkString())

        writeCommand("${RedisCommand.INCRBY.name} incrby 3")
        assertEquals(3L, readLong())
        writeCommand("${RedisCommand.TYPE.name} incrby")
        assertEquals(ValueType.STRING.str, readFullBulkString())

        writeCommand("${RedisCommand.INCRBYFLOAT.name} incrbyfloat 0.5")
        readFullBulkString()
        writeCommand("${RedisCommand.TYPE.name} incrbyfloat")
        assertEquals(ValueType.STRING.str, readFullBulkString())
    }

    @Test
    fun testListType() {
        writeCommand("${RedisCommand.RPUSH.name} list val1")
        assertEquals(1L, readLong())
        writeCommand("${RedisCommand.TYPE.name} list")
        assertEquals(ValueType.LIST.str, readFullBulkString())

        writeCommand("${RedisCommand.LPUSH.name} list2 val1")
        assertEquals(1L, readLong())
        writeCommand("${RedisCommand.TYPE.name} list2")
        assertEquals(ValueType.LIST.str, readFullBulkString())
    }

    @Test
    fun testHashType() {
        writeCommand("${RedisCommand.HSET.name} hash key1 val1")
        assertEquals(1L, readLong())
        writeCommand("${RedisCommand.TYPE.name} hash")
        assertEquals(ValueType.HASH.str, readFullBulkString())

        writeCommand("${RedisCommand.HSETNX.name} hash2 key1 val1")
        assertEquals(1L, readLong())
        writeCommand("${RedisCommand.TYPE.name} hash2")
        assertEquals(ValueType.HASH.str, readFullBulkString())

        writeCommand("${RedisCommand.HMSET.name} hash3 key1 val1")
        assertEquals(ok, readString())
        writeCommand("${RedisCommand.TYPE.name} hash3")
        assertEquals(ValueType.HASH.str, readFullBulkString())
    }

    @Test
    fun testSetType() {
        writeCommand("${RedisCommand.SADD.name} set val1")
        assertEquals(1L, readLong())
        writeCommand("${RedisCommand.TYPE.name} set")
        assertEquals(ValueType.SET.str, readFullBulkString())
    }

    @Test
    fun testZsetType() {
        writeCommand("${RedisCommand.ZADD.name} zset 1 val1")
        assertEquals(1L, readLong())
        writeCommand("${RedisCommand.TYPE.name} zset")
        assertEquals(ValueType.ZSET.str, readFullBulkString())
    }
}
