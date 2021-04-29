package com.aerospike.skyhook

import com.aerospike.skyhook.command.RedisCommand
import com.aerospike.skyhook.listener.ValueType
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TypeCommandTest() : SkyhookIntegrationTestBase() {

    @Test
    fun testStringType() {
        writeCommand("${RedisCommand.SET.name} key1 val1")
        assertEquals(ok, readString())
        writeCommand("${RedisCommand.TYPE.name} key1")
        assertEquals(ValueType.STRING.str, readFullBulkString())
    }

    @Test
    fun testListType() {
        writeCommand("${RedisCommand.RPUSH.name} list val1")
        assertEquals(1L, readLong())
        writeCommand("${RedisCommand.TYPE.name} list")
        assertEquals(ValueType.LIST.str, readFullBulkString())
    }

    @Test
    fun testHashType() {
        writeCommand("${RedisCommand.HSET.name} hash key1 val1")
        assertEquals(1L, readLong())
        writeCommand("${RedisCommand.TYPE.name} hash")
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
