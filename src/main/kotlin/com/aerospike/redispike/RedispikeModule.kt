package com.aerospike.redispike

import com.aerospike.client.AerospikeClient
import com.aerospike.client.Host
import com.aerospike.client.IAerospikeClient
import com.aerospike.client.async.NioEventLoops
import com.aerospike.client.policy.ClientPolicy
import com.aerospike.redispike.config.ServerConfiguration
import com.aerospike.redispike.util.SystemUtils
import com.google.inject.AbstractModule
import com.google.inject.Provides
import com.google.inject.name.Names
import io.netty.channel.EventLoopGroup
import io.netty.channel.epoll.EpollEventLoopGroup
import io.netty.channel.epoll.EpollServerSocketChannel
import io.netty.channel.kqueue.KQueueEventLoopGroup
import io.netty.channel.kqueue.KQueueServerSocketChannel
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.ServerSocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import mu.KotlinLogging
import javax.inject.Inject
import javax.inject.Singleton

class RedispikeModule(
    private val config: ServerConfiguration
) : AbstractModule() {

    companion object {
        private val log = KotlinLogging.logger(this::class.java.name)
    }

    override fun configure() {
        bindEventLoops()
    }

    private fun bindEventLoops() {
        when (SystemUtils.os) {
            SystemUtils.OS.LINUX -> bindEpollEventLoops()
            SystemUtils.OS.MAC -> bindKQueueEventLoops()
            else -> bindNioEventLoops()
        }
    }

    private fun bindNioEventLoops() {
        log.info { "bindNioEventLoops" }
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

    private fun bindEpollEventLoops() {
        log.info { "bindEpollEventLoops" }
        bind(EventLoopGroup::class.java).annotatedWith(
            Names.named(RedispikeServer.NETTY_BOSS_GROUP)
        ).toInstance(EpollEventLoopGroup(config.bossThreads))

        bind(EventLoopGroup::class.java).annotatedWith(
            Names.named(RedispikeServer.NETTY_WORKER_GROUP)
        ).toInstance(EpollEventLoopGroup(config.workerThreads))

        bind(ServerSocketChannel::class.java).toInstance(
            EpollServerSocketChannel()
        )
    }

    private fun bindKQueueEventLoops() {
        log.info { "bindKQueueEventLoops" }
        bind(EventLoopGroup::class.java).annotatedWith(
            Names.named(RedispikeServer.NETTY_BOSS_GROUP)
        ).toInstance(KQueueEventLoopGroup(config.bossThreads))

        bind(EventLoopGroup::class.java).annotatedWith(
            Names.named(RedispikeServer.NETTY_WORKER_GROUP)
        ).toInstance(KQueueEventLoopGroup(config.workerThreads))

        bind(ServerSocketChannel::class.java).toInstance(
            KQueueServerSocketChannel()
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
