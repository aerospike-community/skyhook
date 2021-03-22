package com.aerospike.redispike.util

import com.aerospike.client.Value

object Typed {

    fun getValue(wireVal: ByteArray): Value {
        try {
            return Value.LongValue(String(wireVal).toLong())
        } catch (e: NumberFormatException) {
        }
        try {
            return Value.DoubleValue(String(wireVal).toDouble())
        } catch (e: NumberFormatException) {
        }
        return Value.StringValue(wireVal.toString(Charsets.UTF_8))
    }

    fun getInteger(wireVal: ByteArray?): Int {
        return String(wireVal!!).toInt()
    }

    fun getLong(wireVal: ByteArray?): Long {
        return String(wireVal!!).toLong()
    }

    fun getDouble(wireVal: ByteArray?): Double {
        return String(wireVal!!).toDouble()
    }
}
