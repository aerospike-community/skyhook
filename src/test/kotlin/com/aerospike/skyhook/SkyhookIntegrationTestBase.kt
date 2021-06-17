package com.aerospike.skyhook

import com.aerospike.client.Bin
import com.aerospike.client.Key
import com.aerospike.client.Value
import com.aerospike.skyhook.command.RedisCommand
import com.aerospike.skyhook.config.AerospikeContext
import com.aerospike.skyhook.config.ServerConfiguration
import com.aerospike.skyhook.handler.AerospikeChannelHandler
import com.aerospike.skyhook.pipeline.AerospikeChannelInitializer.Companion.aeroCtxAttrKey
import com.aerospike.skyhook.pipeline.AerospikeChannelInitializer.Companion.clientPoolAttrKey
import com.aerospike.skyhook.pipeline.AerospikeChannelInitializer.Companion.transactionAttrKey
import com.aerospike.skyhook.util.ScanResponse
import com.aerospike.skyhook.util.TransactionState
import com.aerospike.skyhook.util.client.AerospikeClientPool
import com.google.inject.Guice
import io.netty.buffer.Unpooled.buffer
import io.netty.channel.embedded.EmbeddedChannel
import io.netty.handler.codec.redis.*
import org.junit.jupiter.api.AfterEach
import java.util.concurrent.ExecutorService
import kotlin.experimental.and
import kotlin.test.assertEquals

abstract class SkyhookIntegrationTestBase {

    companion object {
        private val config = ServerConfiguration()
        private val injector = Guice.createInjector(SkyhookModule(config))

        protected val clientPool: AerospikeClientPool = injector.getInstance(AerospikeClientPool::class.java)

        private val aerospikeChannelHandler = injector.getInstance(AerospikeChannelHandler::class.java)
        protected val executorService: ExecutorService = injector.getInstance(ExecutorService::class.java)

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

    init {
        channel.attr(aeroCtxAttrKey).set(
            AerospikeContext(
                config.namespace,
                config.set,
                config.bin,
                config.typeBin,
                config.transactionIdBin
            )
        )

        channel.attr(clientPoolAttrKey).set(clientPool)
        channel.attr(transactionAttrKey).set(TransactionState(executorService))
    }

    protected fun aeroKey(key: Any): Key {
        return Key(config.namespace, config.set, Value.get(key))
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

    protected fun readArrayLen(): Long {
        Thread.sleep(sleepMillis)
        return channel.readOutbound<ArrayHeaderRedisMessage>().length()
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

    protected fun readScanResponse(): ScanResponse {
        Thread.sleep(sleepMillis)
        var len = channel.readOutbound<ArrayHeaderRedisMessage>().length()
        assertEquals(2, len)
        val cursor = channel.readOutbound<SimpleStringRedisMessage>().content()
        len = channel.readOutbound<ArrayHeaderRedisMessage>().length()
        val elements = (1..len).map {
            String(channel.readOutbound<FullBulkStringRedisMessage>().content().array())
        }.toList()
        return ScanResponse(cursor, elements)
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
