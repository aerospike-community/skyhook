package com.aerospike.skyhook.handler.aerospike

import com.aerospike.skyhook.command.RequestCommand
import com.aerospike.skyhook.config.AerospikeContext
import com.aerospike.skyhook.handler.CommandHandler
import com.aerospike.skyhook.handler.NettyResponseWriter
import com.aerospike.skyhook.listener.BaseListener
import com.aerospike.skyhook.util.InfoUtils.getNamespaceInfo
import com.aerospike.skyhook.util.InfoUtils.getSetInfo
import io.netty.channel.ChannelHandlerContext
import kotlin.math.floor

class DbsizeCommandHandler(
    private val aeroCtx: AerospikeContext,
    private val ctx: ChannelHandlerContext
) : NettyResponseWriter(), CommandHandler {

    override fun handle(cmd: RequestCommand) {
        require(cmd.argCount == 1) { BaseListener.argValidationErrorMsg(cmd) }

        writeLong(ctx, getTableRecordsNumber(aeroCtx.namespace, aeroCtx.set))
        ctx.flush()
    }

    private fun getTableRecordsNumber(ns: String, set: String?): Long {
        val allRecords = aeroCtx.client.nodes
            .map { getSetInfo(ns, set, it) }
            .map { it["objects"]!!.toInt() }
            .sum()
        val replicationFactor = getNamespaceInfo(ns, aeroCtx.client.nodes[0])["effective_replication_factor"]!!.toInt()
        return floor(allRecords.toDouble() / replicationFactor).toLong()
    }
}
