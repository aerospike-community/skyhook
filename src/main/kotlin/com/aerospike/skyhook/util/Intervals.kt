package com.aerospike.skyhook.util

object Intervals {

    private const val infHighest = "+inf"
    private const val infLowest = "-inf"

    private const val exclusive = "("


    fun fromScore(interval: String): Int {
        return score(interval, 0, 1)
    }

    fun upScore(interval: String): Int {
        return score(interval, 1, 0)
    }

    private fun score(interval: String, includeShift: Int, excludeShift: Int): Int {
        return when (interval.toLowerCase()) {
            infHighest -> Int.MAX_VALUE
            infLowest -> Int.MIN_VALUE
            else -> {
                return if (interval.startsWith(exclusive)) {
                    interval.drop(1).toInt() + excludeShift
                } else {
                    interval.toInt() + includeShift
                }
            }
        }
    }
}
