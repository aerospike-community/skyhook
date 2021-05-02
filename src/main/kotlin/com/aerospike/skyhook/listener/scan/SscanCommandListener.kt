package com.aerospike.skyhook.listener.scan

import com.aerospike.client.Operation
import com.aerospike.client.cdt.MapOperation
import com.aerospike.client.cdt.MapReturnType
import com.aerospike.skyhook.config.AerospikeContext
import io.netty.channel.ChannelHandlerContext

open class SscanCommandListener(
    aeroCtx: AerospikeContext,
    ctx: ChannelHandlerContext
) : HscanCommandListener(aeroCtx, ctx) {

    override fun getOperation(): Operation {
        return MapOperation.getByIndexRange(
            aeroCtx.bin,
            scanCommand.cursor.toInt(),
            scanCommand.COUNT.toInt(),
            MapReturnType.KEY
        )
    }

    override fun writeElementsArray(list: List<*>) {
        writeObject(ctx, list)
    }
}
