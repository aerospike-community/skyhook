package com.aerospike.redispike.handler

import com.aerospike.client.Value
import com.aerospike.client.Value.*
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.redis.*
import java.io.IOException

open class NettyResponseWriter {

    private val pool: RedisMessagePool = FixedRedisMessagePool.INSTANCE

    @Throws(IOException::class)
    fun writeArray(ctx: ChannelHandlerContext, valueArray: ValueArray) {
        val valueAry = valueArray.getObject() as Array<*>
        writeArrayHeader(ctx, valueAry.size.toLong())
    }

    fun writeList(ctx: ChannelHandlerContext?, valueArray: ListValue?) {
        throw NotImplementedError()
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
            is Double -> {
                writeFloat(ctx, value as Double?)
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
            is Long -> {
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

    /*
	 * Writes a zero length list: "*0\r\n"
	 */
    @Throws(IOException::class)
    fun writeEmptyList(ctx: ChannelHandlerContext) {
        writeArrayHeader(ctx, 0)
    }

    /*
	 * Like WriteObjectList but every item is written as a bulkstring
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
        var simpleStringRedisMessage = pool.getSimpleString(sString)
        if (simpleStringRedisMessage == null) {
            simpleStringRedisMessage = SimpleStringRedisMessage(sString)
        }
        ctx.write(simpleStringRedisMessage)
    }

    @Throws(IOException::class)
    fun writeErrorString(ctx: ChannelHandlerContext, eString: String?) {
        var errorRedisMessage = pool.getError(eString)
        if (errorRedisMessage == null) {
            errorRedisMessage = ErrorRedisMessage(eString)
        }
        ctx.write(errorRedisMessage)
    }

    @Throws(IOException::class)
    fun writeOK(ctx: ChannelHandlerContext) {
        writeSimpleString(ctx, "OK")
    }

    fun encodeError(ctx: ChannelHandlerContext?, errorMessage: String?) {
        throw NotImplementedError()
    }

    fun encodeMap(ctx: ChannelHandlerContext?, mapValue: MapValue?) {
        throw NotImplementedError()
    }

    @Throws(IOException::class)
    fun writeLongStr(ctx: ChannelHandlerContext, longValue: Long) {
        var longBytes = pool.getByteBufOfInteger(longValue)
        if (longBytes == null) {
            val longString = String.format(":%d\r\n", longValue)
            longBytes = longString.toByteArray()
        }
        ctx.write(FullBulkStringRedisMessage(Unpooled.wrappedBuffer(longBytes)))
    }

    @Throws(IOException::class)
    fun writeFloat(ctx: ChannelHandlerContext, floatValue: Double?) {
        val floatString = String.format("%f", floatValue)
        ctx.write(FullBulkStringRedisMessage(Unpooled.wrappedBuffer(floatString.toByteArray())))
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
            FullBulkStringRedisMessage(Unpooled.wrappedBuffer(bytesValue.getObject() as ByteArray))
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
        var integerRedisMessage = pool.getInteger(longValue!!)
        if (integerRedisMessage == null) {
            integerRedisMessage = IntegerRedisMessage(longValue)
        }
        ctx.write(integerRedisMessage)
    }

    @Throws(IOException::class)
    fun writeLong(ctx: ChannelHandlerContext, longValue: Int) {
        writeLong(ctx, longValue.toLong())
    }

    @Throws(IOException::class)
    fun writeLongValue(ctx: ChannelHandlerContext, longValue: LongValue) {
        var integerRedisMessage = pool.getInteger((longValue.getObject() as Long))
        if (integerRedisMessage == null) {
            integerRedisMessage = IntegerRedisMessage((longValue.getObject() as Long))
        }
        ctx.write(integerRedisMessage)
    }

    fun flush(ctx: ChannelHandlerContext) {
        ctx.flush()
    }
}
