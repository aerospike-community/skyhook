package com.aerospike.skyhook.listener.scan

import com.aerospike.client.Operation
import com.aerospike.client.cdt.MapOperation
import com.aerospike.client.cdt.MapReturnType
import io.netty.channel.ChannelHandlerContext

open class SscanCommandListener(
    ctx: ChannelHandlerContext
) : HscanCommandListener(ctx) {

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
