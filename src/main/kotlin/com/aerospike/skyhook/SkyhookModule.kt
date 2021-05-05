package com.aerospike.skyhook

import com.aerospike.client.async.EventLoops
import com.aerospike.client.async.NettyEventLoops
import com.aerospike.client.async.NioEventLoops
import com.aerospike.client.policy.ClientPolicy
import com.aerospike.skyhook.config.ServerConfiguration
import com.aerospike.skyhook.util.SystemUtils
import com.aerospike.skyhook.util.client.AerospikeClientPool
import com.aerospike.skyhook.util.client.AerospikeClientPoolImpl
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
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Inject
import javax.inject.Singleton

class SkyhookModule(
    private val config: ServerConfiguration
) : AbstractModule() {

    companion object {
        private val log = KotlinLogging.logger(this::class.java.name)
    }

    override fun configure() {
        bind(AerospikeClientPool::class.java).to(AerospikeClientPoolImpl::class.java)
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
            Names.named(SkyhookServer.NETTY_BOSS_GROUP)
        ).toInstance(NioEventLoopGroup(config.bossThreads))

        bind(EventLoopGroup::class.java).annotatedWith(
            Names.named(SkyhookServer.NETTY_WORKER_GROUP)
        ).toInstance(NioEventLoopGroup(config.workerThreads))

        bind(ServerSocketChannel::class.java).toInstance(
            NioServerSocketChannel()
        )
    }

    private fun bindEpollEventLoops() {
        log.info { "bindEpollEventLoops" }
        bind(EventLoopGroup::class.java).annotatedWith(
            Names.named(SkyhookServer.NETTY_BOSS_GROUP)
        ).toInstance(EpollEventLoopGroup(config.bossThreads))

        bind(EventLoopGroup::class.java).annotatedWith(
            Names.named(SkyhookServer.NETTY_WORKER_GROUP)
        ).toInstance(EpollEventLoopGroup(config.workerThreads))

        bind(ServerSocketChannel::class.java).toInstance(
            EpollServerSocketChannel()
        )
    }

    private fun bindKQueueEventLoops() {
        log.info { "bindKQueueEventLoops" }
        bind(EventLoopGroup::class.java).annotatedWith(
            Names.named(SkyhookServer.NETTY_BOSS_GROUP)
        ).toInstance(KQueueEventLoopGroup(config.bossThreads))

        bind(EventLoopGroup::class.java).annotatedWith(
            Names.named(SkyhookServer.NETTY_WORKER_GROUP)
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
    fun clientPolicy(): ClientPolicy {
        val clientPolicy = ClientPolicy()
        clientPolicy.eventLoops = getClientEventLoops()
        config.clientPolicy.user?.let { clientPolicy.user = it }
        config.clientPolicy.password?.let { clientPolicy.password = it }
        config.clientPolicy.authMode?.let { clientPolicy.authMode = it }
        config.clientPolicy.timeout?.let { clientPolicy.timeout = it }
        return clientPolicy
    }

    private fun getClientEventLoops(): EventLoops {
        return when (SystemUtils.os) {
            SystemUtils.OS.LINUX -> NettyEventLoops(EpollEventLoopGroup(config.workerThreads))
            SystemUtils.OS.MAC -> NettyEventLoops(KQueueEventLoopGroup(config.workerThreads))
            else -> NioEventLoops(config.workerThreads)
        }
    }

    @Provides
    @Singleton
    @Inject
    fun executorService(): ExecutorService {
        return Executors.newFixedThreadPool(config.workerThreads)
    }
}
