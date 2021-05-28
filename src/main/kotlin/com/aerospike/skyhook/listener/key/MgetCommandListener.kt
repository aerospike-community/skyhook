package com.aerospike.skyhook.listener.key

import com.aerospike.client.BatchRead
import com.aerospike.client.listener.BatchListListener
import com.aerospike.client.policy.BatchPolicy
import com.aerospike.skyhook.command.RequestCommand
import com.aerospike.skyhook.listener.BaseListener
import io.netty.channel.ChannelHandlerContext

class MgetCommandListener(
    ctx: ChannelHandlerContext
) : BaseListener(ctx), BatchListListener {

    override fun handle(cmd: RequestCommand) {
        require(cmd.argCount >= 2) { argValidationErrorMsg(cmd) }

        val keys = cmd.args.drop(1)
            .map { createKey(it) }
            .map { BatchRead(it, true) }
        client.get(null, this, BatchPolicy(defaultWritePolicy), keys)
    }

    override fun onSuccess(records: MutableList<BatchRead>?) {
        if (records == null) {
            writeNullString()
            flushCtxTransactionAware()
        } else {
            try {
                writeObjectListStr(records.mapNotNull {
                    if (it.record != null) {
                        it.record.bins[aeroCtx.bin]
                    } else null
                })
                flushCtxTransactionAware()
            } catch (e: Exception) {
                closeCtx(e)
            }
        }
    }
}
