package com.aerospike.skyhook

import com.aerospike.skyhook.command.RedisCommand
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class HashCommandsTest() : SkyhookIntegrationTestBase() {

    private val _key = "hash"

    private fun setup(n: Int = 3) {
        for (i in 1..n) {
            writeCommand("${RedisCommand.HSET.name} $_key key$i val$i")
            assertEquals(1, readLong())
        }
    }

    @Test
    fun testHget() {
        setup()
        writeCommand("${RedisCommand.HGET.name} $_key key1")
        assertEquals("val1", readFullBulkString())
        writeCommand("${RedisCommand.HGET.name} $_key key11")
        assertEquals(nullString, readFullBulkString())
        writeCommand("${RedisCommand.HGET.name} ne key1")
        assertEquals(nullString, readFullBulkString())
    }

    @Test
    fun testHsetnx() {
        writeCommand("${RedisCommand.HSETNX.name} $_key key1 val1")
        assertEquals(1, readLong())
        writeCommand("${RedisCommand.HSETNX.name} $_key key1 val11")
        assertEquals(0, readLong())
        writeCommand("${RedisCommand.HGET.name} $_key key1")
        assertEquals("val1", readFullBulkString())
    }

    @Test
    fun testHmset() {
        writeCommand("${RedisCommand.HMSET.name} $_key key1 val1 key2 val2")
        assertEquals(ok, readString())
        writeCommand("${RedisCommand.HGET.name} $_key key1")
        assertEquals("val1", readFullBulkString())
        writeCommand("${RedisCommand.HGET.name} $_key key2")
        assertEquals("val2", readFullBulkString())
    }

    @Test
    fun testHexists() {
        setup(1)
        writeCommand("${RedisCommand.HEXISTS.name} $_key key1")
        assertEquals(1, readLong())
        writeCommand("${RedisCommand.HEXISTS.name} $_key key2")
        assertEquals(0, readLong())
    }

    @Test
    fun testHmget() {
        setup()
        writeCommand("${RedisCommand.HMGET.name} $_key key1 key2 key3")
        val r = readStringArray()
        assertEquals("val1", r[0])
        assertEquals("val2", r[1])
        assertEquals("val3", r[2])
    }

    @Test
    fun testHgetall() {
        setup()
        writeCommand("${RedisCommand.HGETALL.name} $_key")
        val r = readStringArray()
        assertEquals("key1", r[0])
        assertEquals("val1", r[1])
        assertEquals("key2", r[2])
        assertEquals("val2", r[3])
        assertEquals("key3", r[4])
        assertEquals("val3", r[5])
        writeCommand("${RedisCommand.HGETALL.name} ne")
        val r2 = readStringArray()
        assertTrue { r2.isEmpty() }
    }

    @Test
    fun testHkeys() {
        setup()
        writeCommand("${RedisCommand.HKEYS.name} $_key")
        val r = readStringArray()
        assertEquals("key1", r[0])
        assertEquals("key2", r[1])
        assertEquals("key3", r[2])
        writeCommand("${RedisCommand.HKEYS.name} ne")
        val r2 = readStringArray()
        assertTrue { r2.isEmpty() }
    }

    @Test
    fun testHvals() {
        setup()
        writeCommand("${RedisCommand.HVALS.name} $_key")
        val r = readStringArray()
        assertEquals("val1", r[0])
        assertEquals("val2", r[1])
        assertEquals("val3", r[2])
        writeCommand("${RedisCommand.HVALS.name} ne")
        val r2 = readStringArray()
        assertTrue { r2.isEmpty() }
    }

    @Test
    fun testHincrby() {
        writeCommand("${RedisCommand.HSET.name} $_key key1 10")
        assertEquals(1, readLong())
        writeCommand("${RedisCommand.HINCRBY.name} $_key key1 5")
        assertEquals(15, readLong())
    }

    @Test
    fun testHstrlen() {
        setup(1)
        writeCommand("${RedisCommand.HSTRLEN.name} $_key key1")
        assertEquals(4, readLong())
        writeCommand("${RedisCommand.HSTRLEN.name} $_key key2")
        assertEquals(0, readLong())
        writeCommand("${RedisCommand.HSTRLEN.name} hash2 key1")
        assertEquals(0, readLong())
    }

    @Test
    fun testHlen() {
        setup()
        writeCommand("${RedisCommand.HLEN.name} $_key")
        assertEquals(3, readLong())
        writeCommand("${RedisCommand.HLEN.name} hash2")
        assertEquals(0, readLong())
    }

    @Test
    fun testHdel() {
        setup(1)
        writeCommand("${RedisCommand.HDEL.name} $_key key1")
        assertEquals(1, readLong())
        writeCommand("${RedisCommand.HDEL.name} $_key key1")
        assertEquals(0, readLong())
        writeCommand("${RedisCommand.HGET.name} $_key key1")
        assertEquals(nullString, readFullBulkString())
    }
}
