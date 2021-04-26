package com.aerospike.skyhook

import com.aerospike.skyhook.command.RedisCommand
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SortedSetCommandsTest() : SkyhookIntegrationTestBase() {

    private val _key = "zset"

    private fun setup(n: Int = 3, key: String = _key, v: String = "val") {
        for (i in 1..n) {
            writeCommand("${RedisCommand.ZADD.name} $key $i $v$i")
            assertEquals(1, readLong())
        }
    }

    @Test
    fun testZmscore() {
        setup()
        writeCommand("${RedisCommand.ZMSCORE.name} $_key val1 val2 val3")
        val r = readStringArray()
        assertTrue { r.size == 3 }
        assertEquals("1", r[0])
        assertEquals("2", r[1])
        assertEquals("3", r[2])
    }

    @Test
    fun testZcard() {
        setup()
        writeCommand("${RedisCommand.ZCARD.name} $_key")
        assertEquals(3, readLong())
    }

    @Test
    fun testZrem() {
        setup()
        writeCommand("${RedisCommand.ZREM.name} $_key val1")
        assertEquals(1, readLong())
        writeCommand("${RedisCommand.ZREM.name} $_key val11")
        assertEquals(0, readLong())
        writeCommand("${RedisCommand.ZCARD.name} $_key")
        assertEquals(2, readLong())
    }

    @Test
    fun testZincby() {
        setup(1)
        writeCommand("${RedisCommand.ZINCRBY.name} $_key 10 val1")
        assertEquals("11", readFullBulkString())
    }

    @Test
    fun testZrank() {
        setup(1)
        writeCommand("${RedisCommand.ZRANK.name} $_key val1")
        assertEquals(1, readLong())
        writeCommand("${RedisCommand.ZRANK.name} $_key val2")
        assertEquals(nullString, readFullBulkString())
    }

    @Test
    fun testZpopmax() {
        setup()
        setup(3, _key, "v")
        writeCommand("${RedisCommand.ZPOPMAX.name} $_key 3")
        val r = readStringArray()
        assertTrue { r.size == 6 }
        assertEquals("val3", r[0])
        assertEquals("3", r[1])
        assertEquals("v3", r[2])
        assertEquals("3", r[3])
        assertEquals("val2", r[4])
        assertEquals("2", r[5])

        writeCommand("${RedisCommand.ZPOPMAX.name} $_key")
        val r2 = readStringArray()
        assertTrue { r2.size == 2 }
        assertEquals("v2", r2[0])
        assertEquals("2", r2[1])

        writeCommand("${RedisCommand.ZPOPMAX.name} $_key 0")
        val r3 = readStringArray()
        assertTrue { r3.isEmpty() }
    }

    @Test
    fun testZpopmain() {
        setup()
        setup(3, _key, "v")
        writeCommand("${RedisCommand.ZPOPMIN.name} $_key 3")
        val r = readStringArray()
        assertTrue { r.size == 6 }
        assertEquals("v1", r[0])
        assertEquals("1", r[1])
        assertEquals("val1", r[2])
        assertEquals("1", r[3])
        assertEquals("v2", r[4])
        assertEquals("2", r[5])

        writeCommand("${RedisCommand.ZPOPMIN.name} $_key")
        val r2 = readStringArray()
        assertTrue { r2.size == 2 }
        assertEquals("val2", r2[0])
        assertEquals("2", r2[1])

        writeCommand("${RedisCommand.ZPOPMIN.name} $_key 0")
        val r3 = readStringArray()
        assertTrue { r3.isEmpty() }
    }

    @Test
    fun testZrandmember() {
        setup()
        writeCommand("${RedisCommand.ZRANDMEMBER.name} $_key 5")
        val r = readStringArray()
        assertTrue { r.size == 3 }

        writeCommand("${RedisCommand.ZRANDMEMBER.name} $_key -5")
        val r2 = readStringArray()
        assertTrue { r2.size == 5 }

        writeCommand("${RedisCommand.ZRANDMEMBER.name} $_key 5 WITHSCORES")
        val r3 = readStringArray()
        assertTrue { r3.size == 6 }

        writeCommand("${RedisCommand.ZRANDMEMBER.name} $_key -5 WITHSCORES")
        val r4 = readStringArray()
        assertTrue { r4.size == 10 }
    }

    @Test
    fun testZcount() {
        setup(6)
        writeCommand("${RedisCommand.ZCOUNT.name} $_key -inf +inf")
        assertEquals(6, readLong())
        writeCommand("${RedisCommand.ZCOUNT.name} $_key 2 (5")
        assertEquals(3, readLong())
        writeCommand("${RedisCommand.ZCOUNT.name} $_key (1 4")
        assertEquals(3, readLong())
        writeCommand("${RedisCommand.ZCOUNT.name} ne 1 2")
        assertEquals(0, readLong())
        writeCommand("${RedisCommand.ZCOUNT.name} $_key a b")
        assertTrue { readError().isNotEmpty() }
    }

    @Test
    fun testZremrangebyscore() {
        setup(6)
        writeCommand("${RedisCommand.ZREMRANGEBYSCORE.name} $_key -inf +inf")
        assertEquals(6, readLong())
        writeCommand("${RedisCommand.ZCARD.name} $_key")
        assertEquals(0, readLong())

        clear()
        setup(6)
        writeCommand("${RedisCommand.ZREMRANGEBYSCORE.name} $_key 2 (5")
        assertEquals(3, readLong())
        writeCommand("${RedisCommand.ZCARD.name} $_key")
        assertEquals(3, readLong())

        clear()
        setup(6)
        writeCommand("${RedisCommand.ZREMRANGEBYSCORE.name} $_key (1 4")
        assertEquals(3, readLong())
        writeCommand("${RedisCommand.ZCARD.name} $_key")
        assertEquals(3, readLong())

        writeCommand("${RedisCommand.ZREMRANGEBYSCORE.name} ne 1 2")
        assertEquals(0, readLong())
        writeCommand("${RedisCommand.ZREMRANGEBYSCORE.name} $_key a b")
        assertTrue { readError().isNotEmpty() }
    }

    @Test
    fun testZrange() {
        setup(6)
        writeCommand("${RedisCommand.ZRANGE.name} $_key -inf +inf WITHSCORES")
        assertEquals(12, readStringArray().size)

        writeCommand("${RedisCommand.ZRANGE.name} $_key (2 3 WITHSCORES")
        val r = readStringArray()
        assertTrue { r.size == 2 }
        assertEquals("val4", r[0])
        assertEquals("4", r[1])

        writeCommand("${RedisCommand.ZRANGE.name} $_key (2 4 BYSCORE")
        val r2 = readStringArray()
        assertTrue { r2.size == 2 }
        assertEquals("val3", r2[0])
        assertEquals("val4", r2[1])

        writeCommand("${RedisCommand.ZRANGE.name} $_key 3 (6 BYSCORE REV LIMIT 1 2")
        val r3 = readStringArray()
        assertTrue { r3.size == 2 }
        assertEquals("val4", r3[0])
        assertEquals("val3", r3[1])
    }

    @Test
    fun testZrangestore() {
        setup(6)
        writeCommand("${RedisCommand.ZRANGESTORE.name} zset2 $_key (2 3")
        assertEquals(1, readLong())

        writeCommand("${RedisCommand.ZRANGE.name} zset2 -inf +inf WITHSCORES")
        val r = readStringArray()
        assertTrue { r.size == 2 }
        assertEquals("val4", r[0])
        assertEquals("4", r[1])

        writeCommand("${RedisCommand.ZRANGESTORE.name} zset3 $_key (2 3 BYSCORE")
        assertEquals(1, readLong())

        writeCommand("${RedisCommand.ZRANGE.name} zset3 -inf +inf WITHSCORES")
        val r2 = readStringArray()
        assertTrue { r2.size == 2 }
        assertEquals("val3", r2[0])
        assertEquals("3", r2[1])
    }

    @Test
    fun testZrevrange() {
        setup(6)
        writeCommand("${RedisCommand.ZREVRANGE.name} $_key -inf +inf WITHSCORES")
        assertEquals(12, readStringArray().size)

        writeCommand("${RedisCommand.ZREVRANGE.name} $_key (2 4 WITHSCORES")
        val r = readStringArray()
        assertTrue { r.size == 4 }
        assertEquals("val5", r[0])
        assertEquals("5", r[1])
        assertEquals("val4", r[2])
        assertEquals("4", r[3])

        writeCommand("${RedisCommand.ZREVRANGE.name} $_key (2 4")
        val r2 = readStringArray()
        assertTrue { r2.size == 2 }
        assertEquals("val5", r2[0])
        assertEquals("val4", r2[1])
    }

    @Test
    fun testZrangebyscore() {
        setup(6)
        writeCommand("${RedisCommand.ZRANGEBYSCORE.name} $_key -inf +inf WITHSCORES")
        assertEquals(12, readStringArray().size)

        writeCommand("${RedisCommand.ZRANGEBYSCORE.name} $_key (2 4 WITHSCORES")
        val r = readStringArray()
        assertTrue { r.size == 4 }
        assertEquals("val3", r[0])
        assertEquals("3", r[1])
        assertEquals("val4", r[2])
        assertEquals("4", r[3])

        writeCommand("${RedisCommand.ZRANGEBYSCORE.name} $_key (2 4")
        val r2 = readStringArray()
        assertTrue { r2.size == 2 }
        assertEquals("val3", r2[0])
        assertEquals("val4", r2[1])
    }

    @Test
    fun testZrevrangebyscore() {
        setup(6)
        writeCommand("${RedisCommand.ZREVRANGEBYSCORE.name} $_key -inf +inf WITHSCORES")
        assertEquals(12, readStringArray().size)

        writeCommand("${RedisCommand.ZREVRANGEBYSCORE.name} $_key (2 4 WITHSCORES")
        val r = readStringArray()
        assertTrue { r.size == 4 }
        assertEquals("val4", r[0])
        assertEquals("4", r[1])
        assertEquals("val3", r[2])
        assertEquals("3", r[3])

        writeCommand("${RedisCommand.ZREVRANGEBYSCORE.name} $_key (2 4")
        val r2 = readStringArray()
        assertTrue { r2.size == 2 }
        assertEquals("val4", r2[0])
        assertEquals("val3", r2[1])
    }
}
