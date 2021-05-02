package com.aerospike.skyhook

import com.aerospike.skyhook.command.RedisCommand
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ScanCommandsTest() : SkyhookIntegrationTestBase() {

    @Test
    fun testScan() {
        writeCommand("${RedisCommand.MSET.name} k1 v1 k2 v2 k3 v3 k4 v4 k5 v5 k6 v6 k7 v7 k11 v11 k8 v8")
        assertEquals(ok, readString())
        writeCommand("${RedisCommand.SADD.name} set a b c")
        assertEquals(3L, readLong())

        writeCommand("${RedisCommand.SCAN.name} 0 COUNT 20 TYPE string MATCH k1*")
        Thread.sleep(5000)
        val resp = readScanResponse()
        assertEquals("0", resp.cursor)
        assertEquals(2, resp.elements.size)
        assertEquals("k11", resp.elements[0])
        assertEquals("k1", resp.elements[1])
    }

    @Test
    fun testHscan() {
        writeCommand("${RedisCommand.HSET.name} hash k1 v1 k2 v2 k3 v3 k4 v4 k5 v5 k6 v6 k7 v7 k8 v8 k9 v9")
        assertEquals(9L, readLong())

        writeCommand("${RedisCommand.HSCAN.name} hash 0 COUNT 5")
        var resp = readScanResponse()
        assertEquals("5", resp.cursor)
        assertEquals(10, resp.elements.size)

        writeCommand("${RedisCommand.HSCAN.name} hash ${resp.cursor} COUNT 5")
        resp = readScanResponse()
        assertEquals("0", resp.cursor)
        assertEquals(8, resp.elements.size)
    }

    @Test
    fun testSscan() {
        writeCommand("${RedisCommand.SADD.name} set a b c d e f g h i")
        assertEquals(9L, readLong())

        writeCommand("${RedisCommand.SSCAN.name} set 0 COUNT 5")
        var resp = readScanResponse()
        assertEquals("5", resp.cursor)
        assertEquals(5, resp.elements.size)

        writeCommand("${RedisCommand.SSCAN.name} set ${resp.cursor} COUNT 5")
        resp = readScanResponse()
        assertEquals("0", resp.cursor)
        assertEquals(4, resp.elements.size)
    }

    @Test
    fun testZscan() {
        writeCommand("${RedisCommand.ZADD.name} zset 0 a 2 b 1 c 4 d 3 e 6 f 5 g 8 h 7 i")
        assertEquals(9L, readLong())

        writeCommand("${RedisCommand.ZSCAN.name} zset 0 COUNT 5")
        var resp = readScanResponse()
        assertEquals("5", resp.cursor)
        assertEquals(5, resp.elements.size)
        assertEquals("a", resp.elements[0])
        assertEquals("c", resp.elements[1])
        assertEquals("b", resp.elements[2])
        assertEquals("e", resp.elements[3])
        assertEquals("d", resp.elements[4])

        writeCommand("${RedisCommand.ZSCAN.name} zset ${resp.cursor} COUNT 5")
        resp = readScanResponse()
        assertEquals("0", resp.cursor)
        assertEquals(4, resp.elements.size)
        assertEquals("g", resp.elements[0])
        assertEquals("f", resp.elements[1])
        assertEquals("i", resp.elements[2])
        assertEquals("h", resp.elements[3])
    }
}
