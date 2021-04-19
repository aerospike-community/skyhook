package com.aerospike.skyhook

import com.aerospike.skyhook.command.RedisCommand
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ListCommandsTest() : SkyhookIntegrationTestBase() {

    private val _key = "list"

    private fun setup(n: Int = 3) {
        for (i in 1..n) {
            writeCommand("${RedisCommand.RPUSH.name} $_key val$i")
            assertEquals(i.toLong(), readLong())
        }
    }

    @Test
    fun testLpush() {
        writeCommand("${RedisCommand.LPUSH.name} $_key val1")
        assertEquals(1, readLong())
        writeCommand("${RedisCommand.LPUSHX.name} $_key val2")
        assertEquals(2, readLong())
        writeCommand("${RedisCommand.LPUSHX.name} list2 val1")
        assertTrue { readError().isNotEmpty() }
        writeCommand("${RedisCommand.LINDEX.name} $_key 0")
        assertEquals("val2", readFullBulkString())
    }

    @Test
    fun testRpush() {
        writeCommand("${RedisCommand.RPUSH.name} $_key val1")
        assertEquals(1, readLong())
        writeCommand("${RedisCommand.RPUSHX.name} $_key val2")
        assertEquals(2, readLong())
        writeCommand("${RedisCommand.RPUSHX.name} list2 val1")
        assertTrue { readError().isNotEmpty() }
        writeCommand("${RedisCommand.LINDEX.name} $_key 0")
        assertEquals("val1", readFullBulkString())
    }

    @Test
    fun testLpop() {
        setup()
        writeCommand("${RedisCommand.LPOP.name} $_key")
        val lpopRes = readStringArray()
        assertEquals("val1", lpopRes[0])
        writeCommand("${RedisCommand.LPOP.name} $_key 2")
        val lpopRes2 = readStringArray()
        assertEquals("val2", lpopRes2[0])
        assertEquals("val3", lpopRes2[1])
    }

    @Test
    fun testRpop() {
        setup()
        writeCommand("${RedisCommand.RPOP.name} $_key")
        val rpopRes = readStringArray()
        assertEquals("val3", rpopRes[0])
        writeCommand("${RedisCommand.RPOP.name} $_key 2")
        val rpopRes2 = readStringArray()
        assertEquals("val1", rpopRes2[0])
        assertEquals("val2", rpopRes2[1])
    }

    @Test
    fun testLlen() {
        setup()
        writeCommand("${RedisCommand.LLEN.name} $_key")
        assertEquals(3, readLong())
    }

    @Test
    fun testLindex() {
        setup()
        writeCommand("${RedisCommand.LINDEX.name} $_key 0")
        assertEquals("val1", readFullBulkString())
        writeCommand("${RedisCommand.LINDEX.name} $_key 2")
        assertEquals("val3", readFullBulkString())
    }

    @Test
    fun testLrange() {
        setup()
        writeCommand("${RedisCommand.LRANGE.name} $_key 0 3")
        val r = readStringArray()
        assertEquals("val1", r[0])
        assertEquals("val2", r[1])
        assertEquals("val3", r[2])
    }
}
