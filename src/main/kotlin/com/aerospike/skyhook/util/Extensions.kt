package com.aerospike.skyhook.util

import io.netty.channel.ChannelHandlerContext

fun <T> List<T>.toPair(): Pair<T, T> {
    require(this.size == 2) { "List is not of length 2" }
    val (a, b) = this
    return Pair(a, b)
}

@Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
fun ChannelHandlerContext.wait() = (this as Object).wait()

@Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
fun ChannelHandlerContext.wait(timeout: Long) = (this as Object).wait(timeout)

@Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
fun ChannelHandlerContext.notify() = (this as Object).notify()
