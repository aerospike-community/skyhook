package com.aerospike.skyhook

import com.aerospike.skyhook.command.RedisCommand
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SetCommandsTest() : SkyhookIntegrationTestBase() {

    private val _key = "set"

    private fun setup(n: Int = 3, key: String = _key) {
        for (i in 1..n) {
            writeCommand("${RedisCommand.SADD.name} $key val$i")
            assertEquals(1, readLong())
        }
    }

    @Test
    fun testSadd() {
        setup(2)
        writeCommand("${RedisCommand.SADD.name} $_key val1")
        assertEquals(0, readLong())
    }

    @Test
    fun testSismember() {
        setup(1)
        writeCommand("${RedisCommand.SISMEMBER.name} $_key val1")
        assertEquals(1, readLong())
        writeCommand("${RedisCommand.SISMEMBER.name} $_key val2")
        assertEquals(0, readLong())
        writeCommand("${RedisCommand.SISMEMBER.name} ne val1")
        assertEquals(0, readLong())
    }

    @Test
    fun testSmismember() {
        setup(1)
        writeCommand("${RedisCommand.SMISMEMBER.name} $_key val1 val2 val3")
        val r = readLongArray()
        assertEquals(1, r[0])
        assertEquals(0, r[1])
        assertEquals(0, r[2])

        writeCommand("${RedisCommand.SMISMEMBER.name} ne val1 val2")
        val r2 = readLongArray()
        assertEquals(0, r2[0])
        assertEquals(0, r2[1])
    }

    @Test
    fun testSmembers() {
        setup()
        writeCommand("${RedisCommand.SMEMBERS.name} $_key")
        val r = readStringArray()
        assertEquals("val1", r[0])
        assertEquals("val2", r[1])
        assertEquals("val3", r[2])
    }

    @Test
    fun testScard() {
        setup()
        writeCommand("${RedisCommand.SCARD.name} $_key")
        assertEquals(3, readLong())
        writeCommand("${RedisCommand.SCARD.name} set2")
        assertEquals(0, readLong())
    }

    @Test
    fun testSrem() {
        setup(1)
        writeCommand("${RedisCommand.SREM.name} $_key val1")
        assertEquals(1, readLong())
        writeCommand("${RedisCommand.SREM.name} $_key val2")
        assertEquals(0, readLong())
    }

    @Test
    fun testSunion() {
        setup()
        setup(4, "set2")
        writeCommand("${RedisCommand.SUNION.name} $_key set2")
        val r = readStringArray()
        assertTrue { r.size == 4 }
        assertTrue { r.contains("val1") }
        assertTrue { r.contains("val2") }
        assertTrue { r.contains("val3") }
        assertTrue { r.contains("val4") }
    }

    @Test
    fun testSunionstore() {
        setup()
        setup(4, "set2")
        writeCommand("${RedisCommand.SUNIONSTORE.name} union $_key set2")
        assertEquals(4, readLong())
    }

    @Test
    fun testSinter() {
        setup()
        setup(4, "set2")
        writeCommand("${RedisCommand.SINTER.name} $_key set2")
        val r = readStringArray()
        assertTrue { r.size == 3 }
        assertTrue { r.contains("val1") }
        assertTrue { r.contains("val2") }
        assertTrue { r.contains("val3") }
    }

    @Test
    fun testSinterstore() {
        setup()
        setup(4, "set2")
        writeCommand("${RedisCommand.SINTERSTORE.name} inter $_key set2")
        assertEquals(3, readLong())
    }

    @Test
    fun testSrandmember() {
        setup()
        writeCommand("${RedisCommand.SRANDMEMBER.name} $_key")
        val r = readFullBulkString()
        assertTrue { r.startsWith("val") }

        writeCommand("${RedisCommand.SRANDMEMBER.name} $_key 5")
        val r2 = readStringArray()
        assertTrue { r2.size == 3 }

        writeCommand("${RedisCommand.SRANDMEMBER.name} $_key -5")
        val r3 = readStringArray()
        assertTrue { r3.size == 5 }

        writeCommand("${RedisCommand.SRANDMEMBER.name} ne")
        assertEquals(nullString, readFullBulkString())
    }
}
