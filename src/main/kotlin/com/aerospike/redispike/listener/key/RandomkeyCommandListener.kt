package com.aerospike.redispike.listener.key

import com.aerospike.client.Key
import com.aerospike.client.Record
import com.aerospike.client.listener.RecordSequenceListener
import com.aerospike.client.policy.ScanPolicy
import com.aerospike.redispike.command.RequestCommand
import com.aerospike.redispike.config.AerospikeContext
import com.aerospike.redispike.listener.BaseListener
import io.netty.channel.ChannelHandlerContext

class RandomkeyCommandListener(
    aeroCtx: AerospikeContext,
    ctx: ChannelHandlerContext
) : BaseListener(aeroCtx, ctx), RecordSequenceListener {

    private var isEmpty: Boolean = true

    override fun handle(cmd: RequestCommand) {
        require(cmd.argCount == 1) { argValidationErrorMsg(cmd) }

        val scanPolicy = ScanPolicy()
        scanPolicy.maxRecords = 1
        scanPolicy.includeBinData = false

        aeroCtx.client.scanAll(
            null, this, scanPolicy,
            aeroCtx.namespace, aeroCtx.set
        )
    }

    override fun onRecord(key: Key?, record: Record?) {
        key?.let {
            writeResponse(it.userKey.`object`)
            isEmpty = false
        }
    }

    override fun onSuccess() {
        if (isEmpty) writeNullString(ctx)
        ctx.flush()
    }
}
