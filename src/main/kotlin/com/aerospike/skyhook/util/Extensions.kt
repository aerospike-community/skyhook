package com.aerospike.skyhook.util

fun <T> List<T>.toPair(): Pair<T, T> {
    require(this.size == 2) { "List is not of length 2" }
    val (a, b) = this
    return Pair(a, b)
}
