package com.aerospike.skyhook

import com.aerospike.skyhook.command.RedisCommand
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TransactionTest() : SkyhookIntegrationTestBase() {

    @Test
    fun testTransaction() {
        writeCommand(RedisCommand.MULTI.name)
        assertEquals(ok, readString())

        writeCommand("${RedisCommand.SET.name} key1 val1")
        assertEquals("QUEUED", readString())

        writeCommand("${RedisCommand.GET.name} key1")
        assertEquals("QUEUED", readString())

        writeCommand("NE abc")
        assert(readError().isNotEmpty())

        writeCommand(RedisCommand.PING.name)
        assertEquals("QUEUED", readString())

        writeCommand(RedisCommand.EXEC.name)
        assertEquals(3L, readArrayLen())

        assertEquals(ok, readString())
        assertEquals("val1", readFullBulkString())
        assertEquals("PONG", readString())
    }

    @Test
    fun testDiscardTransaction() {
        writeCommand(RedisCommand.MULTI.name)
        assertEquals(ok, readString())

        writeCommand("${RedisCommand.SET.name} key1 val1")
        assertEquals("QUEUED", readString())

        writeCommand("${RedisCommand.GET.name} key1")
        assertEquals("QUEUED", readString())

        writeCommand(RedisCommand.DISCARD.name)
        assertEquals(ok, readString())

        writeCommand(RedisCommand.EXEC.name)
        assert(readError().isNotEmpty())

        writeCommand(RedisCommand.PING.name)
        assertEquals("PONG", readString())
    }

    @Test
    fun testExecWithoutMultiTransaction() {
        writeCommand(RedisCommand.EXEC.name)
        assert(readError().isNotEmpty())
    }
}
