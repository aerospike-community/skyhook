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
    fun testGet() {
        writeCommand("${RedisCommand.SET.name} key1 val1")
        assertEquals(ok, readString())
        writeCommand("${RedisCommand.GET.name} key1")
        assertEquals("val1", readFullBulkString())

        writeCommand("${RedisCommand.SET.name} key1 1")
        assertEquals(ok, readString())
        writeCommand("${RedisCommand.GET.name} key1")
        assertEquals("1", readFullBulkString())

        writeCommand("${RedisCommand.SET.name} key1 1.25")
        assertEquals(ok, readString())
        writeCommand("${RedisCommand.GET.name} key1")
        assertEquals("1.25", readFullBulkString())

        writeCommand("${RedisCommand.GET.name} ne")
        assertEquals(nullString, readFullBulkString())
    }

    @Test
    fun testGetex() {
        setup(1)
        writeCommand("${RedisCommand.GETEX.name} key1 EX 10 PX 10000")
        assert(readError().isNotEmpty())

        writeCommand("${RedisCommand.GETEX.name} key1")
        assertEquals("val1", readFullBulkString())

        writeCommand("${RedisCommand.GETEX.name} key1 EX 10")
        assertEquals("val1", readFullBulkString())
        writeCommand("${RedisCommand.TTL.name} key1")
        assertTrue(readLong() in 9..10)

        writeCommand("${RedisCommand.GETEX.name} key1 PX 10000")
        assertEquals("val1", readFullBulkString())
        writeCommand("${RedisCommand.PTTL.name} key1")
        assertTrue(readLong() in 9000..10000)

        writeCommand("${RedisCommand.GETEX.name} ne")
        assertEquals(nullString, readFullBulkString())
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
    fun testSet() {
        writeCommand("${RedisCommand.SET.name} key1 val1")
        assertEquals(ok, readString())
        writeCommand("${RedisCommand.SET.name} key1 val2 GET")
        assertEquals("val1", readFullBulkString())
        writeCommand("${RedisCommand.SET.name} key1 val2 NX GET")
        assert(readError().isNotEmpty())
        writeCommand("${RedisCommand.SET.name} key1 val2 NX XX")
        assert(readError().isNotEmpty())
        writeCommand("${RedisCommand.SET.name} key1 val3 XX GET")
        assertEquals("val2", readFullBulkString())
        writeCommand("${RedisCommand.SET.name} key2 val2 NX")
        assertEquals(ok, readString())
        writeCommand("${RedisCommand.SET.name} key2 val2 NX")
        assert(readError().isNotEmpty())
        writeCommand("${RedisCommand.SET.name} key2 val2 EX 10 PX 10000")
        assert(readError().isNotEmpty())
        writeCommand("${RedisCommand.SET.name} key2 val2 EX 10")
        assertEquals(ok, readString())
        writeCommand("${RedisCommand.TTL.name} key2")
        assertTrue(readLong() in 9..10)
        writeCommand("${RedisCommand.SET.name} key2 val2 PX 10000")
        assertEquals(ok, readString())
        writeCommand("${RedisCommand.PTTL.name} key2")
        assertTrue(readLong() in 9000..10000)
        writeCommand("${RedisCommand.SET.name} key3 val3 GET")
        assertEquals(nullString, readFullBulkString())
        writeCommand("${RedisCommand.SET.name} key4 val4 XX")
        assertEquals(nullString, readFullBulkString())
        writeCommand("${RedisCommand.GET.name} key4")
        assertEquals(nullString, readFullBulkString())
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
    fun testSetex() {
        setup(1)
        writeCommand("${RedisCommand.SETEX.name} key1 -10 val11")
        assert(readError().isNotEmpty())
        writeCommand("${RedisCommand.SETEX.name} key1 abc val11")
        assert(readError().isNotEmpty())
        writeCommand("${RedisCommand.SETEX.name} key11 10 val11")
        assertEquals(ok, readString())
        writeCommand("${RedisCommand.SETEX.name} key1 10 val11")
        assertEquals(ok, readString())
        writeCommand("${RedisCommand.TTL.name} key1")
        assertTrue(readLong() in 9..10)
    }

    @Test
    fun testPsetex() {
        setup(1)
        writeCommand("${RedisCommand.PSETEX.name} key1 -10 val11")
        assert(readError().isNotEmpty())
        writeCommand("${RedisCommand.PSETEX.name} key1 abc val11")
        assert(readError().isNotEmpty())
        writeCommand("${RedisCommand.PSETEX.name} key11 10000 val11")
        assertEquals(ok, readString())
        writeCommand("${RedisCommand.PSETEX.name} key1 10000 val11")
        assertEquals(ok, readString())
        writeCommand("${RedisCommand.TTL.name} key1")
        assertTrue(readLong() in 9..10)
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
    fun testMset() {
        writeCommand("${RedisCommand.MSET.name} key1 val1 key2 val2")
        assertEquals(ok, readString())
        writeCommand("${RedisCommand.GET.name} key1")
        assertEquals("val1", readFullBulkString())
        writeCommand("${RedisCommand.GET.name} key2")
        assertEquals("val2", readFullBulkString())
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
    fun testGetdel() {
        setup(1)
        writeCommand("${RedisCommand.GETDEL.name} key1")
        assertEquals("val1", readFullBulkString())
        writeCommand("${RedisCommand.GET.name} key1")
        assertEquals(nullString, readFullBulkString())
        writeCommand("${RedisCommand.GETDEL.name} key4")
        assertEquals(nullString, readFullBulkString())
    }

    @Test
    fun testExpire() {
        setup(1)
        writeCommand("${RedisCommand.EXPIRE.name} key11 10")
        assertEquals(0, readLong())
        writeCommand("${RedisCommand.EXPIRE.name} key1 10")
        assertEquals(1, readLong())
        writeCommand("${RedisCommand.TTL.name} key1")
        assertTrue(readLong() in 9..10)
        writeCommand("${RedisCommand.EXPIRE.name} key1 -10")
        assertEquals(1, readLong())
        writeCommand("${RedisCommand.EXISTS.name} key1")
        assertEquals(0, readLong())
    }

    @Test
    fun testPexpire() {
        setup(1)
        writeCommand("${RedisCommand.PEXPIRE.name} key11 10")
        assertEquals(0, readLong())
        writeCommand("${RedisCommand.PEXPIRE.name} key1 10000")
        assertEquals(1, readLong())
        writeCommand("${RedisCommand.PTTL.name} key1")
        assertTrue(readLong() in 9000..10000)
        writeCommand("${RedisCommand.PEXPIRE.name} key1 -10")
        assertEquals(1, readLong())
        writeCommand("${RedisCommand.EXISTS.name} key1")
        assertEquals(0, readLong())
    }

    @Test
    fun testPersist() {
        setup(1)
        writeCommand("${RedisCommand.PERSIST.name} key11")
        assertEquals(0, readLong())
        writeCommand("${RedisCommand.EXPIRE.name} key1 10")
        assertEquals(1, readLong())
        writeCommand("${RedisCommand.TTL.name} key1")
        assertTrue(readLong() in 9..10)
        writeCommand("${RedisCommand.PERSIST.name} key1")
        assertEquals(1, readLong())
        writeCommand("${RedisCommand.TTL.name} key1")
        assertEquals(-1, readLong())
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
    fun testStrlen() {
        setup(1)
        writeCommand("${RedisCommand.STRLEN.name} key1")
        assertEquals(4, readLong())
        writeCommand("${RedisCommand.STRLEN.name} key4")
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
        assertEquals("18.2", readFullBulkString())
        writeCommand("${RedisCommand.INCRBY.name} key2 5")
        assertEquals("23.2", readFullBulkString())
        writeCommand("${RedisCommand.INCR.name} key2")
        assertEquals("24.2", readFullBulkString())
        writeCommand("${RedisCommand.INCRBYFLOAT.name} key2 0.8")
        assertEquals("25", readFullBulkString())
        writeCommand("${RedisCommand.INCRBY.name} key2 5")
        assertEquals(30, readLong())
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
