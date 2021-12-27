package com.aerospike.skyhook.handler.aerospike

import com.aerospike.skyhook.command.RequestCommand
import com.aerospike.skyhook.handler.CommandHandler
import com.aerospike.skyhook.listener.BaseListener
import com.aerospike.skyhook.util.InfoUtils.getNamespaceInfo
import com.aerospike.skyhook.util.InfoUtils.getSetInfo
import io.netty.channel.ChannelHandlerContext
import kotlin.math.floor

class DbsizeCommandHandler(
    ctx: ChannelHandlerContext
) : BaseListener(ctx), CommandHandler {

    override fun handle(cmd: RequestCommand) {
        require(cmd.argCount == 1) { argValidationErrorMsg(cmd) }

        writeLong(getTableRecordsNumber(aeroCtx.namespace, aeroCtx.set))
        flushCtxTransactionAware()
    }

    private fun getTableRecordsNumber(ns: String, set: String?): Long {
        val allRecords = client.nodes
            .map { getSetInfo(ns, set, it) }
            .sumOf { it["objects"]!!.toInt() }
        val replicationFactor = getNamespaceInfo(ns, client.nodes[0])["effective_replication_factor"]!!.toInt()
        return floor(allRecords.toDouble() / replicationFactor).toLong()
    }
}
