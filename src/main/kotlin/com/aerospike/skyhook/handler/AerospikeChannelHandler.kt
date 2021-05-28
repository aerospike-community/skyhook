package com.aerospike.skyhook.handler

import com.aerospike.client.AerospikeException
import com.aerospike.client.ResultCode
import com.aerospike.skyhook.command.RequestCommand
import com.aerospike.skyhook.pipeline.AerospikeChannelInitializer.Companion.transactionAttrKey
import io.netty.channel.ChannelHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.handler.codec.CodecException
import io.netty.handler.codec.redis.*
import io.netty.util.CharsetUtil
import io.netty.util.ReferenceCountUtil
import mu.KotlinLogging
import java.util.*
import javax.inject.Singleton

@Singleton
@ChannelHandler.Sharable
class AerospikeChannelHandler() : ChannelInboundHandlerAdapter() {

    companion object {
        private val log = KotlinLogging.logger(this::class.java.name)
    }

    override fun channelRead(ctx: ChannelHandlerContext, msg: Any?) {
        try {
            when (msg) {
                is ArrayRedisMessage -> {
                    val arguments: MutableList<ByteArray> = ArrayList(msg.children().size)
                    for (child in msg.children()) {
                        if (child is FullBulkStringRedisMessage) {
                            val bytes = ByteArray(child.content().readableBytes())
                            val readerIndex = child.content().readerIndex()
                            child.content().getBytes(readerIndex, bytes)
                            arguments.add(bytes)
                        }
                    }

                    val cmd = RequestCommand(arguments)
                    handleCommand(cmd, ctx)
                }

                is AbstractStringRedisMessage -> {
                    val cmd = RequestCommand(
                        (msg.content().split(" ")
                            .map { it.encodeToByteArray() }).toList()
                    )
                    handleCommand(cmd, ctx)
                }

                else -> log.warn { "Unsupported message type ${msg?.javaClass?.simpleName}" }
            }
        } catch (e: UnsupportedOperationException) {
            ctx.write(ErrorRedisMessage(e.message))
            ctx.flush()
        } finally {
            ReferenceCountUtil.release(msg)
        }
    }

    /**
     * Handle the input command. Listeners are responsible to send the response
     * to the client.
     */
    private fun handleCommand(cmd: RequestCommand, ctx: ChannelHandlerContext) {
        try {
            val state = ctx.channel().attr(transactionAttrKey).get()
            if (state.inTransaction && !cmd.transactional) {
                state.commands.addLast(cmd)
                ctx.write(SimpleStringRedisMessage("QUEUED"))
                ctx.flush()
            } else {
                cmd.command.newHandler(ctx).handle(cmd)
            }
        } catch (e: Exception) {
            val msg = when (e) {
                is AerospikeException -> {
                    if (e.resultCode == ResultCode.FILTERED_OUT)
                        "Transaction error"
                    else
                        "Internal error"
                }
                else -> e.message
            }
            log.warn(e) {}
            ctx.write(ErrorRedisMessage(msg))
            ctx.flush()
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
