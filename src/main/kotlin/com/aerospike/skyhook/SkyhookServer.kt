package com.aerospike.skyhook

import com.aerospike.skyhook.config.ServerConfiguration
import com.aerospike.skyhook.pipeline.AerospikeChannelInitializer
import com.aerospike.skyhook.util.SystemUtils
import com.google.inject.name.Named
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelOption
import io.netty.channel.EventLoopGroup
import io.netty.channel.epoll.EpollServerDomainSocketChannel
import io.netty.channel.kqueue.KQueueServerDomainSocketChannel
import io.netty.channel.socket.ServerSocketChannel
import io.netty.channel.unix.DomainSocketAddress
import mu.KotlinLogging
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SkyhookServer @Inject constructor(

    /**
     * Connect server configuration.
     */
    private val config: ServerConfiguration,

    /**
     * Initialize channels.
     */
    private val channelInitializer: AerospikeChannelInitializer,

    /**
     * The event loop group to handle connection requests.
     */
    @Named(NETTY_BOSS_GROUP)
    private val bossGroup: EventLoopGroup,

    /**
     * The event loop group to read and parse incoming requests and write
     * responses.
     */
    @Named(NETTY_WORKER_GROUP)
    private val workerGroup: EventLoopGroup,

    /**
     * The ServerSocketChannel.
     */
    private val socketChannel: ServerSocketChannel
) : Server {

    companion object {
        /**
         * Annotation to get hold of the event loop boss group to handle
         * connection requests.
         */
        const val NETTY_BOSS_GROUP =
            "com.aerospike.skyhook.SkyhookServer." +
                    "NETTY_BOSS_GROUP"

        /**
         * Annotation to get hold of the event loop worker group to read, parse
         * incoming requests.
         */
        const val NETTY_WORKER_GROUP =
            "com.aerospike.skyhook.SkyhookServer." +
                    "NETTY_WORKER_GROUP"

        private val log = KotlinLogging.logger(this::class.java.name)
    }

    /**
     * Is the server started.
     */
    private var started: Boolean = false

    override fun start() {
        if (started) {
            return
        }

        log.info { "Starting the Server..." }

        if (config.unixSocket == null) {
            bindInetSocket()
        } else {
            bindUnixSocket()
        }

        log.info { "Started Netty server with config $config" }
        started = true
    }

    override fun stop() {
        if (!started) {
            return
        }

        log.info("Shutting down Netty event loops...")

        bossGroup.shutdownGracefully().sync()
        workerGroup.shutdownGracefully().sync()
        started = false
    }

    private fun bindInetSocket() {
        val server = initServerBootstrap()

        server.channel(socketChannel.javaClass)
            .childOption(ChannelOption.SO_KEEPALIVE, true)
            .childOption(ChannelOption.TCP_NODELAY, true)
            .bind(config.redisPort).sync()
    }

    private fun bindUnixSocket() {
        val server = initServerBootstrap()

        val channel = when (SystemUtils.os) {
            SystemUtils.OS.LINUX -> EpollServerDomainSocketChannel()
            SystemUtils.OS.MAC -> KQueueServerDomainSocketChannel()
            else -> throw IllegalArgumentException("Unsupported UNIX Socket")
        }

        server.channel(channel.javaClass)
            .bind(DomainSocketAddress(config.unixSocket)).sync()
    }

    private fun initServerBootstrap(): ServerBootstrap {
        val server = ServerBootstrap()

        server.group(bossGroup, workerGroup)
            .childHandler(channelInitializer)
            .option(ChannelOption.SO_BACKLOG, 128)
        return server
    }

}
