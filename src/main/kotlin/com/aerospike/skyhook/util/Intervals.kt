package com.aerospike.skyhook.util

import java.util.*

object Intervals {

    private const val infHighest = "+inf"
    private const val infLowest = "-inf"

    private const val lexHighest = "+"
    private const val lexLowest = "-"

    private const val scoreExclusive = "("
    private const val lexExclusive = "["

    fun fromScore(interval: String): Int {
        return score(interval, 0, 1)
    }

    fun upScore(interval: String): Int {
        return score(interval, 1, 0)
    }

    private fun score(interval: String, includeShift: Int, excludeShift: Int): Int {
        return when (interval.lowercase(Locale.ENGLISH)) {
            infHighest -> Int.MAX_VALUE
            infLowest -> Int.MIN_VALUE
            else -> {
                return if (interval.startsWith(scoreExclusive)) {
                    interval.drop(1).toInt() + excludeShift
                } else {
                    interval.toInt() + includeShift
                }
            }
        }
    }

    fun fromLex(interval: String): String {
        return lex(interval, true)
    }

    fun upLex(interval: String): String {
        return lex(interval, false)
    }

    private fun lex(interval: String, from: Boolean): String {
        return when (interval.lowercase(Locale.ENGLISH)) {
            lexHighest -> String(byteArrayOf(127))
            lexLowest -> String(byteArrayOf(0))
            else -> {
                return if (interval.startsWith(lexExclusive)) {
                    if (from) {
                        fixLexExclusive(interval.drop(1))
                    } else {
                        interval.drop(1)
                    }
                } else {
                    if (from) {
                        interval
                    } else {
                        fixLexExclusive(interval)
                    }
                }
            }
        }
    }

    private fun fixLexExclusive(str: String): String {
        val byteArray = str.toByteArray()
        byteArray[byteArray.size - 1]++
        return String(byteArray)
    }
}
