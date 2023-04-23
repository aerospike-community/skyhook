package com.aerospike.skyhook.listener.hyperlog

import com.aerospike.client.Key
import com.aerospike.client.Record
import com.aerospike.client.listener.RecordListener
import com.aerospike.client.operation.HLLOperation
import com.aerospike.client.operation.HLLPolicy
import com.aerospike.skyhook.command.RequestCommand
import com.aerospike.skyhook.listener.BaseListener
import io.netty.channel.ChannelHandlerContext

class PfmergeListener(
    ctx: ChannelHandlerContext
) : BaseListener(ctx), RecordListener {

    override fun handle(cmd: RequestCommand) {
        require(cmd.argCount > 3) { argValidationErrorMsg(cmd) }

        val key = createKey(cmd.key)

        val hllValues = cmd.args.drop(2)
            .map(::createKey)
            .mapNotNull { client.get(null, it)}
            .map { it.getHLLValue(aeroCtx.bin) }

        val operationPut = HLLOperation.setUnion(HLLPolicy.Default, aeroCtx.bin, hllValues)

        client.operate(null, this, defaultWritePolicy, key, operationPut)
    }

    override fun onSuccess(key: Key?, record: Record?) {
        if (record == null) {
            writeNullString()
            flushCtxTransactionAware()
        } else {
            try {
                writeOK()
                flushCtxTransactionAware()
            } catch (e: Exception) {
                closeCtx(e)
            }
        }
    }
}
