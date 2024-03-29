package com.aerospike.skyhook

import com.aerospike.skyhook.command.RedisCommand
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class HyperLogCommandsTest() : SkyhookIntegrationTestBase() {

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
        writeCommand(RedisCommand.PFCOUNT, "a")
        assertEquals(2, readLong())
        writeCommand(RedisCommand.PFCOUNT, "b")
        assertEquals(2, readLong())
    }

    @Test
    fun countNonExistent() {
        writeCommand(RedisCommand.PFCOUNT, "key")
        assertEquals(0, readLong())
        writeCommand(RedisCommand.PFADD, "a 1")
        assertEquals(1, readLong())
        writeCommand(RedisCommand.PFCOUNT, "key a")
        assertEquals(1, readLong())
        writeCommand(RedisCommand.PFCOUNT, "a key")
        assertEquals(1, readLong())
    }

    @Test
    fun countAllNonExistent() {
        writeCommand(RedisCommand.PFCOUNT, "key")
        assertEquals(0, readLong())
        writeCommand(RedisCommand.PFCOUNT, "key key2")
        assertEquals(0, readLong())
    }

    @Test
    fun countMany() {
        val n = 10L
        (0 until n).forEach {
            writeCommand(RedisCommand.PFADD, "key${it} $it")
            assertEquals(1, readLong())
        }
        val args = (0 until n).joinToString(" ") { "key${it}" }
        writeCommand(RedisCommand.PFCOUNT, args)
        assertEquals(n, readLong())
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

    @Test
    fun mergeNonExistent() {
        writeCommand(RedisCommand.PFADD, "a 1 2")
        assertEquals(1, readLong())
        writeCommand(RedisCommand.PFMERGE, "m a b")
        assertEquals(ok, readString())
        writeCommand(RedisCommand.PFCOUNT, "m")
        assertEquals(2, readLong())
    }
}
