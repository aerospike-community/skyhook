package com.aerospike.skyhook.util

object RegexUtils {

    fun format(regex: String): String {
        return regex.replace("*", ".*")
            .replace("?", ".")
    }
}
