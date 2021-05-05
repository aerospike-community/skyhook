package com.aerospike.skyhook.listener.key

import com.aerospike.client.*
import com.aerospike.client.listener.RecordListener
import com.aerospike.skyhook.command.RequestCommand
import com.aerospike.skyhook.listener.BaseListener
import com.aerospike.skyhook.util.Typed
import io.netty.channel.ChannelHandlerContext

class GetsetCommandListener(
    ctx: ChannelHandlerContext
) : BaseListener(ctx), RecordListener {

    override fun handle(cmd: RequestCommand) {
        require(cmd.argCount == 3) { argValidationErrorMsg(cmd) }

        val key = createKey(cmd.key)
        val value = Typed.getValue(cmd.args[2])
        val ops = arrayOf(
            Operation.get(aeroCtx.bin),
            Operation.put(Bin(aeroCtx.bin, value))
        )

        client.operate(null, this, updateOnlyPolicy, key, *ops)
    }

    override fun writeError(e: AerospikeException?) {
        writeNullString()
    }

    override fun onSuccess(key: Key?, record: Record?) {
        if (record == null) {
            writeNullString()
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
