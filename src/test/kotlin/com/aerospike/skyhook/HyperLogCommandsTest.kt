package com.aerospike.skyhook

import com.aerospike.skyhook.command.RedisCommand
import org.junit.jupiter.api.Test
import kotlin.test.Ignore
import kotlin.test.assertEquals

class HyperLogCommandsTest() : SkyhookIntegrationTestBase() {

    companion object {
        @JvmStatic
        protected val ok = "OK"
    }

    @Test
    fun simpleAdd() {
        writeCommand(RedisCommand.PFADD, "ids ABC")
        assertEquals(1, readLong())
        writeCommand(RedisCommand.PFCOUNT, "ids")
        assertEquals(1, readLong())
    }

    @Test
    fun multipleAdd() {
        writeCommand(RedisCommand.PFADD, "ids 1 2 3")
        assertEquals(1, readLong())
        writeCommand(RedisCommand.PFCOUNT, "ids")
        assertEquals(3, readLong())
    }

    @Test
    fun duplicateAdd() {
        writeCommand(RedisCommand.PFADD, "ids ABC")
        assertEquals(1, readLong())
        writeCommand(RedisCommand.PFADD, "ids ABC")
        assertEquals(0, readLong())
        writeCommand(RedisCommand.PFADD, "ids ABC ABC")
        assertEquals(0, readLong())
        writeCommand(RedisCommand.PFCOUNT, "ids")
        assertEquals(1, readLong())
    }

    @Test
    fun redisDocumentationExample() {
        writeCommand(RedisCommand.PFADD, "hll foo bar zap")
        assertEquals(1, readLong())
        writeCommand(RedisCommand.PFADD, "hll zap zap zap")
        assertEquals(0, readLong())
        writeCommand(RedisCommand.PFADD, "hll foo bar")
        assertEquals(0, readLong())
        writeCommand(RedisCommand.PFCOUNT, "hll")
        assertEquals(3, readLong())
        writeCommand(RedisCommand.PFADD, "some-other-hll 1 2 3")
        assertEquals(1, readLong())
        writeCommand(RedisCommand.PFCOUNT, "hll some-other-hll")
        assertEquals(6, readLong())
    }

    @Test
    fun union() {
        writeCommand(RedisCommand.PFADD, "a 1 2")
        assertEquals(1, readLong())
        writeCommand(RedisCommand.PFADD, "b 2 3")
        assertEquals(1, readLong())
        writeCommand(RedisCommand.PFCOUNT, "a b")
        assertEquals(3, readLong())
    }

    @Test
    fun countNotExisting() {
        writeCommand(RedisCommand.PFCOUNT, "key")
        assertEquals(0, readLong())
        writeCommand(RedisCommand.PFADD, "a 1")
        assertEquals(1, readLong())
        writeCommand(RedisCommand.PFCOUNT, "key a")
        assertEquals(1, readLong())
    }

    @Test
    fun merge() {
        writeCommand(RedisCommand.PFADD, "a 1 2")
        assertEquals(1, readLong())
        writeCommand(RedisCommand.PFADD, "b 2 3")
        assertEquals(1, readLong())
        writeCommand(RedisCommand.PFMERGE, "m a b")
        assertEquals(ok, readString())
        writeCommand(RedisCommand.PFCOUNT, "m")
        assertEquals(3, readLong())
    }
}
