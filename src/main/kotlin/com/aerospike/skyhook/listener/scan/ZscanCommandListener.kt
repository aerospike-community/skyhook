package com.aerospike.skyhook.listener.scan

import com.aerospike.client.Operation
import com.aerospike.client.cdt.MapOperation
import com.aerospike.client.cdt.MapReturnType
import com.aerospike.skyhook.config.AerospikeContext
import io.netty.channel.ChannelHandlerContext

open class ZscanCommandListener(
    aeroCtx: AerospikeContext,
    ctx: ChannelHandlerContext
) : SscanCommandListener(aeroCtx, ctx) {

    override fun getOperation(): Operation {
        return MapOperation.getByRankRange(
            aeroCtx.bin,
            scanCommand.cursor.toInt(),
            scanCommand.COUNT.toInt(),
            MapReturnType.KEY
        )
    }
}
