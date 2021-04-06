package com.aerospike.skyhook.handler

import com.aerospike.client.Value
import com.aerospike.client.Value.*
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.redis.*
import java.io.IOException

open class NettyResponseWriter {

    companion object {
        private val pool: RedisMessagePool = FixedRedisMessagePool.INSTANCE
    }

    @Throws(IOException::class)
    fun writeObject(ctx: ChannelHandlerContext, value: Any?) {
        when (value) {
            null -> {
                writeNullString(ctx)
            }
            is List<*> -> {
                writeObjectList(ctx, value as List<*>?)
            }
            is ByteArray -> {
                writeByteArray(ctx, value as ByteArray?)
            }
            is String -> {
                writeBulkString(ctx, value as String?)
            }
            is Long -> {
                writeLong(ctx, value as Long?)
            }
            is Int -> {
                writeLong(ctx, value as Int?)
            }
            is Double -> {
                writeFloat(ctx, value as Double?)
            }
            else -> {
                throw IllegalArgumentException("Unsupported value type")
            }
        }
    }

    @Throws(IOException::class)
    fun writeObjectASBulkString(ctx: ChannelHandlerContext, value: Any?) {
        when (value) {
            null -> {
                writeNullString(ctx)
            }
            is ByteArray -> {
                writeByteArray(ctx, value as ByteArray?)
            }
            is String -> {
                writeBulkString(ctx, value as String?)
            }
            is Long, is Int -> {
                writeBulkString(ctx, value.toString())
            }
            is Double -> {
                writeBulkString(ctx, value.toString())
            }
            is Value -> {
                writeObjectASBulkString(ctx, value.getObject())
            }
            else -> {
                writeNullString(ctx)
            }
        }
    }

    @Throws(IOException::class)
    fun writeArrayHeader(ctx: ChannelHandlerContext, length: Long) {
        ctx.write(ArrayHeaderRedisMessage(length))
    }

    /**
     * Writes a zero length list: "*0\r\n".
     */
    @Throws(IOException::class)
    fun writeEmptyList(ctx: ChannelHandlerContext) {
        writeArrayHeader(ctx, 0)
    }

    /**
     * Like WriteObjectList but every item is written as a bulkstring.
     */
    @Throws(IOException::class)
    fun writeObjectListStr(ctx: ChannelHandlerContext, objectCollection: Collection<*>?) {
        if (objectCollection == null) {
            writeNullArray(ctx)
            return
        }
        writeArrayHeader(ctx, objectCollection.size.toLong())
        for (listItem in objectCollection) {
            writeObjectASBulkString(ctx, listItem)
        }
    }

    @Throws(IOException::class)
    fun writeObjectList(ctx: ChannelHandlerContext, objectList: List<*>?) {
        if (objectList == null) {
            writeNullArray(ctx)
            return
        }
        writeArrayHeader(ctx, objectList.size.toLong())
        for (listItem in objectList) {
            writeObject(ctx, listItem)
        }
    }

    @Throws(IOException::class)
    fun writeBulkString(ctx: ChannelHandlerContext, stringValue: String?) {
        if (stringValue == null) {
            ctx.write(FullBulkStringRedisMessage.NULL_INSTANCE)
            return
        }
        ctx.write(
            FullBulkStringRedisMessage(Unpooled.wrappedBuffer(stringValue.toByteArray()))
        )
    }

    @Throws(IOException::class)
    fun writeSimpleString(ctx: ChannelHandlerContext, sString: String?) {
        val simpleStringRedisMessage = pool.getSimpleString(sString)
            ?: SimpleStringRedisMessage(sString)
        ctx.write(simpleStringRedisMessage)
    }

    @Throws(IOException::class)
    fun writeErrorString(ctx: ChannelHandlerContext, eString: String?) {
        val errorRedisMessage = pool.getError(eString) ?: ErrorRedisMessage(eString)
        ctx.write(errorRedisMessage)
    }

    @Throws(IOException::class)
    fun writeOK(ctx: ChannelHandlerContext) {
        writeSimpleString(ctx, "OK")
    }

    @Throws(IOException::class)
    fun writeLongStr(ctx: ChannelHandlerContext, longValue: Long) {
        val longBytes = pool.getByteBufOfInteger(longValue)
            ?: String.format(":%d\r\n", longValue).toByteArray()
        ctx.write(FullBulkStringRedisMessage(Unpooled.wrappedBuffer(longBytes)))
    }

    @Throws(IOException::class)
    fun writeFloat(ctx: ChannelHandlerContext, floatValue: Double?) {
        val floatString = String.format("%f", floatValue)
        ctx.write(
            FullBulkStringRedisMessage(
                Unpooled.wrappedBuffer(floatString.toByteArray())
            )
        )
    }

    @Throws(IOException::class)
    fun writeByteArray(ctx: ChannelHandlerContext, asByteArray: ByteArray?) {
        ctx.write(FullBulkStringRedisMessage(Unpooled.wrappedBuffer(asByteArray)))
    }

    @Throws(IOException::class)
    fun writeNullArray(ctx: ChannelHandlerContext) {
        ctx.write(ArrayRedisMessage.NULL_INSTANCE)
    }

    @Throws(IOException::class)
    fun writeNullString(ctx: ChannelHandlerContext) {
        ctx.write(FullBulkStringRedisMessage.NULL_INSTANCE)
    }

    @Throws(IOException::class)
    fun writeBytesValue(ctx: ChannelHandlerContext, bytesValue: BytesValue) {
        ctx.write(
            FullBulkStringRedisMessage(
                Unpooled.wrappedBuffer(bytesValue.getObject() as ByteArray)
            )
        )
    }

    @Throws(IOException::class)
    fun writeBulkStringValue(ctx: ChannelHandlerContext, inputString: StringValue?) {
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
    fun writeLong(ctx: ChannelHandlerContext, longValue: Long?) {
        if (longValue == null) {
            ctx.write(FullBulkStringRedisMessage.NULL_INSTANCE)
            return
        }
        val integerRedisMessage = pool.getInteger(longValue)
            ?: IntegerRedisMessage(longValue)
        ctx.write(integerRedisMessage)
    }

    @Throws(IOException::class)
    fun writeLong(ctx: ChannelHandlerContext, longValue: Int?) {
        writeLong(ctx, longValue?.toLong())
    }

    @Throws(IOException::class)
    fun writeLongValue(ctx: ChannelHandlerContext, longValue: LongValue) {
        val integerRedisMessage = pool.getInteger((longValue.getObject() as Long))
            ?: IntegerRedisMessage((longValue.getObject() as Long))
        ctx.write(integerRedisMessage)
    }

    fun flush(ctx: ChannelHandlerContext) {
        ctx.flush()
    }
}
