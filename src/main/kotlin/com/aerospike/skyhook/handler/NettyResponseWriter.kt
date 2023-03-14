package com.aerospike.skyhook.handler

import com.aerospike.client.Value
import com.aerospike.client.Value.*
import com.aerospike.skyhook.listener.BaseListener
import com.aerospike.skyhook.pipeline.AerospikeChannelInitializer
import com.aerospike.skyhook.util.notify
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.redis.*
import java.io.IOException

open class NettyResponseWriter(
    protected val ctx: ChannelHandlerContext
) {

    companion object {
        private val pool: RedisMessagePool = FixedRedisMessagePool.INSTANCE
    }

    @Throws(IOException::class)
    fun writeObject(value: Any?) {
        when (value) {
            null -> {
                writeNullString()
            }

            is List<*> -> {
                writeObjectList(value as List<*>?)
            }

            is Set<*> -> {
                writeObjectList(value.toList())
            }

            is ByteArray -> {
                writeByteArray(value as ByteArray?)
            }

            is String -> {
                writeBulkString(value as String?)
            }

            is Long -> {
                writeLong(value as Long?)
            }

            is Int -> {
                writeLong(value as Int?)
            }

            is Double -> {
                writeFloat(value as Double?)
            }

            else -> {
                throw IllegalArgumentException("Unsupported value type")
            }
        }
    }

    @Throws(IOException::class)
    fun writeObjectASBulkString(value: Any?) {
        when (value) {
            null -> {
                writeNullString()
            }

            is ByteArray -> {
                writeByteArray(value as ByteArray?)
            }

            is String -> {
                writeBulkString(value as String?)
            }

            is Long, is Int -> {
                writeBulkString(value.toString())
            }

            is Double -> {
                writeBulkString(value.toString())
            }

            is Value -> {
                writeObjectASBulkString(value.getObject())
            }

            else -> {
                writeNullString()
            }
        }
    }

    @Throws(IOException::class)
    fun writeArrayHeader(length: Long) {
        ctx.write(ArrayHeaderRedisMessage(length))
    }

    /**
     * Writes a zero length list: "*0\r\n".
     */
    @Throws(IOException::class)
    fun writeEmptyList() {
        writeArrayHeader(0)
    }

    /**
     * Like WriteObjectList but every item is written as a bulkstring.
     */
    @Throws(IOException::class)
    fun writeObjectListStr(objectCollection: Collection<*>?) {
        if (objectCollection == null) {
            writeNullArray()
            return
        }
        writeArrayHeader(objectCollection.size.toLong())
        for (listItem in objectCollection) {
            writeObjectASBulkString(listItem)
        }
    }

    @Throws(IOException::class)
    fun writeObjectList(objectList: List<*>?) {
        if (objectList == null) {
            writeNullArray()
            return
        }
        writeArrayHeader(objectList.size.toLong())
        for (listItem in objectList) {
            writeObject(listItem)
        }
    }

    @Throws(IOException::class)
    fun writeBulkString(stringValue: String?) {
        if (stringValue == null) {
            ctx.write(FullBulkStringRedisMessage.NULL_INSTANCE)
            return
        }
        ctx.write(
            FullBulkStringRedisMessage(Unpooled.wrappedBuffer(stringValue.toByteArray()))
        )
    }

    @Throws(IOException::class)
    fun writeSimpleString(sString: String?) {
        val simpleStringRedisMessage = pool.getSimpleString(sString)
            ?: SimpleStringRedisMessage(sString)
        ctx.write(simpleStringRedisMessage)
    }

    @Throws(IOException::class)
    fun writeErrorString(eString: String?) {
        val errorRedisMessage = pool.getError(eString) ?: ErrorRedisMessage(eString)
        ctx.write(errorRedisMessage)
    }

    @Throws(IOException::class)
    fun writeOK() {
        writeSimpleString("OK")
    }

    @Throws(IOException::class)
    fun writeLongStr(longValue: Long) {
        val longBytes = pool.getByteBufOfInteger(longValue)
            ?: String.format(":%d\r\n", longValue).toByteArray()
        ctx.write(FullBulkStringRedisMessage(Unpooled.wrappedBuffer(longBytes)))
    }

    @Throws(IOException::class)
    fun writeFloat(floatValue: Double?) {
        if (floatValue == null) {
            ctx.write(FullBulkStringRedisMessage.NULL_INSTANCE)
            return
        }
        val intValue = floatValue.toInt()
        val floatString = if (intValue.toDouble() == floatValue) {
            intValue.toString()
        } else {
            floatValue.toString()
        }
        ctx.write(
            FullBulkStringRedisMessage(
                Unpooled.wrappedBuffer(floatString.toByteArray())
            )
        )
    }

    @Throws(IOException::class)
    fun writeByteArray(asByteArray: ByteArray?) {
        ctx.write(FullBulkStringRedisMessage(Unpooled.wrappedBuffer(asByteArray)))
    }

    @Throws(IOException::class)
    fun writeNullArray() {
        ctx.write(ArrayRedisMessage.NULL_INSTANCE)
    }

    @Throws(IOException::class)
    fun writeNullString() {
        ctx.write(FullBulkStringRedisMessage.NULL_INSTANCE)
    }

    @Throws(IOException::class)
    fun writeBytesValue(bytesValue: BytesValue) {
        ctx.write(
            FullBulkStringRedisMessage(
                Unpooled.wrappedBuffer(bytesValue.getObject() as ByteArray)
            )
        )
    }

    @Throws(IOException::class)
    fun writeBulkStringValue(inputString: StringValue?) {
        if (inputString == null) {
            ctx.write(FullBulkStringRedisMessage.NULL_INSTANCE)
            return
        }
        ctx.write(
            FullBulkStringRedisMessage(
                Unpooled.wrappedBuffer(
                    (inputString.getObject() as String).toByteArray()
                )
            )
        )
    }

    @Throws(IOException::class)
    fun writeLong(longValue: Long?) {
        if (longValue == null) {
            ctx.write(FullBulkStringRedisMessage.NULL_INSTANCE)
            return
        }
        val integerRedisMessage = pool.getInteger(longValue)
            ?: IntegerRedisMessage(longValue)
        ctx.write(integerRedisMessage)
    }

    @Throws(IOException::class)
    fun writeLong(longValue: Int?) {
        writeLong(longValue?.toLong())
    }

    @Throws(IOException::class)
    fun writeLongValue(longValue: LongValue) {
        val integerRedisMessage = pool.getInteger((longValue.getObject() as Long))
            ?: IntegerRedisMessage((longValue.getObject() as Long))
        ctx.write(integerRedisMessage)
    }

    fun flushCtx() {
        ctx.flush()
    }

    fun flushCtxTransactionAware() {
        if (ctx.channel().attr(AerospikeChannelInitializer.transactionAttrKey).get().inTransaction) {
            synchronized(ctx) { ctx.notify() }
        }
        ctx.flush()
    }

    fun closeCtx(e: Exception?) {
        BaseListener.log.error(e) { "${this.javaClass.simpleName} error" }
        ctx.close()
    }
}
