package com.aerospike.redispike.pipeline

import com.aerospike.redispike.handler.AerospikeChannelHandler
import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel
import io.netty.handler.codec.redis.RedisArrayAggregator
import io.netty.handler.codec.redis.RedisBulkStringAggregator
import io.netty.handler.codec.redis.RedisDecoder
import io.netty.handler.codec.redis.RedisEncoder
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Initialize channels on the socket.
 */
@Singleton
class AerospikeChannelInitializer @Inject constructor(
    private val aerospikeChannelHandler: AerospikeChannelHandler
) : ChannelInitializer<SocketChannel>() {

    override fun initChannel(ch: SocketChannel) {
        ch.pipeline().addLast(
            RedisDecoder(true), RedisBulkStringAggregator(), RedisArrayAggregator(),
            RedisEncoder(), aerospikeChannelHandler
        )
    }
}
