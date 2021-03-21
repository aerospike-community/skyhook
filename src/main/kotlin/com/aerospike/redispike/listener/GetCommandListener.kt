package com.aerospike.redispike.listener

import com.aerospike.client.Key
import com.aerospike.redispike.command.RequestCommand
import com.aerospike.redispike.config.AerospikeContext
import io.netty.channel.ChannelHandlerContext
import mu.KotlinLogging

class GetCommandListener(
    private val aeroCtx: AerospikeContext,
    ctx: ChannelHandlerContext
) : BaseListener(aeroCtx, ctx) {

    companion object {
        private val log = KotlinLogging.logger(this::class.java.name)
    }

    override fun handle(cmd: RequestCommand) {
        require(cmd.argCount == 2)

        val key = Key(aeroCtx.namespace, aeroCtx.set, cmd.args!![1])
        aeroCtx.client.get(null, this, null, key)
    }
}
