package com.aerospike.skyhook.util

import com.aerospike.client.Value
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets

object Typed {

    @Suppress("kotlin:S108")
    fun getValue(wireVal: ByteArray): Value {
        try {
            return Value.DoubleValue(String(wireVal).toDouble())
        } catch (ignore: NumberFormatException) {
        }
        try {
            StandardCharsets.UTF_8.newDecoder().decode(ByteBuffer.wrap(wireVal))
        } catch (ex: CharacterCodingException) {
            return Value.BytesValue(wireVal)
        }
        return Value.StringValue(wireVal.toString(Charsets.UTF_8))
    }

    fun getStringValue(wireVal: ByteArray): Value {
        return Value.StringValue(wireVal.toString(Charsets.UTF_8))
    }

    fun getInteger(wireVal: ByteArray): Int {
        return String(wireVal).toInt()
    }

    fun getLong(wireVal: ByteArray): Long {
        return String(wireVal).toLong()
    }

    fun getDouble(wireVal: ByteArray): Double {
        return String(wireVal).toDouble()
    }
}
