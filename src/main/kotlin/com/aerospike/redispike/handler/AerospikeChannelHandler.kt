package com.aerospike.redispike.handler

import com.aerospike.redispike.command.RequestCommand
import io.netty.channel.ChannelHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.handler.codec.CodecException
import io.netty.handler.codec.redis.*
import io.netty.util.CharsetUtil
import io.netty.util.ReferenceCountUtil
import mu.KotlinLogging
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@ChannelHandler.Sharable
class AerospikeChannelHandler @Inject constructor(
    private val nettyAerospikeHandler: NettyAerospikeHandler
) : ChannelInboundHandlerAdapter() {

    companion object {
        private val log = KotlinLogging.logger(this::class.java.name)
    }

    override fun channelRead(ctx: ChannelHandlerContext, msg: Any?) {
        val redisMessage = msg as RedisMessage?
        //printAggregatedRedisResponse(redisMessage!!)

        try {
            if (redisMessage is ArrayRedisMessage) {
                val arguments: MutableList<ByteArray> = ArrayList(redisMessage.children().size)
                for (child in redisMessage.children()) {
                    if (child is FullBulkStringRedisMessage) {
                        val bytes = ByteArray(child.content().readableBytes())
                        val readerIndex = child.content().readerIndex()
                        child.content().getBytes(readerIndex, bytes)
                        arguments.add(bytes)
                    }
                }

                val cmd = RequestCommand(arguments)
                nettyAerospikeHandler.handleCommand(cmd, ctx)
            } else if (redisMessage is InlineCommandRedisMessage) {
                val cmd = RequestCommand(
                    (redisMessage.content().split(" ")
                        .map { it.encodeToByteArray() }).toMutableList()
                )
                nettyAerospikeHandler.handleCommand(cmd, ctx)
            }
        } finally {
            ReferenceCountUtil.release(redisMessage)
        }
    }

    private fun printAggregatedRedisResponse(msg: RedisMessage) {
        when (msg) {
            is SimpleStringRedisMessage -> {
                log.debug { msg.content() }
            }
            is ErrorRedisMessage -> {
                log.debug { msg.content() }
            }
            is IntegerRedisMessage -> {
                log.debug { msg.value() }
            }
            is InlineCommandRedisMessage -> {
                log.debug { msg.content() }
            }
            is FullBulkStringRedisMessage -> {
                log.debug { getString(msg) }
            }
            is ArrayRedisMessage -> {
                for (child in msg.children()) {
                    printAggregatedRedisResponse(child)
                }
            }
            else -> {
                throw CodecException("unknown message type: $msg")
            }
        }
    }

    private fun getString(msg: FullBulkStringRedisMessage): String? {
        return if (msg.isNull) {
            "(null)"
        } else msg.content().toString(CharsetUtil.UTF_8)
    }
}
