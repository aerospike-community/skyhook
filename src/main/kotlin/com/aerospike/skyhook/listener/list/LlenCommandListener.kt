package com.aerospike.skyhook.listener.list

import com.aerospike.client.Key
import com.aerospike.client.Record
import com.aerospike.client.cdt.ListOperation
import com.aerospike.client.listener.RecordListener
import com.aerospike.skyhook.command.RequestCommand
import com.aerospike.skyhook.listener.BaseListener
import io.netty.channel.ChannelHandlerContext

class LlenCommandListener(
    ctx: ChannelHandlerContext
) : BaseListener(ctx), RecordListener {

    override fun handle(cmd: RequestCommand) {
        require(cmd.argCount == 2) { argValidationErrorMsg(cmd) }

        val key = createKey(cmd.key)
        val operation = ListOperation.size(aeroCtx.bin)
        client.operate(
            null, this, defaultWritePolicy,
            key, operation
        )
    }

    override fun onSuccess(key: Key?, record: Record?) {
        if (record == null) {
            writeEmptyList()
            flushCtxTransactionAware()
        } else {
            try {
                writeResponse(record.bins[aeroCtx.bin])
                flushCtxTransactionAware()
            } catch (e: Exception) {
                closeCtx(e)
            }
        }
    }
}
