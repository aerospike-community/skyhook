package com.aerospike.skyhook

import com.aerospike.skyhook.command.RedisCommand
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SystemCommandsTest() : SkyhookIntegrationTestBase() {

    @Test
    fun testPing() {
        writeCommand(RedisCommand.PING.name)
        val response = readString()
        assertEquals("PONG", response)
    }

    @Test
    fun testEcho() {
        writeCommand("${RedisCommand.ECHO.name} abc")
        val response = readString()
        assertEquals("abc", response)
    }

    @Test
    fun testCommandInfo() {
        writeCommand("${RedisCommand.COMMAND.name} COUNT")
        val commands = readLong()
        assertTrue { commands > 50 }
    }

    @Test
    fun testReset() {
        writeCommand(RedisCommand.RESET.name)
        val response = readString()
        assertEquals("RESET", response)
    }

    @Test
    fun testSave() {
        writeCommand(RedisCommand.SAVE.name)
        val response = readString()
        assertEquals(ok, response)
    }

    @Test
    fun testBgsave() {
        writeCommand(RedisCommand.BGSAVE.name)
        val response = readString()
        assertEquals(ok, response)
    }

    @Test
    fun testQuit() {
        writeCommand(RedisCommand.QUIT.name)
        val response = readString()
        assertEquals(ok, response)
    }

    @Test
    fun testTime() {
        writeCommand(RedisCommand.TIME.name)
        val response = readStringArray()
        assertEquals(2, response.size)
    }
}
