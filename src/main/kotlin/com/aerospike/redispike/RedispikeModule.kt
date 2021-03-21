package com.aerospike.redispike

import com.aerospike.client.AerospikeClient
import com.aerospike.client.Host
import com.aerospike.client.IAerospikeClient
import com.aerospike.client.async.NioEventLoops
import com.aerospike.client.policy.ClientPolicy
import com.aerospike.redispike.config.ServerConfiguration
import com.google.inject.AbstractModule
import com.google.inject.Provides
import com.google.inject.name.Names
import io.netty.channel.EventLoopGroup
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.ServerSocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import javax.inject.Inject
import javax.inject.Singleton

class RedispikeModule(
    private val config: ServerConfiguration
) : AbstractModule() {

    override fun configure() {
        bindServerEventLoops()
    }

    private fun bindServerEventLoops() {
        bind(EventLoopGroup::class.java).annotatedWith(
            Names.named(RedispikeServer.NETTY_BOSS_GROUP)
        ).toInstance(NioEventLoopGroup(config.bossThreads))

        bind(EventLoopGroup::class.java).annotatedWith(
            Names.named(RedispikeServer.NETTY_WORKER_GROUP)
        ).toInstance(NioEventLoopGroup(config.workerThreads))

        bind(ServerSocketChannel::class.java).toInstance(
            NioServerSocketChannel()
        )
    }

    @Provides
    @Singleton
    @Inject
    fun serverConfiguration(): ServerConfiguration {
        return config
    }

    @Provides
    @Singleton
    @Inject
    fun aerospikeClient(): IAerospikeClient {
        val clientPolicy = ClientPolicy()
        clientPolicy.eventLoops = NioEventLoops(config.workerThreads)

        return AerospikeClient(
            clientPolicy, *Host.parseHosts(
                config.hostList,
                3000
            )
        )
    }
}
