package com.aerospike.skyhook.pipeline

import com.aerospike.skyhook.config.AerospikeContext
import com.aerospike.skyhook.config.ServerConfiguration
import com.aerospike.skyhook.handler.AerospikeChannelHandler
import com.aerospike.skyhook.util.client.AerospikeClientPool
import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel
import io.netty.handler.codec.redis.RedisArrayAggregator
import io.netty.handler.codec.redis.RedisBulkStringAggregator
import io.netty.handler.codec.redis.RedisDecoder
import io.netty.handler.codec.redis.RedisEncoder
import io.netty.util.AttributeKey
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Initialize channels on the socket.
 */
@Singleton
class AerospikeChannelInitializer @Inject constructor(
    private val config: ServerConfiguration,
    private val clientPool: AerospikeClientPool,
    private val aerospikeChannelHandler: AerospikeChannelHandler
) : ChannelInitializer<SocketChannel>() {

    companion object {
        val authDetailsAttrKey: AttributeKey<String> = AttributeKey.valueOf("authDetails")
        val aeroCtxAttrKey: AttributeKey<AerospikeContext> = AttributeKey.valueOf("aeroCtx")
        val clientPoolAttrKey: AttributeKey<AerospikeClientPool> = AttributeKey.valueOf("clientPool")
    }

    override fun initChannel(ch: SocketChannel) {
        ch.pipeline().addLast(
            RedisDecoder(true), RedisBulkStringAggregator(), RedisArrayAggregator(),
            RedisEncoder(), aerospikeChannelHandler
        )

        ch.attr(aeroCtxAttrKey).set(
            AerospikeContext(
                config.namespase,
                config.set,
                config.bin,
                config.typeBin
            )
        )

        ch.attr(clientPoolAttrKey).set(clientPool)
    }
}
