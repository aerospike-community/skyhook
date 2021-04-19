package com.aerospike.skyhook

import com.aerospike.client.Bin
import com.aerospike.client.IAerospikeClient
import com.aerospike.client.Key
import com.aerospike.client.Value
import com.aerospike.skyhook.command.RedisCommand
import com.aerospike.skyhook.config.ServerConfiguration
import com.aerospike.skyhook.handler.AerospikeChannelHandler
import com.google.inject.Guice
import io.netty.buffer.Unpooled.buffer
import io.netty.channel.embedded.EmbeddedChannel
import io.netty.handler.codec.redis.*
import org.junit.jupiter.api.AfterEach
import kotlin.experimental.and
import kotlin.test.assertEquals

abstract class SkyhookIntegrationTestBase {

    companion object {
        private val config = ServerConfiguration()
        private val injector = Guice.createInjector(SkyhookModule(config))

        protected val client: IAerospikeClient = injector.getInstance(IAerospikeClient::class.java)

        private val aerospikeChannelHandler = injector.getInstance(AerospikeChannelHandler::class.java)

        @JvmStatic
        protected val ok = "OK"

        @JvmStatic
        protected val nullString = ""
        private const val eol = "\r\n"
        private const val sleepMillis = 50L
    }

    @AfterEach
    protected fun clear() {
        writeCommand(RedisCommand.FLUSHDB.name)
        assertEquals(ok, readString())
    }

    private val channel = EmbeddedChannel(
        RedisDecoder(true),
        RedisBulkStringAggregator(),
        RedisArrayAggregator(),
        aerospikeChannelHandler,
        RedisEncoder(),
    )

    protected fun aeroKey(key: Any): Key {
        return Key(config.namespase, config.set, Value.get(key))
    }

    protected fun aeroBin(bin: Any): Bin {
        return Bin(config.bin, bin)
    }

    protected fun writeCommand(command: String) {
        val sb = StringBuilder()
        val list = command.split(" ")
        if (list.size > 1) {
            sb.append("*${list.size}$eol")
            command.split(" ").forEach { s ->
                sb.append("$${s.length}$eol")
                sb.append("$s$eol")
            }
        } else {
            sb.append("+")
            sb.append("${list[0]}$eol")
        }
        val byteBuf = buffer().writeBytes(sb.toString().toByteArray())
        channel.writeInbound(byteBuf)
    }

    protected fun readStringArray(): Array<String> {
        Thread.sleep(sleepMillis)
        val len = channel.readOutbound<ArrayHeaderRedisMessage>().length()
        return (1..len).map {
            String(channel.readOutbound<FullBulkStringRedisMessage>().content().array())
        }.toTypedArray()
    }

    protected fun readLongArray(): Array<Long> {
        Thread.sleep(sleepMillis)
        val len = channel.readOutbound<ArrayHeaderRedisMessage>().length()
        return (1..len).map {
            channel.readOutbound<IntegerRedisMessage>().value()
        }.toTypedArray()
    }

    protected fun readString(): String {
        Thread.sleep(sleepMillis)
        return channel.readOutbound<SimpleStringRedisMessage>().content()
    }

    protected fun readFullBulkString(): String {
        Thread.sleep(sleepMillis)
        return String(channel.readOutbound<FullBulkStringRedisMessage>().content().array())
    }

    protected fun readLong(): Long {
        Thread.sleep(sleepMillis)
        return channel.readOutbound<IntegerRedisMessage>().value()
    }

    protected fun readError(): String {
        Thread.sleep(sleepMillis)
        return channel.readOutbound<ErrorRedisMessage>().content()
    }

    private val hexArray = "0123456789ABCDEF".toCharArray()
    protected fun bytesToHex(bytes: ByteArray): String {
        val hexChars = CharArray(bytes.size * 2)
        for (j in bytes.indices) {
            val v = (bytes[j] and 0xFF.toByte()).toInt()

            hexChars[j * 2] = hexArray[v ushr 4]
            hexChars[j * 2 + 1] = hexArray[v and 0x0F]
        }
        return String(hexChars)
    }
}
