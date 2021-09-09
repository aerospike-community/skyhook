package com.aerospike.skyhook.listener.scan

import com.aerospike.skyhook.command.RequestCommand
import com.aerospike.skyhook.listener.ValueType
import com.aerospike.skyhook.util.RegexUtils

class ScanCommand(val cmd: RequestCommand, flagIndex: Int) {
    var MATCH: String? = null
    var COUNT: Long = defaultCount
    var TYPE: ValueType? = null

    val cursor = String(cmd.args[flagIndex - 1])

    init {
        for (i in flagIndex until cmd.args.size step 2) {
            setFlag(i)
        }
    }

    private fun setFlag(i: Int) {
        val flagStr = String(cmd.args[i])
        when (flagStr.toUpperCase()) {
            "MATCH" -> MATCH = RegexUtils.format(String(cmd.args[i + 1]))
            "COUNT" -> COUNT = String(cmd.args[i + 1]).toLong()
            "TYPE" -> TYPE = ValueType.valueOf(cmd.args[i + 1])
            else -> throw IllegalArgumentException(flagStr)
        }
    }

    companion object {
        const val zeroCursor = "0"
        const val defaultCount = 10L
    }
}
