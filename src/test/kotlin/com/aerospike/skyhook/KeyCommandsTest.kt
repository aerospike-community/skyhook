package com.aerospike.skyhook

import com.aerospike.skyhook.command.RedisCommand
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class KeyCommandsTest() : SkyhookIntegrationTestBase() {

    private fun setup(n: Int = 3) {
        for (i in 1..n) {
            writeCommand("${RedisCommand.SET.name} key$i val$i")
            assertEquals(ok, readString())
        }
    }

    @Test
    fun testMget() {
        setup()
        writeCommand("${RedisCommand.MGET.name} key1 key2 key3")
        val r = readStringArray()
        assertEquals("val1", r[0])
        assertEquals("val2", r[1])
        assertEquals("val3", r[2])
    }

    @Test
    fun testSetnx() {
        setup(1)
        writeCommand("${RedisCommand.SETNX.name} key1 val11")
        assertEquals(0, readLong())
        writeCommand("${RedisCommand.SETNX.name} key4 val4")
        assertEquals(1, readLong())
        writeCommand("${RedisCommand.DEL.name} key4")
        assertEquals(1, readLong())
    }

    @Test
    fun testMsetnx() {
        setup(1)
        writeCommand("${RedisCommand.MSETNX.name} key1 val11 key4 val4")
        assertEquals(0, readLong())
        writeCommand("${RedisCommand.MSETNX.name} key4 val4 key5 val5")
        assertEquals(1, readLong())
    }

    @Test
    fun testGetset() {
        setup(1)
        writeCommand("${RedisCommand.GETSET.name} key1 val11")
        assertEquals("val1", readFullBulkString())
        writeCommand("${RedisCommand.GETSET.name} key4 val4")
        assertEquals(nullString, readFullBulkString())
    }

    @Test
    fun testExists() {
        setup(1)
        writeCommand("${RedisCommand.EXISTS.name} key1")
        assertEquals(1, readLong())
        writeCommand("${RedisCommand.TOUCH.name} key4")
        assertEquals(0, readLong())
    }

    @Test
    fun testAppend() {
        setup(1)
        writeCommand("${RedisCommand.APPEND.name} key1 1")
        assertEquals(5, readLong())
        writeCommand("${RedisCommand.GET.name} key1")
        assertEquals("val11", readFullBulkString())
    }

    @Test
    fun testIncr() {
        writeCommand("${RedisCommand.SET.name} key1 10")
        assertEquals(ok, readString())
        writeCommand("${RedisCommand.INCR.name} key1")
        assertEquals(11, readLong())
        writeCommand("${RedisCommand.INCRBY.name} key1 5")
        assertEquals(16, readLong())
        writeCommand("${RedisCommand.SET.name} key2 10.5")
        assertEquals(ok, readString())
        writeCommand("${RedisCommand.INCRBYFLOAT.name} key2 7.7")
        assertEquals("18.200000", readFullBulkString())
    }

    @Test
    fun testDecr() {
        writeCommand("${RedisCommand.SET.name} key1 10")
        assertEquals(ok, readString())
        writeCommand("${RedisCommand.DECR.name} key1")
        assertEquals(9, readLong())
        writeCommand("${RedisCommand.DECRBY.name} key1 5")
        assertEquals(4, readLong())
    }

    @Test
    fun testRandomkey() {
        setup(3)
        writeCommand(RedisCommand.RANDOMKEY.name)
        Thread.sleep(2000)
        assertTrue { readFullBulkString().startsWith("key") }
    }
}
